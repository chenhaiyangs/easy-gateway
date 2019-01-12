package com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.weight;

import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.exception.NoHostBeFoundException;
import com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.LoadBalance;
import com.github.chenhaiyangs.gateway.service.storage.restroute.vo.RestRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按照权重的负载均衡算法
 * @author chenhaiyang
 */
public class WeightLoadBalance implements LoadBalance{

    /**
     * 存储路由的缓存，避免重复计算
     */
    private Map<String,Weight> balances = new ConcurrentHashMap<>();

    @Override
    public String getOne(HandlerContext handlerContext, RestRoute restRoute) {

        Api api = handlerContext.getApi();
        Map<String,Integer> routeMap =  restRoute.getRouteMap();
        Weight weight = balances.get(api.getPath());
        if(weight==null){
            weight = new Weight(routeMap);
            balances.putIfAbsent(api.getPath(),weight);
        }
        return weight.getOne();
    }

    @Override
    public void notify(String path) {
        balances.remove(path);
    }

    /**
     * 负载均衡实现核心算法
     */
    private class Weight{
        private List<String> hosts = new ArrayList<>();
        private Map<String, Integer> weights;

        private Weight(Map<String,Integer> weights) {
            this.weights=weights;

            if(weights==null || weights.size()==0){
                throw new NoHostBeFoundException();
            }

            weights.keySet().forEach(this::putHosts);

        }
        private void putHosts(String host) {
            for(int i=0;i<weights.get(host);i++){
                hosts.add(host);
            }
        }
        private String getOne(){
            int index = new Random().nextInt(hosts.size());
            return hosts.get(index);
        }
    }
}
