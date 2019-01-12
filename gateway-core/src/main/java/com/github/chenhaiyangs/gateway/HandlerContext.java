package com.github.chenhaiyangs.gateway;

import com.github.chenhaiyangs.gateway.common.config.ConfigDTO;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;

import java.util.Map;

/**
 * 网关执行上下文
 * @author chenhaiyang
 */
public interface HandlerContext {
    /**
     * 获取请求包装类
     * @return {@link AbstractRequestWapper}
     */
    AbstractRequestWapper getRequestWapper();
    /**
     * 用于在handler之间可传递的多个公共配置
     * @return {@link ConfigDTO}
     */
    Map<String,ConfigDTO> getConfigs();

    /**
     * 获取响应包装类
     * @return {@link ResponseWapper}
     */
    ResponseWapper getResponseWapper();
    /**
     * 获取Api信息
     * @return getApi
     */
    Api getApi();
}
