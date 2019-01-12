package com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension;

import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.service.storage.restroute.vo.RestRoute;

/**
 * 负载均衡接口
 * @author chenhaiyang
 */
public interface LoadBalance {
    /**
     * 生成一个最终结果
     * @param handlerContext {@link HandlerContext}
     * @param restRoute {@link RestRoute}
     * @return host
     */
    String getOne(HandlerContext handlerContext, RestRoute restRoute);

    /**
     * 处理变更通知，主要是针对负载均衡有缓存的情况存在时
     * @param path path
     */
    void notify(String path);
}
