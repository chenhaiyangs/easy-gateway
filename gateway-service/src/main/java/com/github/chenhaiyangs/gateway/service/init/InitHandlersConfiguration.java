package com.github.chenhaiyangs.gateway.service.init;

import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.service.handler.pre.trim.TrimRequestHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.wihitelist.WhiteListHandler;
import com.github.chenhaiyangs.gateway.service.handler.runtime.sign.RequestSignHandler;
import com.github.chenhaiyangs.gateway.service.handler.runtime.time.TimeLogRuntimeHandler;
import com.github.chenhaiyangs.gateway.service.handler.runtime.token.RequestTokenHandler;
import com.github.chenhaiyangs.gateway.service.listener.DefaultInvokeInitListener;
import com.github.chenhaiyangs.gateway.service.handler.post.header.HeadersHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.blacklist.BlackListHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.blacklist.GlobalBlackListHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.mock.MockHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.RateLimitHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.restroute.RestfulLoadBalanceHandler;
import com.github.chenhaiyangs.gateway.service.handler.runtime.breaker.handler.CircuitBreakerHandler;
import com.github.chenhaiyangs.gateway.exception.SystemException;
import com.github.chenhaiyangs.gateway.GateWayExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 初始化各种Handler容器
 * @author chenhaiyang
 */
@SuppressWarnings("all")
@Configuration
public class InitHandlersConfiguration {
    //invocation
    @Resource
    private RouteInvocation routeInvocation;
    //initListener
    @Resource
    private DefaultInvokeInitListener invokeInitListener;
    //preheander
    @Resource
    private GlobalBlackListHandler globalListHandler;
    @Resource
    private BlackListHandler blackListHandler;
    @Resource
    private WhiteListHandler whiteListHandler;
    @Resource
    private RateLimitHandler rateLimitHandler;
    @Resource
    private MockHandler mockHandler;
    @Resource
    private RestfulLoadBalanceHandler loadBalanceHandler;
    @Resource
    private TrimRequestHandler trimRequestHandler;

    //runtimeHandler
    @Resource
    private RequestSignHandler requestSignHandler;
    @Resource
    private RequestTokenHandler requestTokenHandler;
    @Resource
    private CircuitBreakerHandler circuitBreakerHandler;
    @Resource
    private TimeLogRuntimeHandler timeLogRuntimeHandler;
    //post handler
    @Resource
    private HeadersHandler headersHandler;

    @Bean
    public GateWayExecutor buildGateWayExector() throws SystemException {
        return new GateWayExecutor()
                .setInvokeInitListener(invokeInitListener)
                .setRoute(routeInvocation)
                .preHandle(globalListHandler)
                .preHandle(whiteListHandler)
                .preHandle(blackListHandler)
                .preHandle(rateLimitHandler)
                .preHandle(mockHandler)
                .preHandle(loadBalanceHandler)
                .preHandle(trimRequestHandler)
                .runtimeHandle(requestTokenHandler)
                .runtimeHandle(requestSignHandler)
                .runtimeHandle(circuitBreakerHandler)
                .runtimeHandle(timeLogRuntimeHandler)
                .postHandle(headersHandler)
                .build();
    }
}
