package com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.iphash;

import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.service.exception.NoHostBeFoundException;
import com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.LoadBalance;
import com.github.chenhaiyangs.gateway.service.storage.restroute.vo.RestRoute;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;

import java.util.ArrayList;

/**
 * ipHash的负载均衡算法
 * @author chenhaiyang
 */
public class IpHashLoadBalance implements LoadBalance{
    @Override
    public String getOne(HandlerContext handlerContext, RestRoute restRoute) {

        AbstractRequestWapper requestWapper = handlerContext.getRequestWapper();
        ArrayList<String> keyList = new ArrayList<>(restRoute.getRouteMap().keySet());
        if(keyList.size()==0){
            throw new NoHostBeFoundException();
        }
        int hashCode = requestWapper.getClientIp().hashCode();
        int pos = hashCode % keyList.size();

        return keyList.get(pos);
    }

    @Override
    public void notify(String path) {

    }
}
