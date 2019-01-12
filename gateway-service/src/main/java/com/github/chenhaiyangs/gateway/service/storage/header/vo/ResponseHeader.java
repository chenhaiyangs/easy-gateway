package com.github.chenhaiyangs.gateway.service.storage.header.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 针对某个api，给响应添加http-header
 * @author chenhaiyang
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ResponseHeader extends BaseModel{

    /**
     * id
     */
    @Setter
    @Getter
    private int id;
    /**
     * apiId
     */
    @Setter
    @Getter
    private int apiId;
    /**
     * heanders
     */
    @Getter
    private String headers;
    /**
     * blackListMap
     */
    @Getter
    private Map<String,String> headersMap;
    /**
     * 是否被禁用
     */
    @Setter
    @Getter
    private boolean forbidden;

    /**
     * overWrite setHeaders
     * @param headers heasers
     */
    @SuppressWarnings("unused")
    public void setHeaders(String headers) {
        this.headers = headers;
        this.headersMap = FastJsonUtil.toStringMap(headers);
    }
}
