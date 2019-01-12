package com.github.chenhaiyangs.gateway.service.storage;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 自己封装的一个redis服务
 * @author chenhaiyang
 */
@Component
public class RedisDataHolder {
    @Resource
    private JedisPool jedisPool;
    /**
     * 进行各种无返回值的操作
     * 比如，
     *    jes.del
     *
     * @param consumer consumer
     */
    public void operate(Consumer<Jedis> consumer){
        Jedis jedis =null;
        try {
            jedis = jedisPool.getResource();
            consumer.accept(jedis);
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 使用此方法执行各种有返回值的操作
     * 比如
     *     jedis.set(k,value);成功返回 "OK",失败返回 null
     *     jedis.set(key,value,"NX","PX",livetime);
     *     jedis.exist
     *     jedis.incr
     * @param mapper lambda
     * @param <R> 返回值类型
     */
    public <R> R getResult(Function<? super Jedis, ? extends R> mapper){
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            return mapper.apply(jedis);
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }
}
