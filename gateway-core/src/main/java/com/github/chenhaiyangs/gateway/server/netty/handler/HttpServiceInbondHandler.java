package com.github.chenhaiyangs.gateway.server.netty.handler;

import com.github.chenhaiyangs.gateway.GateWayExecutor;
import com.github.chenhaiyangs.gateway.server.netty.converter.NettyConverter;
import com.github.chenhaiyangs.gateway.server.netty.wapper.NettyRequestWapper;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * 用于处理Http请求
 * @author chenhaiyang
 */
@Slf4j
public class HttpServiceInbondHandler extends SimpleChannelInboundHandler{
    /**
     * 网关执行器
     */
    private GateWayExecutor exector;
    public HttpServiceInbondHandler(GateWayExecutor exector) {
        this.exector = exector;
    }

    /**
     * 用于处理Http请求
     * @param cht http请求上下文
     * @param msg  请求
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext cht, Object msg) throws Exception {
        if(msg instanceof HttpRequest && msg instanceof HttpContent){

            HttpRequest httpRequest = (HttpRequest) msg;
            boolean keepAlive =  HttpUtil.isKeepAlive(httpRequest);

            AbstractRequestWapper requestWapper = new NettyRequestWapper(cht,msg).buildRequestWapper();
            MDC.put("requestId",requestWapper.getRequestId());
            log.info("full httpRequest:{}",requestWapper);

            long start =System.currentTimeMillis();
            ResponseWapper responseWapper = exector.execute(requestWapper);
            long end =System.currentTimeMillis();
            log.info("网关组件执行总耗时：{} ms",end-start);

            FullHttpResponse response = NettyConverter.convertToNettyResponse(responseWapper);
            log.info("full responseWapper :{}",responseWapper);
            if(keepAlive){
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            }
            ChannelFuture future = cht.writeAndFlush(response);
            if(!keepAlive){
                future.addListener(ChannelFutureListener.CLOSE);
            }
            MDC.clear();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(null != cause){
            log.error(cause.getMessage(),cause);
        }
        if(null != ctx){
            ctx.close();
        }
    }

}
