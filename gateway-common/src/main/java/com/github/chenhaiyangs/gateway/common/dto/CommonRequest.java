package com.github.chenhaiyangs.gateway.common.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 公共请求信息
 * @author chenhaiyang
 */
@Data
public class CommonRequest<T> {
    /**
     * 当前app的版本号
     */
    private String appVersion;
    /**
     * 设备类型 ios或者安卓或者网页等
     */
    private String deviceType;
    /**
     * 设备Id 表示设备唯一的id
     */
    private String deviceId;
    /**
     * 客户端时间戳
     */
    private String ts;
    /**
     * 请求的接口的版本
     */
    private String version;
    /**
     * 请求的scope，一个scope对应一个token
     */
    @JSONField(serialize = false)
    private String scope;
    /**
     * user的Token (启用了用户token认证机制时使用，面向H5)
     * 此字段不透传到后台
     */
    @JSONField(serialize = false)
    private String authToken;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 用户签名 （自己的app使用的一套签名机制。当用户登陆时申请签名，然后只要是用户登陆后才能访问的接口，一律使用该签名验证，用户登陆时获取macKey。面向app）
     * 此字段不透传到后台
     */
    @JSONField(serialize = false)
    private String sign;
    /**
     * 请求的详细报文json
     */
    private T reqData;

}
