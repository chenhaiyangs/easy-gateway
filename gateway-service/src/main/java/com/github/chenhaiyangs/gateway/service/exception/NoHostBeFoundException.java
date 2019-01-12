package com.github.chenhaiyangs.gateway.service.exception;

import com.github.chenhaiyangs.gateway.exception.GateWayException;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;

/**
 * @author chenhaiyang
 */
public class NoHostBeFoundException extends GateWayException {

    public NoHostBeFoundException() {
        super(ErrorEnum.API_NOT_AVALIABLE.getCode(),ErrorEnum.API_NOT_AVALIABLE.getMsg());
    }
}
