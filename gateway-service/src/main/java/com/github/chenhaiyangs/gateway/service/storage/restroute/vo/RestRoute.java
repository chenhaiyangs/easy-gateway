package com.github.chenhaiyangs.gateway.service.storage.restroute.vo;

import com.github.chenhaiyangs.gateway.common.model.base.BaseModel;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * restRoute路由组件
 * @author chenhaiyang
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RestRoute extends BaseModel{
    /**
     * 自增主键
     */
    @Getter
    @Setter
    private int id;
    /**
     * 关联的api的Id
     */
    @Getter
    @Setter
    private int apiId;
    /**
     * 该请求路由到后端的真实的uri
     */
    @Getter
    @Setter
    private String routeUri;

    /**
     * 路由的后端的服务器的列表集，key为ip:port,value为权重
     */
    @Getter
    private String routeList;

    @Getter
    private Map<String,Integer> routeMap;
    /**
     * 路由类型
     */
    @Getter
    @Setter
    private int type;

    /**
     * setRouteList
     * @param routeList 后端真实服务器地址列表
     */
    @SuppressWarnings("unused")
    public void setRouteList(String routeList) {
        this.routeList = routeList;
        this.routeMap = FastJsonUtil.toIntegerValueMap(routeList);
    }

    /**
     * 负载均衡算法
     */
    public enum LoadBalanceType{
        /**
         * 按权重
         */
        WEIGHT(1),
        /**
         * IP_HASH
         */
        IP_HASH(2);

        @Getter
        private int value;
        LoadBalanceType(int value) {
            this.value = value;
        }
    }
}
