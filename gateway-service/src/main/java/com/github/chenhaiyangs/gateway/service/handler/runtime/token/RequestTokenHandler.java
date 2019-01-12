package com.github.chenhaiyangs.gateway.service.handler.runtime.token;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.common.dto.CommonRequest;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.runtime.AbstractRuntimeHandler;
import com.github.chenhaiyangs.gateway.service.handler.runtime.token.jwt.JwtVertify;
import com.github.chenhaiyangs.gateway.service.storage.RedisDataHolder;
import com.github.chenhaiyangs.gateway.service.storage.token.TokenConfigStorage;
import com.github.chenhaiyangs.gateway.service.storage.token.vo.RequestTokenConfig;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 请求token组件
 * h5或者小程序鉴权时使用
 * @author chenhaiyang
 */
@Slf4j
@Service
public class RequestTokenHandler extends AbstractRuntimeHandler{
    /**
     * 存储用户token的前缀
     */
    private static final String TOKEN_CACHE_BY_USER="gateway_user_token_id_";
    /**
     * tokenkey分隔符
     */
    private static final String SEPARATE="_";
    /**
     * 在响应字段中添加的authToken
     */
    private static final String AUTH_TOKEN="authToken";

    @Resource
    private TokenConfigStorage tokenConfigStorage;
    @Resource
    private RedisDataHolder redisDataHolder;

    @Autowired
    public RequestTokenHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @Override
    protected GateWayResponse execute0(RouteInvocation routeInvocation, HandlerContext handlerContext) throws Exception {

        Api api = handlerContext.getApi();
        RequestTokenConfig requestTokenConfig = tokenConfigStorage.getRequestTokenConfigByApiId(api.getId());
        //如果token组件禁用，则直接透传请求
        if(requestTokenConfig==null || requestTokenConfig.isForbidden()){
            return routeInvocation.invoke(handlerContext);
        }
        AbstractRequestWapper request = handlerContext.getRequestWapper();
        String postBody = handlerContext.getRequestWapper().getPostBody();
        CommonRequest commonRequest = FastJsonUtil.toJSONObject(postBody, CommonRequest.class);

        //如果传递的scope不合法，不在api所属的scope集中，禁止申请token
        if((!api.getScopeList().contains(commonRequest.getScope()))
                || StringUtils.isBlank(commonRequest.getUserId())){

            return new GateWayResponse(
                    ErrorEnum.SCOPE_ERROR.getCode(),
                    ErrorEnum.SCOPE_ERROR.getMsg());
        }

        //非loginApi验token，loginApi在响应中添加token
        if(!api.isLoginApi()) {

            String token = redisDataHolder.getResult(jedis ->
                    jedis.get(TOKEN_CACHE_BY_USER
                            .concat(commonRequest.getScope())
                            .concat(SEPARATE)
                            .concat(commonRequest.getUserId())));

            if((!vertifyToken(token,commonRequest))){

                log.info("请求Id:{},api:{},token鉴权失败",request.getRequestId(),api);
                return new GateWayResponse(
                        ErrorEnum.TOKEN_HANDLER.getCode(),
                        ErrorEnum.TOKEN_HANDLER.getMsg());
            }
            return routeInvocation.invoke(handlerContext);
        }else {

            GateWayResponse gateWayResponse =  routeInvocation.invoke(handlerContext);
            if(gateWayResponse.isPassed()) {

                String token =JwtVertify.createJwt(commonRequest.getUserId(),requestTokenConfig.getExpire());
                redisDataHolder.operate(jedis ->
                        jedis.setex(TOKEN_CACHE_BY_USER
                                .concat(commonRequest.getScope())
                                .concat(SEPARATE)
                                .concat(commonRequest.getUserId()),
                                requestTokenConfig.getExpire(),token));

                String responseBody = gateWayResponse.getResponseWapper().getBody();
                Map<String,Object> responseMap = FastJsonUtil.parseToObjectMap(responseBody);
                responseMap.put(AUTH_TOKEN,token);
                gateWayResponse.getResponseWapper().setBody(FastJsonUtil.toJSONString(responseMap));

            }
            return gateWayResponse;
        }
    }

    /**
     * 验证token
     * @param token token
     * @param commonRequest request
     * @return boolean
     */
    private boolean vertifyToken(String token, CommonRequest commonRequest) {
        return token!=null
                && token.equals(commonRequest.getAuthToken())
                && JwtVertify.verifyJwt(token,commonRequest.getUserId());
    }

    @Override
    public int index() {
        return 0;
    }

    @Override
    public String getName() {
        return "requestTokenHandler";
    }
}
