package com.github.chenhaiyangs.gateway.service.storage.header.mapper;

import com.github.chenhaiyangs.gateway.service.storage.header.vo.ResponseHeader;

import java.util.List;

/**
 * @author chenhaiyang
 */
public interface ResponseHeadersMapper {
    /**
     * 查询全部
     * @return {@link ResponseHeader}
     */
    List<ResponseHeader> findAll();

    /**
     * 根据Id查询
     * @param id 主键
     * @return @return {@link ResponseHeader}
     */
    ResponseHeader selectById(Integer id);
}
