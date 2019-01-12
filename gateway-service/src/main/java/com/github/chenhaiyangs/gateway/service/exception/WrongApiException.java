package com.github.chenhaiyangs.gateway.service.exception;

import com.github.chenhaiyangs.gateway.exception.GateWayException;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;

/**
 * 错误的api请求
 * @author chenhaiyang
 */
public class WrongApiException extends GateWayException {

    public WrongApiException() {
        super(ErrorEnum.WRONG_API_EXCEPTION.getCode(),
                ErrorEnum.WRONG_API_EXCEPTION.getMsg());
    }
}
