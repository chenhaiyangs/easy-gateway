package com.github.chenhaiyangs.gateway.service.handler.pre.trim;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.PreHandler;
import com.github.chenhaiyangs.gateway.common.dto.CommonRequest;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 对请求做加工，删除那些和业务无关的字段，如sign和authToken等
 * 此前置组件一定要放在前置组件的最后执行
 * @author chenhaiyang
 */
@Slf4j
@Service
public class TrimRequestHandler implements PreHandler{

    @Override
    public GateWayResponse handle(HandlerContext handlerContext) {

        AbstractRequestWapper request = handlerContext.getRequestWapper();
        String postBody = handlerContext.getRequestWapper().getPostBody();
        CommonRequest commonRequest = FastJsonUtil.toJSONObject(postBody,CommonRequest.class);
        String result = FastJsonUtil.toJSONString(commonRequest);
        request.setPostResultBody(result);

        return new GateWayResponse();
    }

    @Override
    public int index() {
        return 1000;
    }

    @Override
    public String getName() {
        return "trimRequestHandler";
    }

    @Override
    public void destory() {

    }
}
