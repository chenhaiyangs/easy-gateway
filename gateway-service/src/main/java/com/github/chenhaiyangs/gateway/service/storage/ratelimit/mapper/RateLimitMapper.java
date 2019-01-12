package com.github.chenhaiyangs.gateway.service.storage.ratelimit.mapper;

import com.github.chenhaiyangs.gateway.service.storage.ratelimit.vo.RateLimitVo;

import java.util.List;

/**
 * 限流组件Mapper
 * @author chenhaiyang
 */
public interface RateLimitMapper {
    /**
     * 查询全部
     * @return {@link RateLimitVo}
     */
    List<RateLimitVo> findAll();

    /**
     * 根据Id查询
     * @param id id
     * @return {@link RateLimitVo}
     */
    RateLimitVo selectById(Integer id);
}
