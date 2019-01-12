package com.github.chenhaiyangs.gateway;

import com.github.chenhaiyangs.gateway.common.config.ConfigDTO;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import lombok.AccessLevel;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的handler上下文
 * @author chenhaiyang
 */
public class DefaultHandlerContext implements HandlerContext {
    /**
     * 请求的配置上下文
     */
    private Map<String, ConfigDTO> configs = new ConcurrentHashMap<>();
    /**
     * 请求包装类
     */
    @Setter(AccessLevel.PACKAGE)
    private AbstractRequestWapper abstractRequestWapper;
    /**
     * 响应包装类
     */
    @Setter(AccessLevel.PACKAGE)
    private ResponseWapper responseWapper;
    /**
     * 设置API
     */
    @Setter(AccessLevel.PACKAGE)
    private Api api;

    @Override
    public AbstractRequestWapper getRequestWapper() {
        return abstractRequestWapper;
    }

    @Override
    public Map<String, ConfigDTO> getConfigs() {
        return configs;
    }

    @Override
    public ResponseWapper getResponseWapper() {
        return responseWapper;
    }

    @Override
    public Api getApi() {
        return this.api;
    }
}
