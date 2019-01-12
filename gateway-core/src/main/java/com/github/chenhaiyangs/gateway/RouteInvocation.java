package com.github.chenhaiyangs.gateway;

/**
 * 路由组件
 * @author chenhaiyang
 */
public interface RouteInvocation {
    /**
     * 执行具体的下游服务的路由
     * @param handlerContext handlerContext
     * @return {@link GateWayResponse}
     * @throws Exception 异常信息
     */
    GateWayResponse invoke(HandlerContext handlerContext) throws Exception;
    /**
     * 销毁资源
     */
    void destory();
}
