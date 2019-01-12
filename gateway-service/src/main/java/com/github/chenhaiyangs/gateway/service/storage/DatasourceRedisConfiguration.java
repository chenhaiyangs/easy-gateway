package com.github.chenhaiyangs.gateway.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis的配置
 * @author chenhaiyang
 */
@Configuration
@SuppressWarnings("all")
public class DatasourceRedisConfiguration {

    /**
     * redis_host
     */
    @Value("#{redisGroup['redis.host']}")
    private String redisHost;
    /**
     * redis_port
     */
    @Value("#{redisGroup['redis.port']}")
    private int port;
    /**
     * redis_password
     */
    @Value("#{redisGroup['redis.password']}")
    private String password;
    /**
     * connecttimeout
     */
    @Value("#{redisGroup['redis.timeout']}")
    private int timout;
    /**
     * redis的最大空闲连接
     */
    @Value("#{redisGroup['redis.pool.maxIdle']}")
    private int maxIdle;
    /**
     * redis的最大连接数
     */
    @Value("#{redisGroup['redis.pool.maxTotal']}")
    private int maxTotal;
    /**
     * redis建立连接的最大等待时间
     */
    @Value("#{redisGroup['redis.pool.maxWaitMillis']}")
    private int maxWaitMillis;
    /**
     * redis初始化连接数
     */
    @Value("#{redisGroup['redis.pool.minIdle']}")
    private int minIdle;
    /**
     * 对拿到的链接做初始化校验
     */
    @Value("#{redisGroup['redis.pool.testOnBorrow']}")
    private boolean testOnBorrow;
    /**
     * 在归还链接的时候做初始化校验
     */
    @Value("#{redisGroup['redis.pool.testOnReturn']}")
    private boolean testOnReturn;
    /**
     * 定期对连接池的空闲线程做校验
     */
    @Value("#{redisGroup['redis.pool.testWhileIdle']}")
    private boolean testWhileIdle;

    @Bean
    public JedisPoolConfig getJedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setTestWhileIdle(testWhileIdle);
        return jedisPoolConfig;
    }

    @Bean
    public JedisPool getJedisPool(JedisPoolConfig jedisPoolConfig){
        if(password==null || password.length()==0){
            return new JedisPool(jedisPoolConfig, redisHost,port,timout);
        }
        return new JedisPool(jedisPoolConfig, redisHost,port,timout,password);
    }
}
