package com.github.chenhaiyangs.gateway.common.dto;

import lombok.Data;

/**
 * 通用响应格式
 * @author chenhaiyang
 */
@Data
public class CommonResponse<T> {
    /**
     * 网关返回响应码错误码
     */
    private String resCode="0000";
    /**
     * 网关返回错误信息
     */
    private String resMessage="请求成功";
    /**
     * 业务信息错误码
     */
    private String subCode;
    /**
     * 业务响应信息
     */
    private String subMessage;
    /**
     * 响应的业务信息
     */
    private T resData;
}
