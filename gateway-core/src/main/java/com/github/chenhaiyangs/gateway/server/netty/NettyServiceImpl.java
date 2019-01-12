package com.github.chenhaiyangs.gateway.server.netty;

import com.github.chenhaiyangs.gateway.GateWayExecutor;
import com.github.chenhaiyangs.gateway.HttpService;
import com.github.chenhaiyangs.gateway.server.netty.config.NettyConfig;
import com.github.chenhaiyangs.gateway.server.netty.constant.Constant;
import com.github.chenhaiyangs.gateway.server.netty.handler.HeartBeatInbondHandler;
import com.github.chenhaiyangs.gateway.server.netty.handler.HttpServiceInbondHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * 使用Netty作为http服务的容器
 * @author chenhaiyang
 */
public class NettyServiceImpl implements HttpService {
    /**
     * netty 配置
     */
    private NettyConfig nettyConfig;
    /**
     * 网关执行器
     */
    private GateWayExecutor gateWayExector;
    /**
     * bossGroup
     */
    private EventLoopGroup bossGrup;
    /**
     * workerGroup
     */
    private EventLoopGroup workerGroup;

    public NettyServiceImpl(NettyConfig nettyConfig, GateWayExecutor gateWayExector) {
        this.nettyConfig = Objects.requireNonNull(nettyConfig,"nettyConfig must not be null !");
        this.gateWayExector = Objects.requireNonNull(gateWayExector,"gatewayExecutor must not be null !");
        this.bossGrup = new NioEventLoopGroup(this.nettyConfig.getBossGroupNThread());
        this.workerGroup = new NioEventLoopGroup(this.nettyConfig.getWorkerGroupNThread());
    }

    @Override
    public void start() throws Exception{
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGrup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress (new InetSocketAddress(nettyConfig.getPort()))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(Constant.DECODER,new HttpRequestDecoder())
                                    .addLast(Constant.ENCODER,new HttpResponseEncoder())
                                    .addLast(Constant.AGGREGATOR,new HttpObjectAggregator(nettyConfig.getAggregator()*1024))
                                    .addLast(Constant.HEARTBEAT_HANDLER,new HeartBeatInbondHandler())
                                    .addLast(Constant.HTTPSERVICE_HANDLER,new HttpServiceInbondHandler(gateWayExector));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, nettyConfig.getSobackLog());
            //绑定端口，开始接收进来的连接
            ChannelFuture f = server.bind(nettyConfig.getPort()).sync();
            //等待服务器，socket 关闭
            f.channel().closeFuture().sync();
        }finally {
            stop();
        }
    }

    @Override
    public void stop() throws Exception {
        bossGrup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        gateWayExector.destory();
    }
}
