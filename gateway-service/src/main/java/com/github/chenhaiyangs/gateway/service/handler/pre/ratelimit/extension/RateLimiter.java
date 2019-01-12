package com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension;

/**
 * 限流逻辑实现
 * @author chenhaiyang
 */
public interface RateLimiter {
    /**
     * 返回 true则通过，否则不通过
     * @param key key
     * @return ifaccquire
     */
    boolean acquire(String key);

    /**
     * 重置令牌桶
     * @param path apth
     */
    void reset(String path);
}
