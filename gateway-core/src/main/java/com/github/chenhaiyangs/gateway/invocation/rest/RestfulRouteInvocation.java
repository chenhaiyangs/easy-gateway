package com.github.chenhaiyangs.gateway.invocation.rest;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.common.config.ConfigDTO;
import com.github.chenhaiyangs.gateway.common.constant.DefaultHttp;
import com.github.chenhaiyangs.gateway.common.dto.CommonResponse;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import com.github.chenhaiyangs.gateway.invocation.rest.httpclient.RestTemplate;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * httpExecutor 执行http请求的网关执行器
 * @author chenhaiyang
 */
@Slf4j
public class RestfulRouteInvocation implements RouteInvocation{

    /**
     * 最终路由出来的Http请求结果的key ,格式为 ip:port
     */
    public static final String UNIQUE_HTTP_IP_POTE="routerResult";
    /**
     * rest调用模版
     */
    private RestTemplate restTemplate;
    public RestfulRouteInvocation(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GateWayResponse invoke(HandlerContext handlerContext) throws IOException {

        AbstractRequestWapper requestWapper=handlerContext.getRequestWapper();
        Map<String,ConfigDTO> configs = handlerContext.getConfigs();

        try {
            String url = buildUniqueUrl(requestWapper, configs);
            ResponseWapper responseWapper = restTemplate.restPost(url, requestWapper.getSrcHeaders(), requestWapper.getPostResultBody());
            responseWapper.setUri(requestWapper.getUri());

            //下面这两行代码实现将响应转化为标准响应
            CommonResponse commonResponse = FastJsonUtil.toJSONObject(responseWapper.getBody(),CommonResponse.class);
            responseWapper.setBody(FastJsonUtil.toJSONString(commonResponse));

            GateWayResponse response = new GateWayResponse();
            response.setResponseWapper(responseWapper);
            return response;
        }catch (Exception e){
            log.error("restful routeInvocation invoke err !",e);
            throw e;
        }
    }

    @Override
    public void destory() {
        restTemplate.destory();
    }

    /**
     * 构造最终版本的config
     * @param abstractRequestWapper 请求
     * @param configs configs配置类
     * @return 返回结果
     */
    private String buildUniqueUrl(AbstractRequestWapper abstractRequestWapper, Map<String, ConfigDTO> configs) {
        return String.format("%s://%s%s", DefaultHttp.DEFAULT_PROTOCOL,configs.get(UNIQUE_HTTP_IP_POTE).getValue(),abstractRequestWapper.getUri());
    }
}
