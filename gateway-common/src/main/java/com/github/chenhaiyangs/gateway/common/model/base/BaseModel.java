package com.github.chenhaiyangs.gateway.common.model.base;

import lombok.Data;

import java.util.Date;

/**
 * 公共属性集
 * @author chenhaiyang
 */
@Data
public abstract class BaseModel {
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updatetime;
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 最后更新者
     */
    private String updateBy;
}
