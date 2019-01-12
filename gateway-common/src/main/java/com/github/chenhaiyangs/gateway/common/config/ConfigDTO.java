package com.github.chenhaiyangs.gateway.common.config;

import lombok.Data;

/**
 * 一些组件的特殊配置
 * 存储一些通用配置，和handler之间的上下文配置
 * @author chenhaiyang
 */
@Data
public class ConfigDTO {
    /**
     * value
     */
    private String value;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 是否必须
     */
    private Boolean required;
}
