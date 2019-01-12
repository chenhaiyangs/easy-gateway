package com.github.chenhaiyangs.gateway.service.handler.post;

import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.PostHandler;
import com.github.chenhaiyangs.gateway.service.constant.CommonHandlerConstant;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import lombok.extern.slf4j.Slf4j;

/**
 * 实现自PostHandler,主要实现公共组件禁用集的功能
 * @author chenhaiyang
 */
@Slf4j
public abstract class AbstractPostHandler implements PostHandler{
    /**
     * 全局禁用组件集
     */
    private HandlerDisabledCompment handlerDisabledCompment;
    public AbstractPostHandler(HandlerDisabledCompment handlerDisabledCompment) {
        this.handlerDisabledCompment = handlerDisabledCompment;
    }

    @Override
    public void handle(HandlerContext handlerContext) {

        if(handlerDisabledCompment.containsKey(this.getName())
                && handlerDisabledCompment.get(this.getName()).equals(CommonHandlerConstant.TRUE)){

            log.warn("组件name:{},全局禁用，此请求放行",this.getName());
            return;
        }
        iHandle(handlerContext);
    }

    @Override
    public void destory() {

    }

    /**
     * 执行具体的网关后置处理器
     * @param handlerContext 网关执行逻辑上下文
     */
    protected abstract void iHandle(HandlerContext handlerContext);
}
