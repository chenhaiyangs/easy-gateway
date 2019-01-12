package com.github.chenhaiyangs.gateway.exception.handler;

import com.github.chenhaiyangs.gateway.common.constant.DefaultHttp;
import com.github.chenhaiyangs.gateway.common.dto.CommonResponse;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import com.github.chenhaiyangs.gateway.exception.GateWayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;

import java.util.HashMap;

/**
 * 全局异常处理器
 * @author chenhaiyang
 */
@Slf4j
public class ExceptionHandler {
    /**
     * 默认的错误码
     */
    private static final String DEFAULT_CODE="getaway0112";
    /**
     * 默认的错误信息
     */
    private static final String DEFAULT_MSG="后台系统异常";

    /**
     * 处理异常
     * @param e e
     * @param abstractRequestWapper 请求包装类
     * @return ResponseWapper 返回响应包装类
     */
    public ResponseWapper handler(Exception e, AbstractRequestWapper abstractRequestWapper) {

        String errorCode =DEFAULT_CODE;
        String errorMsg =DEFAULT_MSG;

        if(e instanceof GateWayException){
            errorCode=((GateWayException) e).getCode();
            errorMsg=((GateWayException) e).getMsg();
        }else{
            log.error("gateway excute error ! uri:{},err:",abstractRequestWapper.getUri(),e);
        }
        ResponseWapper responseWapper = new ResponseWapper();
        responseWapper.setHeaders(new HashMap<>(16));
        responseWapper.getHeaders().put(HttpHeaders.CONTENT_TYPE, DefaultHttp.DEFAULT_CONTENT_TYPE);
        responseWapper.setMediaType(DefaultHttp.DEFAULT_CONTENT_TYPE);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResCode(errorCode);
        commonResponse.setResMessage(errorMsg);
        String body = FastJsonUtil.toJSONString(commonResponse);

        responseWapper.setBody(body);
        return responseWapper;
    }
}
