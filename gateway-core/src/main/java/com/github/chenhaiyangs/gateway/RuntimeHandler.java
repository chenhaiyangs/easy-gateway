package com.github.chenhaiyangs.gateway;

/**
 * 运行时handler 处理器
 * 用于处理调用前和后的一些事件
 * @author chenhaiyang
 */
public interface RuntimeHandler {

    /**
     * 网关运行时执行器
     * @param routeInvocation 具体的路由组件
     * @param handlerContext handler上下文
     * @return {@link GateWayResponse}
     * @throws Exception IO异常
     */
    GateWayResponse execute(RouteInvocation routeInvocation,HandlerContext handlerContext) throws Exception;
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
