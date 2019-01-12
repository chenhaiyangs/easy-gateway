package com.github.chenhaiyangs.gateway.service.handler.runtime.breaker;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hystrix 熔断器的动态配置实现
 * @author chenhaiyang
 */
@Configuration
@SuppressWarnings("all")
public class HytrixConfiguration {

    @Bean
    public DynamicConfiguration dynamicConfiguration(DynamicConfigSource configource) {
        DynamicConfiguration configuration = new DynamicConfiguration(configource, new FixedDelayPollingScheduler(30 * 1000, 60 * 1000, false));
        // 安裝后会启动schedel,定时调用DynamicConfigSource.poll()更新配置
        ConfigurationManager.install(configuration);
        return configuration;
    }
}
