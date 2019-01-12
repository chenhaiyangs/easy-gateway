package com.github.chenhaiyangs.gateway.exception;

import lombok.Getter;

/**
 * gateWay 网关异常
 * @author chenhaiyang
 */
public class GateWayException extends RuntimeException {
    @Getter
    private String code;
    @Getter
    private String msg;

    public GateWayException(String code, String msg) {
        super(code+":"+msg);
        this.code = code;
        this.msg = msg;
    }
}
