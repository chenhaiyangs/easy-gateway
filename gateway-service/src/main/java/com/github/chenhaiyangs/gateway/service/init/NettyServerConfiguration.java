package com.github.chenhaiyangs.gateway.service.init;

import com.github.chenhaiyangs.gateway.HttpService;
import com.github.chenhaiyangs.gateway.GateWayExecutor;
import com.github.chenhaiyangs.gateway.server.netty.NettyServiceImpl;
import com.github.chenhaiyangs.gateway.server.netty.config.NettyConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Netty启动容器
 * @author chenhaiyang
 */
@Component
@Slf4j
@SuppressWarnings("all")
public class NettyServerConfiguration {

    @Resource
    private NettyConfig nettyConfig;

    @Resource
    private GateWayExecutor gateWayExector;

    private HttpService service;

    @PostConstruct
    public void init() throws Exception {
        service = new NettyServiceImpl(nettyConfig,gateWayExector);
        new Thread(()->{
            try {
                service.start();
            } catch (Exception e) {
                log.error("init httpserver error !",e);
            }
        }).start();
    }

    @PreDestroy
    public void destory() throws Exception{
        if(service!=null){
            service.stop();
        }
    }

}
