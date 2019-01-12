package com.github.chenhaiyangs.gateway.service.storage;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * zookeeper客户端
 * @author chenhaiyang
 */
@Configuration
@SuppressWarnings("all")
public class DatasourceZookeeperConfiguration {
    /**
     * zookeeper地址
     */
    @Value("#{gatewayConfig['zk.address']}")
    private String zookeeperUrl;

    @Bean
    public CuratorFramework getCuratorFramework(){
        return CuratorFrameworkFactory.newClient(zookeeperUrl,new ExponentialBackoffRetry(1000, 3));
    }
}
