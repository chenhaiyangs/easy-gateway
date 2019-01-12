package com.github.chenhaiyangs.gateway.service.handler.runtime.breaker.handler;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.concurrent.GateWayThreadFactory;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.runtime.AbstractRuntimeHandler;
import com.github.chenhaiyangs.gateway.service.storage.breaker.BreakerStorage;
import com.github.chenhaiyangs.gateway.service.storage.breaker.vo.CurcuitBeaker;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.netflix.hystrix.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 熔断组件
 * @author chenhaiyang
 */
@Slf4j
@Service
public class CircuitBreakerHandler extends AbstractRuntimeHandler {

    /**
     * Netty workerGroupThread
     */
    @SuppressWarnings("all")
    @Value("#{gatawayBasic['workerGroupNThread']}")
    private int workerGroupNThread;

    /**
     * 脱离信号量实现超时控制，维护一个和Netty工作线程池大小一样的线程池
     */
    private ExecutorService taskThreadPool;

    @Resource
    private BreakerStorage breakerStorage;

    @Autowired
    public CircuitBreakerHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @PostConstruct
    public void init(){
        taskThreadPool= new ThreadPoolExecutor(workerGroupNThread, workerGroupNThread,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),new GateWayThreadFactory());
    }

    @Override
    public GateWayResponse execute0(RouteInvocation routeInvocation,HandlerContext handlerContext) throws Exception {

        AbstractRequestWapper request = handlerContext.getRequestWapper();
        Api api = handlerContext.getApi();
        CurcuitBeaker curcuitBeaker = breakerStorage.getBreakerByApiId(api.getId());
        log.info("api:{},组件名：{}，请求Id:{},curcuitBeaker:{}",api,this.getName(),request.getRequestId(),curcuitBeaker);
        if(curcuitBeaker==null || curcuitBeaker.isForbidden()){
            return routeInvocation.invoke(handlerContext);
        }
        CircuitHystrixCommand hystrixCommand = new CircuitHystrixCommand(handlerContext,api,curcuitBeaker,routeInvocation);
        return hystrixCommand.execute();
    }

    @Override
    public int index() {
        return 1;
    }

    @Override
    public String getName() {
        return "circuitBreakerHandler";
    }

    @Override
    public void destory() {
        taskThreadPool.shutdown();
    }

    /**
     * 熔断链路执行器
     */
    private class CircuitHystrixCommand extends HystrixCommand<GateWayResponse>{

        private RouteInvocation routeInvocation;
        private HandlerContext handlerContext;
        private CurcuitBeaker curcuitBeaker;

        private CircuitHystrixCommand(HandlerContext handlerContext,Api api,CurcuitBeaker curcuitBeaker, RouteInvocation routeInvocation) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(api.getPath()))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(api.getPath()))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            // 舱壁隔离策略 - 信号量
                            .withExecutionIsolationStrategy (HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                            // 设置每组 command 可以申请的信号量最大数（一个服务可以申请的最大的信号量）
                            .withExecutionIsolationSemaphoreMaxConcurrentRequests(curcuitBeaker.getLimit())
                            //允许调用线程最大的fullback并发数量
                            .withFallbackIsolationSemaphoreMaxConcurrentRequests(500)
                            //是否开启熔断机制
                            .withCircuitBreakerEnabled(true)
                            //如果当前的错误率小于%n，则不进行熔断
                            .withCircuitBreakerErrorThresholdPercentage(curcuitBeaker.getPercent())
                            //是否强制关闭熔断器开关
                            .withCircuitBreakerForceClosed(curcuitBeaker.isForceClose())
                            //短路多久后尝试进行恢复。默认5秒放过一个请求进行尝试（单位：ms）
                            .withCircuitBreakerSleepWindowInMilliseconds(curcuitBeaker.getRetriesTime())
                            //执行超时时间（单位：ms）
                            .withExecutionTimeoutInMilliseconds(curcuitBeaker.getTimeout())
                            //启用超时管控
                            .withExecutionTimeoutEnabled(true)));

            this.routeInvocation=routeInvocation;
            this.handlerContext=handlerContext;
            this.curcuitBeaker=curcuitBeaker;
        }

        @Override
        protected GateWayResponse getFallback() {

            Throwable exception = getFailedExecutionException();
            if (null == exception) {
                List<HystrixEventType> executionEvents = getExecutionEvents();
                String cause = Optional.ofNullable(executionEvents)
                        .map(List::stream)
                        .orElseGet(Stream::empty)
                        .map(HystrixEventType::name)
                        .collect(Collectors.toList())
                        .toString();

                log.error("后端服务连续异常后熔断：cause:{},requestId:{}",cause,handlerContext.getRequestWapper().getRequestId());

            }else if(exception instanceof TimeoutException){

                log.error("后端服务调用超时：requestId:{}",handlerContext.getRequestWapper().getRequestId());

                return new GateWayResponse(
                        ErrorEnum.REQUEST_TIME_OUT.getCode(),
                        ErrorEnum.REQUEST_TIME_OUT.getMsg());
            }else{
                log.error("后端服务调用异常：requestId:{}",handlerContext.getRequestWapper().getRequestId(),exception);
            }
            return new GateWayResponse(
                    ErrorEnum.REQUEST_ERROR.getCode(),
                    ErrorEnum.REQUEST_ERROR.getMsg());
        }

        @Override
        protected GateWayResponse run() throws Exception {
            Future<GateWayResponse> executeFuture =taskThreadPool.submit(()->routeInvocation.invoke(handlerContext));
            return executeFuture.get(curcuitBeaker.getTimeout(), TimeUnit.MILLISECONDS);
        }
    }
}
