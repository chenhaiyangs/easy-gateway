package com.github.chenhaiyangs.gateway.service.storage.blacklist.mapper;

import com.github.chenhaiyangs.gateway.service.storage.blacklist.vo.IpBlackList;

import java.util.List;

/**
 * api的ip黑名单列表
 * @author chenhaiyang
 */
public interface IpBlackListMapper {
    /**
     * 查询全部
     * @return {@link IpBlackList}
     */
    List<IpBlackList> findAll();

    /**
     * 根据id查询
     * @param id id
     * @return {@link IpBlackList}
     */
    IpBlackList selectById(Integer id);
}
