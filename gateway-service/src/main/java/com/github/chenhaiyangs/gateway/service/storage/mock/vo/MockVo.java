package com.github.chenhaiyangs.gateway.service.storage.mock.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * mock组件Vo类
 * @author chenhaiyang
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MockVo extends BaseModel{

    /**
     * id
     */
    private int id;
    /**
     * apiId
     */
    private int apiId;
    /**
     * 请求的body
     */
    private String body;
    /**
     * 是否被禁用
     */
    private boolean forbidden;
}
