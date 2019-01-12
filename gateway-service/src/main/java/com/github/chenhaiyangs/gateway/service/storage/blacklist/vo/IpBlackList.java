package com.github.chenhaiyangs.gateway.service.storage.blacklist.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 黑名单 vo
 * @author chenhaiyang
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IpBlackList extends BaseModel{
    /**
     * id
     */
    @Setter
    @Getter
    private int id;
    /**
     * apiId
     */
    @Setter
    @Getter
    private int apiId;
    /**
     * blackList
     */
    @Getter
    private String blackList;
    /**
     * blackListMap
     */
    @Getter
    private Map<String,Boolean> blackListMap;
    /**
     * 是否被禁用
     */
    @Setter
    @Getter
    private boolean forbidden;

    /**
     * setBlackList
     * @param blackList 黑名单列表字符串
     */
    @SuppressWarnings("unused")
    public void setBlackList(String blackList) {
        this.blackList = blackList;
        this.blackListMap = FastJsonUtil.toBooleanValueMap(blackList);
    }

}
