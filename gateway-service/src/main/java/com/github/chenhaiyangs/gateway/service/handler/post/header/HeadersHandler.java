package com.github.chenhaiyangs.gateway.service.handler.post.header;

import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.post.AbstractPostHandler;
import com.github.chenhaiyangs.gateway.service.storage.header.HeadersStorage;
import com.github.chenhaiyangs.gateway.service.storage.header.vo.ResponseHeader;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 是否给响应体中添加指定的各种额外的headers
 * @author chenhaiyang
 */
@Slf4j
@Service
public class HeadersHandler extends AbstractPostHandler{

    @Resource
    private HeadersStorage headersStorage;

    @Autowired
    public HeadersHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @Override
    public void iHandle(HandlerContext handlerContext) {
        AbstractRequestWapper request = handlerContext.getRequestWapper();
        Api api = handlerContext.getApi();
        ResponseHeader responseHeader = headersStorage.getResponseHeaderByApiId(api.getId());
        log.info("api:{},组件名：{}，请求Id:{},responseHeader:{}",api,this.getName(),request.getRequestId(),responseHeader);
        if(responseHeader!=null && !responseHeader.isForbidden()){
            handlerContext.getResponseWapper().getHeaders().putAll(responseHeader.getHeadersMap());
        }
    }

    @Override
    public int index() {
        return 0;
    }

    @Override
    public String getName() {
        return "headersHandler";
    }
}
