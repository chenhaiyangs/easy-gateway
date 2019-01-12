package com.github.chenhaiyangs.gateway.server.netty.config;

import lombok.Builder;
import lombok.Data;

/**
 * Netty容器启动时需要的配置
 * @author chenhaiyang
 */
@Data
@Builder()
public class NettyConfig {
    /**
     * 端口
     */
    private int port;
    /**
     * sobacklog
     */
    private int sobackLog=1024;
    /**
     * 聚合http请求
     */
    private int aggregator=1000;
    /**
     * 处理io任务的线程数
     */
    private int bossGroupNThread=Runtime.getRuntime().availableProcessors()*2;
    /**
     * 处理工作任务的线程数
     */
    private int workerGroupNThread=1500;
}
