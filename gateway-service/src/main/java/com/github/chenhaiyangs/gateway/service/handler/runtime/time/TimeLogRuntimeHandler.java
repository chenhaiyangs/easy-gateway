package com.github.chenhaiyangs.gateway.service.handler.runtime.time;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.runtime.AbstractRuntimeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 执行后端请求的调用耗时日志组件
 * @author chenhaiyang
 */
@Slf4j
@Service
public class TimeLogRuntimeHandler extends AbstractRuntimeHandler{

    @Autowired
    public TimeLogRuntimeHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @Override
    protected GateWayResponse execute0(RouteInvocation routeInvocation, HandlerContext handlerContext) throws Exception {
        long start = System.currentTimeMillis();
        GateWayResponse response = routeInvocation.invoke(handlerContext);
        long end = System.currentTimeMillis();
        log.info("请求Id：{},api:{},调用耗时：{} ms",handlerContext.getRequestWapper().getRequestId(),handlerContext.getApi(),end-start);
        return response;
    }

    @Override
    public int index() {
        return 2;
    }

    @Override
    public String getName() {
        return "timeHandler";
    }
}
