package com.github.chenhaiyangs.gateway;
/**
 * 网关执行器-后置处理器，当调用成功后，附加的功能。主要是用来装饰http响应等内容
 * @author chenhaiyang
 */
public interface PostHandler {

    /**
     * 网关执行逻辑
     * @param handlerContext handler上下文，{@link HandlerContext}
     */
    void handle(HandlerContext handlerContext);

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
