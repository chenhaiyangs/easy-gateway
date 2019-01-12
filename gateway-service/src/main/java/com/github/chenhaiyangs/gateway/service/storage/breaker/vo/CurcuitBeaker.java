package com.github.chenhaiyangs.gateway.service.storage.breaker.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 网关的熔断器vo
 * @author chenhaiyang
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CurcuitBeaker extends BaseModel{
    /**
     * id
     */
    private int id;
    /**
     * 关联的api的Id
     */
    private int apiId;
    /**
     * 每一个api的最大信号量
     */
    private int limit;
    /**
     * 当错误率小于 %percent，则不进行熔断
     */
    private int percent;
    /**
     * 是否开启强制关闭容器功能
     */
    private boolean forceClose;
    /**
     * 如果已经触发了熔断，那么，多久以后放一个请求过去（单位：ms）默认为五秒
     */
    private int retriesTime;
    /**
     * api的超时时间
     */
    private int timeout;
    /**
     * 组件是否被禁用
     */
    private boolean forbidden;
}
