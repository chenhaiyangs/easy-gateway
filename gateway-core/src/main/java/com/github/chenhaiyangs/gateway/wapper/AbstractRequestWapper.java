package com.github.chenhaiyangs.gateway.wapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * request请求封装类
 * @author chenhaiyang
 */
@ToString
public abstract class AbstractRequestWapper {

    protected static final String MEDIA_KEY="content-type";
    /**
     * 路径的uri
     */
    @Getter
    protected String uri;
    /**
     * 请求Id
     */
    @Getter
    protected String requestId;
    /**
     * 客户端Ip地址
     */
    @Getter
    protected String clientIp;
    /**
     * 请求方法 post get
     */
    @Getter
    protected String httpMethod;

    /**
     * 请求媒体类型 例如：application-json
     */
    @Getter
    protected String mediaType;
    /**
     * 请求头，key转为小写的
     */
    @Getter
    protected Map<String,String> headers;

    /**
     * 原始请求头，不对key的大小写进行转换
     */
    @Getter
    protected Map<String,String> srcHeaders;
    /**
     * 如果是原生Post请求，请求体 仅支持application-json,application-xml等格式
     * 也就是请求体
     */
    @Getter
    protected String postBody;
    /**
     * 网关会对结果进行渲染，这个是网关最终响应给后台的请求体
     */
    @Setter
    @Getter
    protected String  postResultBody;
    /**
     * 如果是get 请求,则拿到get请求的请求体，全部转成String类型
     */
    @Getter
    protected Map<String,String> getParams;

    /**
     * 构造requstWapper
     * @return 返回requestWapper
     */
    protected abstract AbstractRequestWapper buildRequestWapper();
}
