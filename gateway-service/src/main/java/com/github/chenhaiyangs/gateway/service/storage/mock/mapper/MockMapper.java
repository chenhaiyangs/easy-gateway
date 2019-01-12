package com.github.chenhaiyangs.gateway.service.storage.mock.mapper;

import com.github.chenhaiyangs.gateway.service.storage.mock.vo.MockVo;

import java.util.List;

/**
 * mock组件mapper层
 * @author chenhaiyang
 */
public interface MockMapper {
    /**
     * 查询全部信息
     * @return {@link MockVo}
     */
    List<MockVo> findAll();

    /**
     * 根据id查询mock信息
     * @param id id
     * @return {@link MockVo}
     */
    MockVo selectById(Integer id);
}
