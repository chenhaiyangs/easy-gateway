package com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension.inmemory;

import com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension.RateLimiter;
import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 内存级别的限流
 * @author chenhaiyang
 */
public class InMemoryRateLimiter implements RateLimiter {
    /**
     * 一个api维持一个RequestRateLimiter
     */
    private RequestRateLimiter requestRateLimiter;

    public InMemoryRateLimiter( Map<Integer,Integer> ruleMap) {
        Set<RequestLimitRule> rules = new HashSet<>();
        ruleMap.forEach((k,v)->rules.add(RequestLimitRule.of(k, TimeUnit.SECONDS, v)));
        this.requestRateLimiter = new InMemorySlidingWindowRequestRateLimiter(rules);
    }

    @Override
    public boolean acquire(String key) {
        return !requestRateLimiter.overLimitWhenIncremented(key);
    }

    @Override
    public void reset(String path) {
        requestRateLimiter.resetLimit(path);
    }
}
