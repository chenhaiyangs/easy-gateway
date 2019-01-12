package com.github.chenhaiyangs.gateway.service.storage.whitelist.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * ip白名单vo
 * @author chenhaiyang
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IpWhiteList extends BaseModel{

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
     * whitelist
     */
    @Getter
    private String whitelist;
    /**
     * whitelistMap
     */
    @Getter
    private Map<String,Boolean> whitelistMap;
    /**
     * 是否被禁用
     */
    @Setter
    @Getter
    private boolean forbidden;

    /**
     * setWhiteList
     * @param whitelist 白名单列表字符串
     */
    @SuppressWarnings("unused")
    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
        this.whitelistMap = FastJsonUtil.toBooleanValueMap(whitelist);
    }
}
