package com.github.chenhaiyangs.gateway.server.netty.wapper;

import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 基于Netty的实现类，构造通用的httpRequest
 * @author chenhaiyang
 */
public class NettyRequestWapper extends AbstractRequestWapper {
    /**
     * 用户获取客户端ip的请求头
     */
    private static final String HTTPHEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HTTPHEADER_X_REAL_IP="X-Real-IP";
    /**
     * 未知ip地址头
     */
    private static final String HTTPIP_UNKNOWN="unknown";

    private Object msg;
    private ChannelHandlerContext cht;

    public NettyRequestWapper(ChannelHandlerContext cht, Object msg){
        this.cht=cht;
        this.msg=msg;
    }

    @Override
    public AbstractRequestWapper buildRequestWapper() {
        HttpRequest httpRequest = (HttpRequest) msg;

        this.uri=httpRequest.uri();
        this.requestId= String.format("%s-%s",uri, UUID.randomUUID().toString().replaceAll("-",""));
        this.httpMethod = httpRequest.method().name().toLowerCase();

        HttpHeaders headers =  httpRequest.headers();

        buildHeaders(headers,true);
        buildHeaders(headers,false);

        this.mediaType =headers.get(MEDIA_KEY);

        if(HttpMethod.POST.name().toLowerCase().equals(this.httpMethod)){

            HttpContent content = (HttpContent) msg;
            this.postBody=content.content().toString(CharsetUtil.UTF_8);
            this.postResultBody=this.postBody;
        }
        if(HttpMethod.GET.name().toLowerCase().equals(this.httpMethod)){
            buildGetParams();
        }
        buildClientIp();
        return this;

    }

    /**
     * 构建http get 请求的参数
     */
    private void buildGetParams() {
        Map<String, String> requestParams=new HashMap<>(16);
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> params = decoder.parameters();
        params.forEach((k,v)->requestParams.put(k,v.get(0)));
        this.getParams=requestParams;
    }

    /**
     * 构建headers
     * @param headers headers
     * @param toLower 是否转化为小写
     */
    private void buildHeaders(HttpHeaders headers,boolean toLower) {
        Map<String,String> stringHeaders = new HashMap<>(16);
        if(toLower){
            headers.forEach(entry ->stringHeaders.put(entry.getKey().toLowerCase(),entry.getValue()));
            this.headers=stringHeaders;
        }else {
            headers.forEach(entry ->stringHeaders.put(entry.getKey(),entry.getValue()));
            this.srcHeaders=stringHeaders;
            this.srcHeaders.put("requestId",requestId);
        }
    }

    /**
     * 生成客户端ip地址
     */
    private void buildClientIp() {
        if (msg instanceof HttpRequest) {
            HttpRequest mReq = (HttpRequest) msg;
            String clientIp = mReq.headers().get(HTTPHEADER_X_FORWARDED_FOR);
            if (clientIp == null||clientIp.length()==0||HTTPIP_UNKNOWN.equalsIgnoreCase(clientIp)) {
                clientIp = mReq.headers().get(HTTPHEADER_X_REAL_IP);
            }
            if(clientIp==null||clientIp.length()==0||HTTPIP_UNKNOWN.equalsIgnoreCase(clientIp)){
                InetSocketAddress insocket = (InetSocketAddress) cht.channel()
                        .remoteAddress();
                clientIp = insocket.getAddress().getHostAddress();
            }
            this.clientIp=clientIp;
        }
    }
}
