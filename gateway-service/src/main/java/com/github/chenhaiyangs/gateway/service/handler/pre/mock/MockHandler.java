package com.github.chenhaiyangs.gateway.service.handler.pre.mock;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.pre.AbstractPreHandler;
import com.github.chenhaiyangs.gateway.service.storage.mock.MockStorage;
import com.github.chenhaiyangs.gateway.service.storage.mock.vo.MockVo;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 是否mock后端的接口Handler。如果mock，则返回mock的假数据
 * @author chenhaiyang
 */
@Slf4j
@Service
public class MockHandler extends AbstractPreHandler{

    @Resource
    private MockStorage mockStorage;

    @Autowired
    public MockHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @Override
    public GateWayResponse iHandle(HandlerContext handlerContext) {

        AbstractRequestWapper request = handlerContext.getRequestWapper();
        Api api = handlerContext.getApi();
        MockVo mockVo = mockStorage.getMockByApiId(api.getId());

        if(mockVo==null || mockVo.isForbidden()|| !FastJsonUtil.isJson(mockVo.getBody())) {
            return new GateWayResponse();
        }
        log.warn("请求Id:{},该请求开启了mock，返回mock结果",request.getRequestId());
        return new GateWayResponse(false,mockVo.getBody());
    }

    @Override
    public int index() {
        return 5;
    }

    @Override
    public String getName() {
        return "mockHandler";
    }
}
