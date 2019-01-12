package com.github.chenhaiyangs.gateway.service.storage.sign.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 请求签名配置
 * @author chenhaiyang
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RequestSignConfig extends BaseModel{
    /**
     * id
     */
    private int id;
    /**
     * 被关联的api的Id
     */
    private int apiId;
    /**
     * 组件是否被禁用
     */
    private boolean forbidden;
}
