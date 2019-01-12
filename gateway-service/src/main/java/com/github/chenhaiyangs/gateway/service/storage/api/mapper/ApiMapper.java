package com.github.chenhaiyangs.gateway.service.storage.api.mapper;

import com.github.chenhaiyangs.gateway.common.model.Api;

import java.util.List;

/**
 * api查询列表
 * @author chenhaiyang
 */
public interface ApiMapper {
    /**
     * 查询全部
     * @return {@link Api}
     */
    List<Api> findAll();

    /**
     * 根据apiId 查询api
     * @param apiId apiId
     * @return {@link Api}
     */
    Api selectApiById(Integer apiId);
}
