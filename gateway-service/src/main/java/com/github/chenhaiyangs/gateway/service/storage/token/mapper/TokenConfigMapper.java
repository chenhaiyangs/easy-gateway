package com.github.chenhaiyangs.gateway.service.storage.token.mapper;

import com.github.chenhaiyangs.gateway.service.storage.token.vo.RequestTokenConfig;

import java.util.List;

/**
 * token配置存储层
 * @author chenhaiyang
 */
public interface TokenConfigMapper {
    /**
     * 查询全部
     * @return {@link RequestTokenConfig}
     */
    List<RequestTokenConfig> findAll();

    /**
     * 根据Id查询
     * @param id id
     * @return {@link RequestTokenConfig}
     */
    RequestTokenConfig selectById(Integer id);
}
