package com.github.chenhaiyangs.gateway.service.handler.pre.blacklist;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.service.constant.CommonHandlerConstant;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.pre.AbstractPreHandler;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 全局黑名单拦截管理器，和api无关，拦截全量的api,只要ip地址不对就会拦截
 * @author chenhaiyang
 */
@Slf4j
@Service
public class GlobalBlackListHandler extends AbstractPreHandler {
    /**
     * 全局黑名单
     */
    @Resource(name="blacklistGroup")
    private AbstractGeneralConfigGroup blackListGroup;

    @Autowired
    public GlobalBlackListHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }


    @Override
    public GateWayResponse iHandle(HandlerContext handlerContext) {

        AbstractRequestWapper request = handlerContext.getRequestWapper();
        if(blackListGroup.containsKey(request.getClientIp())
                && blackListGroup.get(request.getClientIp()).equals(CommonHandlerConstant.TRUE)){

            log.warn("请求Id:{},该请求在全局ip黑名单,已拦截",request.getRequestId());
            return new GateWayResponse(
                    ErrorEnum.BLACK_LIST_HANDLER.getCode(),
                    ErrorEnum.BLACK_LIST_HANDLER.getMsg());
        }
        return new GateWayResponse();
    }

    @Override
    public int index() {
        return 0;
    }

    @Override
    public String getName() {
        return "globalBlackListHandler";
    }
}
