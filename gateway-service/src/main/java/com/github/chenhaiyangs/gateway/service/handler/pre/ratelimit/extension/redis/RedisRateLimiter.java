package com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension.redis;

import com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension.RateLimiter;
import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.core.limiter.request.RequestRateLimiter;
import es.moki.ratelimitj.redis.request.RedisRateLimiterFactory;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author chenhaiyang
 * 包装
 */
public class RedisRateLimiter implements RateLimiter {
    /**
     * 一个api维持一个RequestRateLimiter
     */
    private RequestRateLimiter requestRateLimiter;

    public RedisRateLimiter(String host,int port,String password, Map<Integer,Integer> ruleMap) {
        RedisURI redisURI = RedisURI.Builder
                .redis(host, port)
                .build();
        if(password!=null && password.length()>0) {
            redisURI.setPassword(password);
        }

        RedisRateLimiterFactory factory = new RedisRateLimiterFactory(RedisClient.create(redisURI));
        Set<RequestLimitRule> rules = new HashSet<>();
        ruleMap.forEach((k,v)->rules.add(RequestLimitRule.of(k,TimeUnit.SECONDS, v)));
        this.requestRateLimiter = factory.getInstance(rules);
    }

    @Override
    public boolean acquire(String key){
        return !requestRateLimiter.overLimitWhenIncremented(key);
    }

    @Override
    public void reset(String path) {
        requestRateLimiter.resetLimit(path);
    }
}
