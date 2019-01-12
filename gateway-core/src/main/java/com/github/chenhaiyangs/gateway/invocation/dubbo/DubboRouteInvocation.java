package com.github.chenhaiyangs.gateway.invocation.dubbo;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.RouteInvocation;

/**
 * dubboRoute路由组件,待实现
 * @author chenhaiyang
 */
public class DubboRouteInvocation implements RouteInvocation {
    @Override
    public GateWayResponse invoke(HandlerContext handlerContext) throws Exception {
        return new GateWayResponse();
    }

    @Override
    public void destory() {

    }
}
