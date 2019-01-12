package com.github.chenhaiyangs.gateway;

import com.github.chenhaiyangs.gateway.common.constant.DefaultHttp;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.exception.SystemException;
import com.github.chenhaiyangs.gateway.exception.handler.ExceptionHandler;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 网关执行器-分别执行前置处理器和后置处理器
 * 生成全局单例的网关执行器。依此执行拦截
 * @author chenhaiyang
 */
@Slf4j
public class GateWayExecutor {
    /**
     * 一系列的前置网关执行器,{@link PreHandler}
     */
    private List<PreHandler> prehandlers = new ArrayList<>();
    /**
     * 一系列的后置网关执行器{@link PostHandler}
     */
    private List<PostHandler> postHandlers = new ArrayList<>();
    /**
     * 一系列的运行时网关执行器 {@link RuntimeHandler}
     */
    private List<RuntimeHandler> runtimeHandlers = new ArrayList<>();

    /**
     * GateWayExecutor会调用 {@link InvokeInitListener} 的setApi方法给{@link HandlerContext} 设置一个合适的API对象
     */
    private InvokeInitListener invokeInitListener;
    /**
     * 服务发送请求组件，可以是dubbo或者http
     * 但最后该内容的赋值不是原始{@link #setRoute}函数赋值的结果。而是经由{@link #decorateRouteInvocation}函数组装而成的handler链
     */
    private RouteInvocation routeInvocation;
    /**
     * 全局异常处理器 {@link ExceptionHandler}
     */
    private ExceptionHandler exceptionHandler = new ExceptionHandler();

    /**
     * 最终的请求发送组件
     * @param routeInvocation  请求发送组件
     * @return {@link GateWayExecutor}
     */
    public GateWayExecutor setRoute(RouteInvocation routeInvocation) throws SystemException {
        if(this.routeInvocation==null) {
            this.routeInvocation = routeInvocation;
            return this;
        }
        throw new SystemException("can't setRoute after build() is called !");
    }

    /**
     * 设置invkeInitListener
     * @param invokeInitListener {@link InvokeInitListener}
     */
    public GateWayExecutor setInvokeInitListener(InvokeInitListener invokeInitListener) {
        this.invokeInitListener = invokeInitListener;
        return this;
    }

    /**
     * 顺序添加API网关前置执行器，添加的网关按照顺序执行
     * @param handler handler
     * @return {@link GateWayExecutor}
     */
    public GateWayExecutor preHandle(PreHandler handler){
        prehandlers.add(handler);
        return this;
    }
    /**
     * 顺序添加API网关后置执行器，添加的网关按照顺序执行
     * @param handler handler
     * @return {@link GateWayExecutor}
     */
    public GateWayExecutor postHandle(PostHandler handler){
        postHandlers.add(handler);
        return this;
    }

    /**
     * 顺序添加运行时网关
     * @param runtimeHandler 运行时网关
     * @return {@link GateWayExecutor}
     */
    public GateWayExecutor runtimeHandle(RuntimeHandler runtimeHandler){
        runtimeHandlers.add(runtimeHandler);
        return this;
    }

    public GateWayExecutor build(){

        prehandlers = prehandlers.stream()
                .sorted(Comparator.comparing(PreHandler::index))
                .collect(Collectors.toList());

        postHandlers = postHandlers.stream()
                .sorted(Comparator.comparing(PostHandler::index))
                .collect(Collectors.toList());

        runtimeHandlers=runtimeHandlers.stream()
                .sorted(Comparator.comparing(RuntimeHandler::index))
                .collect(Collectors.toList());

        Objects.requireNonNull(invokeInitListener,"InvokeInitListener must not be null!");
        Objects.requireNonNull(routeInvocation,"RouteInvocation must not be null!");

        decorateRouteInvocation();
        return this;
    }

    /**
     * 组装Runtimehandler链
     */
    private void decorateRouteInvocation() {

        RouteInvocation last = this.routeInvocation;
        RouteInvocation src =  this.routeInvocation;

        for (int i = runtimeHandlers.size() - 1; i >= 0; i--) {

            final RuntimeHandler handler = runtimeHandlers.get(i);
            final RouteInvocation next = last;

            last = new RouteInvocation() {
                @Override
                public GateWayResponse invoke(HandlerContext handlerContext) throws Exception {
                    return handler.execute(next,handlerContext);
                }

                @Override
                public void destory() {
                    src.destory();
                }
            };
        }
        this.routeInvocation=last;
    }

    /**
     * 依次执行网关结果
     * @param abstractRequestWapper 通用请求类
     * @return {@link ResponseWapper}
     */
    public ResponseWapper execute(AbstractRequestWapper abstractRequestWapper) {

        DefaultHandlerContext handlerContext = new DefaultHandlerContext();
        handlerContext.setAbstractRequestWapper(abstractRequestWapper);

        try{
            Api api = invokeInitListener.setApi(abstractRequestWapper);
            handlerContext.setApi(api);

            /*
             * 执行前置处理器runtimeHandler，只要有执行未通过的组件立即转移到后置处理器
             */
            for(PreHandler handler :prehandlers){
                GateWayResponse response = handler.handle(handlerContext);
                if(!response.isPassed()){
                    return processPostHandler(response,handlerContext);
                }
            }
            /*
             * 执行运行时处理器RuntimeHandler,只要有失败的立刻转移到后置处理器
             */
            GateWayResponse response= routeInvocation.invoke(handlerContext);
            if(!response.isPassed()){
               return processPostHandler(response,handlerContext);
            }

            ResponseWapper responseWapper=response.getResponseWapper();
            handlerContext.setResponseWapper(responseWapper);
            processPostHandler(handlerContext);

            return responseWapper;

        }catch (Exception e){
            log.error(e.getMessage(),e);
            return exceptionHandler.handler(e,abstractRequestWapper);
        }
    }

    private ResponseWapper processPostHandler(GateWayResponse gateWayResponse,DefaultHandlerContext handlerContext){
        ResponseWapper responseWapperRuntime = convertResponseWithGateWayResponse(gateWayResponse,handlerContext.getRequestWapper());
        handlerContext.setResponseWapper(responseWapperRuntime);
        processPostHandler(handlerContext);
        return responseWapperRuntime;
    }
    /**
     * 运行一系列的后置处理器
     * @param handlerContext handler上下文
     */
    private void processPostHandler(HandlerContext handlerContext) {
        for(PostHandler postHandler :postHandlers){
            postHandler.handle(handlerContext);
        }
    }

    /**
     * 销毁所持有的资源
     */
    public void destory(){
        prehandlers.forEach(PreHandler::destory);
        runtimeHandlers.forEach(RuntimeHandler::destory);
        postHandlers.forEach(PostHandler::destory);
        routeInvocation.destory();
    }

    /**
     * 将执行结果解析成responseWapper
     * @param response 响应
     * @param request 请求包装类
     * @return {@link ResponseWapper}
     */
    private ResponseWapper convertResponseWithGateWayResponse(GateWayResponse response, AbstractRequestWapper request) {

        ResponseWapper responseWapper = response.getResponseWapper();
        responseWapper.setUri(request.getUri());
        responseWapper.setBody(response.getBody());
        responseWapper.setMediaType(DefaultHttp.DEFAULT_CONTENT_TYPE);
        responseWapper.getHeaders().put(HttpHeaders.CONTENT_TYPE,DefaultHttp.DEFAULT_CONTENT_TYPE);

        return responseWapper;
    }
}
