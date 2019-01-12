package com.github.chenhaiyangs.gateway.common.model;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * API信息
 * @author chenhaiyang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Api extends BaseModel{
    /**
     * apiId
     */
    private Integer id;
    /**
     * api名称
     */
    private String name;
    /**
     * 暴露给第三方使用的路径
     */
    private String path;
    /**
     * 针对该api的描述
     */
    private String description;
    /**
     * 是否是loginApi，如果是，在sign组件和token组件，会在响应中添加signKey或者token
     */
    private boolean loginApi;
    /**
     * api所在的域，逗号间隔，申请token时以域为单位申请
     */
    private String scopes;
    /**

     * api所在的域转化成list,
     */
    private List<String> scopeList = new ArrayList<>();

    @SuppressWarnings("unused")
    public void setScopes(String scopes) {
        this.scopes = scopes;
        if(this.scopes!=null){
            this.scopeList= Arrays.stream(scopes.split(",")).collect(Collectors.toList());
        }
    }


}
