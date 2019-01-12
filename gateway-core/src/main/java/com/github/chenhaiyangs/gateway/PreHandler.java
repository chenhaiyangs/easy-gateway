package com.github.chenhaiyangs.gateway;

/**
 * 网关执行器-前置执行器，用于处理拦截
 * @author chenhaiyang
 */
public interface PreHandler {
    /**
     * 网关执行逻辑
     * @param handlerContext handler上下文
     * @return {@link GateWayResponse}
     */
    GateWayResponse handle(HandlerContext handlerContext);
    /**
     * index index决定了网关的执行顺序
     * @return 获取handler 排序
     */
    int index();
    /**
     * 返回组件名称
     * @return 返回结果
     */
    String getName();
    /**
     * 销毁组件所持有的资源
     */
    void destory();
}
