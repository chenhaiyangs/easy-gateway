package com.github.chenhaiyangs.gateway.invocation.rest.httpclient;

import com.github.chenhaiyangs.gateway.common.constant.DefaultHttp;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * http 请求连接管理
 * @author chenhaiyang
 */
@Slf4j
public class RestTemplate {

    private PoolingHttpClientConnectionManager poolManager = new PoolingHttpClientConnectionManager();
    /**
     * 禁用http重试
     */
    private HttpRequestRetryHandler requestRetryHandler = new DefaultHttpRequestRetryHandler(0, false);

    /**
     * 定时清理无效的连接
     */
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, (ThreadFactory) Thread::new);

    /**
     * http 连接超时时间
     */
    private RequestConfig defaultRequestConfig;
    /**
     * http Client 连接
     */
    private CloseableHttpClient httpClient;

    /**
     * 设置连接池的最大连接数
     * @param maxTotal 最大连接数
     * @return this
     */
    public RestTemplate maxTotal(int maxTotal){
        poolManager.setMaxTotal(maxTotal);
        return this;
    }

    /**
     * 设置的每个路由的默认最大连接数
     * @param defaultMaxPerRoute 默认每个路由的默认最大连接数
     * @return this
     */
    public RestTemplate setDefaultMaxPerRoute(int defaultMaxPerRoute){
        poolManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return this;
    }

    /**
     * 设置http 请求的连接超时时间和读取超时时间
     * @param connectTimeout 连接超时时间
     * @param readTimeout 读取超时时间
     * @return this
     */
    public RestTemplate timeout(int connectTimeout, int readTimeout){
        defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(readTimeout)
                .build();

        return this;
    }

    /**
     * 构造对象 RestTemplate
     * @return this
     */
    public RestTemplate build(){
        httpClient=HttpClients.custom()
                .setConnectionManager(poolManager)
                .setConnectionManagerShared(true)
                .setRetryHandler(requestRetryHandler)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();

        executorService.scheduleAtFixedRate(()->{
                poolManager.closeExpiredConnections();
                poolManager.closeIdleConnections(60, TimeUnit.SECONDS);
        },0,5, TimeUnit.SECONDS);
        return this;
    }

    /**
     * 销毁资源
     */
    public void destory(){
        try {
            httpClient.close();
            poolManager.close();
            executorService.shutdown();
        } catch (Exception e) {
            log.error("restroute template destory error ! ",e);
        }
    }

    /**
     * 转发post 请求
     * @param url 请求路径
     * @param headers 请求头
     * @param body 请求体
     */
    public ResponseWapper restPost(String url, Map<String,String> headers, String body) throws IOException {

        HttpPost httpPost=null;
        HttpResponse response=null;
        try{
            headers.remove(HttpHeaders.CONTENT_LENGTH);
            headers.remove(HttpHeaders.CONTENT_LENGTH.toLowerCase());

            httpPost = new HttpPost(url);
            httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
            headers.forEach(httpPost::addHeader);

            httpPost.setEntity(new StringEntity(body, Charset.forName("UTF-8")));

            response = httpClient.execute(httpPost);
            ResponseWapper responseWapper = new ResponseWapper();
            responseWapper.setStatusCode(response.getStatusLine().getStatusCode());
            Map<String,String> responseHeaders = new HashMap<>(16);
            Arrays.stream(response.getAllHeaders()).forEach(header->responseHeaders.put(header.getName(),header.getValue()));
            responseWapper.setHeaders(responseHeaders);

            HttpEntity entity= response.getEntity();

            String mediaType = entity.getContentType()==null? DefaultHttp.DEFAULT_CONTENT_TYPE:entity.getContentType().getValue();
            responseWapper.setMediaType(mediaType);
            responseWapper.setBody(responseWapper.read(entity.getContent()));

            return responseWapper;
        }finally {
            if(httpPost!=null){
                httpPost.releaseConnection();
            }
            if(response!=null) {
                EntityUtils.consume(response.getEntity());
            }
        }

    }

}
