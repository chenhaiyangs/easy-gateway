package com.github.chenhaiyangs.gateway.service.handler.pre;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.PreHandler;
import com.github.chenhaiyangs.gateway.service.constant.CommonHandlerConstant;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import lombok.extern.slf4j.Slf4j;

/**
 * 实现自prehandler,主要实现公共组件禁用集的功能
 * @author chenhaiyang
 */
@Slf4j
public abstract class AbstractPreHandler implements PreHandler{

    /**
     * 全局禁用组件集
     */
    private HandlerDisabledCompment handlerDisabledCompment;
    public AbstractPreHandler(HandlerDisabledCompment handlerDisabledCompment) {
        this.handlerDisabledCompment = handlerDisabledCompment;
    }

    @Override
    public GateWayResponse handle(HandlerContext handlerContext) {
        if(handlerDisabledCompment.containsKey(this.getName())
                && handlerDisabledCompment.get(this.getName()).equals(CommonHandlerConstant.TRUE)){

            log.warn("组件name:{},全局禁用，此请求放行",this.getName());
            return new GateWayResponse();
        }
        return iHandle(handlerContext);
    }

    @Override
    public void destory() {

    }

    /**
     * 执行具体的网关前置逻辑
     * @param handlerContext {@link HandlerContext}
     * @return {@link GateWayResponse}
     */
    protected abstract GateWayResponse iHandle(HandlerContext handlerContext);
}
