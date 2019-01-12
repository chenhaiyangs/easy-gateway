package com.github.chenhaiyangs.gateway.service.storage.breaker.mapper;

import com.github.chenhaiyangs.gateway.service.storage.breaker.vo.CurcuitBeaker;

import java.util.List;

/**
 * 熔断器存储层
 * @author chenhaiyang
 */
public interface CurcuitBeakerMapper {
    /**
     * 查询全部熔断器
     * @return {@link CurcuitBeaker}
     */
    List<CurcuitBeaker> findAll();

    /**
     * 根据Id查询
     * @param id id
     * @return {@link CurcuitBeaker}
     */
    CurcuitBeaker selectById(Integer id);
}
