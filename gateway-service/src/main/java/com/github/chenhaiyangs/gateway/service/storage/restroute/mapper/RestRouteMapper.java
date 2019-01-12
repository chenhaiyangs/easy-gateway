package com.github.chenhaiyangs.gateway.service.storage.restroute.mapper;

import com.github.chenhaiyangs.gateway.service.storage.restroute.vo.RestRoute;

import java.util.List;

/**
 * rest路由存储层
 * @author chenhaiyang
 */
public interface RestRouteMapper {
    /**
     * 查询全部
     * @return {@link RestRoute}
     */
    List<RestRoute> findAll();

    /**
     * 根据Id查询
     * @param id id
     * @return {@link RestRoute}
     */
    RestRoute selectById(Integer id);
}
