package com.github.chenhaiyangs.gateway.wapper;

import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * response 请求类封装
 * @author chenhaiyang
 */
@Data
public class ResponseWapper{
    /**
     * 原始请求uri
     */
    private String uri;
    /**
     * 后端响应类型
     */
    private String mediaType;
    /**
     * 透传后端响应头
     */
    private Map<String,String> headers = new HashMap<>();
    /**
     * 响应体,仅支持restful
     */
    private String body;
    /**
     * 后端服务的响应码：
     * 默认值 200
     */
    private Integer statusCode = 200;

    /**
     * input 转字符串
     * @param input content
     * @return 返回结果
     */
    public String read(InputStream input) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String line;
        try{
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
