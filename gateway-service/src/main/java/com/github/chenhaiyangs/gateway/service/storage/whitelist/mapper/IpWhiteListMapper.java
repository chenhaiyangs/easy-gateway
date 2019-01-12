package com.github.chenhaiyangs.gateway.service.storage.whitelist.mapper;

import com.github.chenhaiyangs.gateway.service.storage.whitelist.vo.IpWhiteList;

import java.util.List;

/**
 * Ip白名单
 * @author chenhaiyang
 */
public interface IpWhiteListMapper {
    /**
     * 查询全部的ip白名单信息
     * @return {@link IpWhiteList}
     */
    List<IpWhiteList> findAll();

    /**
     * 根据id查询api的ip白名单信息
     * @param id 主键Id
     * @return {@link IpWhiteList}
     */
    IpWhiteList selectById(Integer id);
}
