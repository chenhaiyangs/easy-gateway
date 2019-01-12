package com.github.chenhaiyangs.gateway.service.handler.pre.restroute;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.common.config.ConfigDTO;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.invocation.rest.RestfulRouteInvocation;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.pre.AbstractPreHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.LoadBalance;
import com.github.chenhaiyangs.gateway.service.handler.pre.restroute.extension.LoadBalanceFactory;
import com.github.chenhaiyangs.gateway.service.storage.api.ApiStorage;
import com.github.chenhaiyangs.gateway.service.storage.restroute.RestRouteStorage;
import com.github.chenhaiyangs.gateway.service.storage.restroute.vo.RestRoute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * 负载均衡处理器。在这里负责处理负载均衡相关的功能。并选举出最终一个调用接口
 * @author chenhaiyang
 */
@Slf4j
@Service
public class RestfulLoadBalanceHandler extends AbstractPreHandler{

    @Resource
    private RestRouteStorage restRouteStorage;
    @Resource
    private ApiStorage apiStorage;

    @Autowired
    public RestfulLoadBalanceHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @PostConstruct
    public void init() throws Exception {
        restRouteStorage.onDateChanged( dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            RestRoute restRoute = restRouteStorage.getRestRouteById(id);
            Api api = apiStorage.getApiById(restRoute.getApiId());
            if(api!=null){
                LoadBalanceFactory.notify(api.getPath());
            }
        });
    }

    @Override
    public GateWayResponse iHandle(HandlerContext handlerContext) {

        Map<String,ConfigDTO> configs = handlerContext.getConfigs();
        Api api = handlerContext.getApi();
        RestRoute restRoute = restRouteStorage.getRestRouteByApiId(api.getId());

        LoadBalance loadBalance = LoadBalanceFactory.getLoadBalance(restRoute);
        String host = loadBalance.getOne(handlerContext,restRoute);
        log.info("最终路由后端请求地址：{}",host);

        // 在这里将选举结果放到configs上下文中
        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setValue(host);
        configDTO.setDefaultValue(host);
        configDTO.setRequired(true);
        configs.put(RestfulRouteInvocation.UNIQUE_HTTP_IP_POTE,configDTO);

        return new GateWayResponse();
    }

    @Override
    public int index() {
        return 6;
    }

    @Override
    public String getName() {
        return "loadBalanceHandler";
    }
}
