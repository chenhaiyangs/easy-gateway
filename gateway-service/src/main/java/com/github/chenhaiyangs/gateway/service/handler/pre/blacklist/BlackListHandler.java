package com.github.chenhaiyangs.gateway.service.handler.pre.blacklist;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.pre.AbstractPreHandler;
import com.github.chenhaiyangs.gateway.service.storage.blacklist.IpBlackListStorage;
import com.github.chenhaiyangs.gateway.service.storage.blacklist.vo.IpBlackList;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 黑白名单处理器
 * @author chenhaiyang
 */
@Slf4j
@Service
public class BlackListHandler extends AbstractPreHandler {

    @Resource
    private IpBlackListStorage ipBlackListStorage;

    @Autowired
    public BlackListHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @Override
    public GateWayResponse iHandle(HandlerContext handlerContext) {

        AbstractRequestWapper request = handlerContext.getRequestWapper();
        Api api = handlerContext.getApi();
        IpBlackList ipBlackList = ipBlackListStorage.getIpBlackList(api.getId());
        log.info("api:{},组件名：{}，请求Id:{},ipBlackList:{}",api,this.getName(),request.getRequestId(),ipBlackList);
        /*
         * 该api的黑名单组件禁用，直接放行请求
         */
        if(ipBlackList==null || ipBlackList.isForbidden()){
            return new GateWayResponse();
        }
        if(ipBlackList.getBlackListMap().containsKey(request.getClientIp()) &&
                ipBlackList.getBlackListMap().get(request.getClientIp())){

            log.warn("请求Id:{},该请求被黑名单组件拦截",request.getRequestId());
            return new GateWayResponse(
                    ErrorEnum.BLACK_LIST_HANDLER.getCode(),
                    ErrorEnum.BLACK_LIST_HANDLER.getMsg());
        }
        return new GateWayResponse();
    }

    @Override
    public int index() {
        return 1;
    }

    @Override
    public String getName() {
        return "blackListHandler";
    }
}
