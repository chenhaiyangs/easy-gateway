package com.github.chenhaiyangs.gateway.service.storage.ratelimit.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 限流实现方案
 * @author chenhaiyang
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RateLimitVo extends BaseModel{
    /**
     * 主键Id
     */
    @Getter
    @Setter
    private int id;
    /**
     * apiId
     */
    @Getter
    @Setter
    private int apiId;

    /**
     * api的具体限流配置
     */
    @Getter
    private String limitBody;
    /**
     * limitbody转成map。对于每一个api，可以配置多个限流测试，每k秒只允许v个请求
     */
    @Getter
    private Map<Integer,Integer> limitMap;
    /**
     * 限流类型 1，redis实现分布式限流，2单机限流
     */
    @Getter
    @Setter
    private int type;
    /**
     * 是否被禁用
     */
    @Getter
    @Setter
    private boolean forbidden;

    @SuppressWarnings("unused")
    public void setLimitBody(String limitBody) {
        this.limitBody = limitBody;
        this.limitMap = FastJsonUtil.toIntegerKeyValueMap(limitBody);
    }

    /**
     * 限流实现方式
     */
    public enum RateLimitType{
        /**
         * 使用redis实现分布式限流
         */
        REDIS(1),
        /**
         * 本地限流
         */
        LOCAL(2);

        @Getter
        private int value;
        RateLimitType(int value) {
            this.value = value;
        }
    }
}
