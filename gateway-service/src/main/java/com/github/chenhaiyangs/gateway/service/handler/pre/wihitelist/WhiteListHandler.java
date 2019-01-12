package com.github.chenhaiyangs.gateway.service.handler.pre.wihitelist;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.pre.AbstractPreHandler;
import com.github.chenhaiyangs.gateway.service.storage.whitelist.IpWhiteListStorage;
import com.github.chenhaiyangs.gateway.service.storage.whitelist.vo.IpWhiteList;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 白名单前置处理器，配置了白名单前置处理器的api，只能ip在白名单中的请求才会通过
 * @author chenhaiyang
 */
@Slf4j
@Service
public class WhiteListHandler extends AbstractPreHandler{

    @Resource
    private IpWhiteListStorage ipWhiteListStorage;

    @Autowired
    public WhiteListHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @Override
    public GateWayResponse iHandle(HandlerContext handlerContext) {
        AbstractRequestWapper request = handlerContext.getRequestWapper();
        Api api = handlerContext.getApi();
        IpWhiteList ipWhiteList = ipWhiteListStorage.getIpWhiteList(api.getId());
        log.info("api:{},组件名：{}，请求Id:{},ipWhiteList:{}",api,this.getName(),request.getRequestId(),ipWhiteList);
        /*
         * 该组件禁用或者该api没有该组件，直接放行请求
         */
        if(ipWhiteList==null ||
                ipWhiteList.isForbidden()){

            return new GateWayResponse();
        }
        //如ip在ip白名单，且value为true,即生效，则放行请求
        if(ipWhiteList.getWhitelistMap().containsKey(request.getClientIp()) &&
                ipWhiteList.getWhitelistMap().get(request.getClientIp())){

            return new GateWayResponse();
            //否则，拦截请求
        }else{
            log.warn("请求Id:{},该请求被ip白名单组件拦截",request.getRequestId());
            return new GateWayResponse(
                    ErrorEnum.WHITE_LIST_HANDLER.getCode(),
                    ErrorEnum.WHITE_LIST_HANDLER.getMsg());
        }

    }

    @Override
    public int index() {
        return 2;
    }

    @Override
    public String getName() {
        return "whitelistHandler";
    }
}
