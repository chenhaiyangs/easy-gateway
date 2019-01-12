package com.github.chenhaiyangs.gateway.server.netty.converter;

import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

/**
 * 负责将通用的请求转化为Netty的converter
 * @author chenhaiyang
 */
public class NettyConverter {
    /**
     * 将 responseWapper 转化为FullHttpResponse
     * @param responseWapper  {@link ResponseWapper}
     * @return {@link FullHttpResponse}
     * @throws UnsupportedEncodingException exception
     */
    public static FullHttpResponse convertToNettyResponse(ResponseWapper responseWapper) throws UnsupportedEncodingException {
        HttpResponseStatus status = HttpResponseStatus.valueOf(responseWapper.getStatusCode());
        FullHttpResponse response =  new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(responseWapper.getBody().getBytes("UTF-8")));
        responseWapper.getHeaders().forEach((k,v)->response.headers().add(k,v));

        return response;
    }

}
