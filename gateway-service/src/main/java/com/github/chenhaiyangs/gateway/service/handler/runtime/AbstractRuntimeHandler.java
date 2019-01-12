package com.github.chenhaiyangs.gateway.service.handler.runtime;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.RuntimeHandler;
import com.github.chenhaiyangs.gateway.service.constant.CommonHandlerConstant;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import lombok.extern.slf4j.Slf4j;

/**
 * 实现自RuntimeHandler,主要实现公共组件禁用集的功能
 * @author chenhaiyang
 */
@Slf4j
public abstract class AbstractRuntimeHandler implements RuntimeHandler{
    /**
     * 全局禁用组件集
     */
    private HandlerDisabledCompment handlerDisabledCompment;

    public AbstractRuntimeHandler(HandlerDisabledCompment handlerDisabledCompment) {
        this.handlerDisabledCompment = handlerDisabledCompment;
    }
    @Override
    public GateWayResponse execute(RouteInvocation routeInvocation, HandlerContext handlerContext) throws Exception {
        if(handlerDisabledCompment.containsKey(this.getName())
                && handlerDisabledCompment.get(this.getName()).equals(CommonHandlerConstant.TRUE)){

            log.warn("组件name:{},全局禁用，此请求放行",this.getName());
            return new GateWayResponse();
        }
        return execute0(routeInvocation,handlerContext);
    }

    @Override
    public void destory() {

    }

    /**
     * 执行具体的网关逻辑执行器
     * @param routeInvocation 具体的路由组件
     * @param handlerContext handler上下文
     * @return {@link GateWayResponse}
     * @throws Exception IO异常
     */
    protected abstract GateWayResponse execute0(RouteInvocation routeInvocation, HandlerContext handlerContext) throws Exception;

}
