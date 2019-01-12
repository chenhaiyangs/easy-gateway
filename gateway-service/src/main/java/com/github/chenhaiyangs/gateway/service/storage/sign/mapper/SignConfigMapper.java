package com.github.chenhaiyangs.gateway.service.storage.sign.mapper;

import com.github.chenhaiyangs.gateway.service.storage.sign.vo.RequestSignConfig;

import java.util.List;

/**
 * signConfigMapper
 * @author chenhaiyang
 */
public interface SignConfigMapper {
    /**
     * 查询全部
     * @return {@link RequestSignConfig}
     */
    List<RequestSignConfig> findAll();

    /**
     * 根据Id查询
     * @param id id
     * @return {@link RequestSignConfig}
     */
    RequestSignConfig selectById(Integer id);
}
