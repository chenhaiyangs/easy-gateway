package com.github.chenhaiyangs.gateway.service.handler.runtime.sign;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.common.dto.CommonRequest;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.runtime.AbstractRuntimeHandler;
import com.github.chenhaiyangs.gateway.service.handler.runtime.sign.mac.Hmac;
import com.github.chenhaiyangs.gateway.service.storage.RedisDataHolder;
import com.github.chenhaiyangs.gateway.service.storage.sign.SignConfigStorage;
import com.github.chenhaiyangs.gateway.service.storage.sign.vo.RequestSignConfig;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 认证鉴权的handler
 * 验证mac
 * @author chenhaiyang
 */
@Slf4j
@Service
public class RequestSignHandler extends AbstractRuntimeHandler{
    /**
     * 网关中存储用户mackey的key前缀
     */
    private static final String SIGN_CACHE_BY_USER="gateway_user_mackey_id_";
    /**
     * 在响应字段中添加的signKey
     */
    private static final String SING_KEY="signKey";

    @Resource
    private RedisDataHolder redisDataHolder;
    @Resource
    private SignConfigStorage signConfigStorage;

    @Autowired
    public RequestSignHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @Override
    protected GateWayResponse execute0(RouteInvocation routeInvocation, HandlerContext handlerContext) throws Exception {

        Api api = handlerContext.getApi();
        RequestSignConfig requestSignConfig =  signConfigStorage.getRequestSignByApiId(api.getId());
        //如果签名组件禁用，则直接透传请求
        if(requestSignConfig==null || requestSignConfig.isForbidden()){
            return routeInvocation.invoke(handlerContext);
        }
        AbstractRequestWapper request = handlerContext.getRequestWapper();
        String postBody = handlerContext.getRequestWapper().getPostBody();
        Map<String, String> result = FastJsonUtil.parseToJSONObject(postBody);
        CommonRequest commonRequest = FastJsonUtil.toJSONObject(postBody, CommonRequest.class);

        if(StringUtils.isBlank(commonRequest.getUserId())){

            return new GateWayResponse(
                    ErrorEnum.USER_ID_NULL.getCode(),
                    ErrorEnum.USER_ID_NULL.getMsg());
        }

        //非loginApi验签，loginApi在响应中添加签名Key
        if(!api.isLoginApi()) {

            String macKey = redisDataHolder.getResult(jedis -> jedis.get(SIGN_CACHE_BY_USER.concat(commonRequest.getUserId())));

            int signRequest = Hmac.verifyHmac(macKey, result, commonRequest.getSign());
            if (Hmac.VertifyType.PASSED.getValue() != signRequest) {
                log.error("请求Id:{},userId:{},请求签名未通过!", request.getRequestId(), commonRequest.getUserId());
                return new GateWayResponse(
                        ErrorEnum.SING_HANDLER.getCode(),
                        ErrorEnum.SING_HANDLER.getMsg());
            }
            return routeInvocation.invoke(handlerContext);
        }else {

            GateWayResponse gateWayResponse =  routeInvocation.invoke(handlerContext);
            if(gateWayResponse.isPassed()) {

                String signKey =Hmac.getNewHmacKey(commonRequest.getUserId());
                redisDataHolder.operate(jedis -> jedis.set(SIGN_CACHE_BY_USER.concat(commonRequest.getUserId()),signKey));
                String responseBody = gateWayResponse.getResponseWapper().getBody();
                Map<String,Object> responseMap = FastJsonUtil.parseToObjectMap(responseBody);
                responseMap.put(SING_KEY,signKey);
                gateWayResponse.getResponseWapper().setBody(FastJsonUtil.toJSONString(responseMap));

            }
            return gateWayResponse;
        }
    }

    @Override
    public int index() {
        return 0;
    }

    @Override
    public String getName() {
        return "authHandler";
    }
}
