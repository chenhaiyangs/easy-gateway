package com.github.chenhaiyangs.gateway.service.init;

import com.github.chenhaiyangs.gateway.RouteInvocation;
import com.github.chenhaiyangs.gateway.invocation.rest.RestfulRouteInvocation;
import com.github.chenhaiyangs.gateway.invocation.rest.httpclient.RestTemplate;
import com.github.chenhaiyangs.gateway.server.netty.config.NettyConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化网关core相关的配置信息集
 * @author chenhaiyang
 */
@Configuration
@SuppressWarnings("all")
public class GatewayConfiguration {

    /**
     * netty port
     */
    @Value("#{gatawayBasic['port']}")
    private int port;
    /**
     * Netty sobackLog
     */
    @Value("#{gatawayBasic['sobackLog']}")
    private int sobackLog;
    /**
     * Netty aggregator
     */
    @Value("#{gatawayBasic['aggregator']}")
    private int aggregator;
    /**
     * netty bossGroupThread
     */
    @Value("#{gatawayBasic['bossGroupNThread']}")
    private int bossGroupNThread;
    /**
     * Netty workerGroupThread
     */
    @Value("#{gatawayBasic['workerGroupNThread']}")
    private int workerGroupNThread;
    /**
     * httpclient httpReadtimeout
     */
    @Value("#{gatawayBasic['httpReadtimeout']}")
    private int httpReadtimeout;
    /**
     * Http请求连接超时时间
     */
    @Value("#{gatawayBasic['httpConntimeout']}")
    private int httpConntimeout;
    /**
     * httpclient 连接池最大的连接数
     */
    @Value("#{gatawayBasic['httpMaxConnection']}")
    private int httpMaxConnection;
    /**
     * 每一个路由默认的最大连接数
     */
    @Value("#{gatawayBasic['httpdefaultMaxPerRoute']}")
    private int httpdefaultMaxPerRoute;

    @Bean
    public NettyConfig buildNettyConfig(){
        return NettyConfig.builder()
                .port(port)
                .sobackLog(sobackLog)
                .aggregator(aggregator)
                .bossGroupNThread(bossGroupNThread)
                .workerGroupNThread(workerGroupNThread)
                .build();
    }

    @Bean
    public RouteInvocation buildRouteInvocation(){
        RestTemplate template = new RestTemplate()
                .timeout(httpConntimeout,httpReadtimeout)
                .maxTotal(httpMaxConnection)
                .setDefaultMaxPerRoute(httpdefaultMaxPerRoute)
                .build();
        return new RestfulRouteInvocation(template);
    }
}
