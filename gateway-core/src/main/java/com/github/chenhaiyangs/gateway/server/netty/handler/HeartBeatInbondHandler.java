package com.github.chenhaiyangs.gateway.server.netty.handler;

import com.github.chenhaiyangs.gateway.common.constant.DefaultHttp;
import com.github.chenhaiyangs.gateway.server.netty.converter.NettyConverter;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于处理健康检查的handler
 * @author chenhaiyang
 */
@Slf4j
public class HeartBeatInbondHandler extends ChannelInboundHandlerAdapter {
    /**
     * 处理http的健康检查，一般检查检查的路径为/
     */
    private static final String URI = "/";
    /**
     * 用于处理健康检查Http请求
     * @param ctx http请求上下文
     * @param msg  请求
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest && msg instanceof HttpContent){

            HttpRequest httpRequest = (HttpRequest) msg;
            String uri = httpRequest.uri();

            if (uri.equals(URI)){
                try{

                    ResponseWapper responseWapper = new ResponseWapper();
                    responseWapper.setUri(uri);
                    responseWapper.setBody("{}");
                    responseWapper.setMediaType(DefaultHttp.DEFAULT_CONTENT_TYPE);
                    responseWapper.getHeaders().put(org.apache.http.HttpHeaders.CONTENT_TYPE,DefaultHttp.DEFAULT_CONTENT_TYPE);
                    FullHttpResponse response = NettyConverter.convertToNettyResponse(responseWapper);
                    log.info("full responseWapper :{}",responseWapper);

                    ChannelFuture future = ctx.writeAndFlush(response);
                    future.addListener(ChannelFutureListener.CLOSE);
                }finally {
                    ReferenceCountUtil.release(msg);
                }
            }else {
                ctx.fireChannelRead(msg);
            }
        }
    }
}
