package com.github.chenhaiyangs.gateway.service.error;

import lombok.Getter;

/**
 * 网关Handler错误信息枚举
 * @author chenhaiyang
 */
public enum ErrorEnum {
    /**
     * 黑名单系统拦截
     */
    BLACK_LIST_HANDLER("1001","您在系统黑名单，请联系管理员解封"),
    /**
     * api白名单
     */
    WHITE_LIST_HANDLER("1002","您没有权限访问该api"),
    /**
     * 请求被限流
     */
    RATE_LIMIT_HANDLER("1003","请求频繁，请稍后再试"),
    /**
     * 签名失败
     */
    SING_HANDLER("1004","签名失败，请重新登陆"),
    /**
     * token无效
     */
    TOKEN_HANDLER("1009","token无效，请重新登陆"),
    /**
     * 请求超时
     */
    REQUEST_TIME_OUT("1005","请求超时"),
    /**
     * 请求异常
     */
    REQUEST_ERROR("1006","请求异常"),
    /**
     * api不可用
     */
    API_NOT_AVALIABLE("1007","该api不可用"),
    /**
     * 不存在的api
     */
    WRONG_API_EXCEPTION("1008","不存在的api"),
    /**
     * scope不合法
     */
    SCOPE_ERROR("1010","您传递的scope不合法或userId为空"),
    /**
     * userId不允许为空
     */
    USER_ID_NULL("1011","userId不允许为空！");

    /**
     * 错误code
     */
    @Getter
    private String code;
    /**
     * 错误msg
     */
    @Getter
    private String msg;
    ErrorEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
