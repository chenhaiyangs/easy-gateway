package com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension;

import com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.iphash.IpHashLoadBalance;
import com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.weight.WeightLoadBalance;
import com.github.chenhaiyangs.gateway.service.storage.restroute.vo.RestRoute;

import java.util.HashMap;
import java.util.Map;

/**
 * 负载均衡算法工厂
 * @author chenhaiyang
 */
public class LoadBalanceFactory {

    private static Map<Integer,LoadBalance> loadBalances = new HashMap<>();
    static {
        loadBalances.put(RestRoute.LoadBalanceType.WEIGHT.getValue(),new WeightLoadBalance());
        loadBalances.put(RestRoute.LoadBalanceType.IP_HASH.getValue(),new IpHashLoadBalance());
    }

    /**
     * LoadBalanceFactory
     * @return {@link LoadBalance}
     */
    public static LoadBalance getLoadBalance(RestRoute restRoute){

        int type = restRoute.getType();
        LoadBalance loadBalance = loadBalances.get(type);
        if(loadBalance!=null){
            return loadBalance;
        }
        return loadBalances.get(RestRoute.LoadBalanceType.WEIGHT.getValue());
    }

    public static void notify(String path) {
        loadBalances.forEach((k,v)->v.notify(path));
    }
}
