package com.github.chenhaiyangs.gateway.service.storage.token.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 请求token配置
 * @author chenhaiyang
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RequestTokenConfig extends BaseModel{
    /**
     * id
     */
    private int id;
    /**
     * 被关联的api的Id
     */
    private int apiId;
    /**
     * 超时时间：单位／秒
     */
    private int expire;
    /**
     * 组件是否被禁用
     */
    private boolean forbidden;
}
