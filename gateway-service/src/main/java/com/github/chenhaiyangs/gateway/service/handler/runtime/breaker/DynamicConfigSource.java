package com.github.chenhaiyangs.gateway.service.handler.runtime.breaker;

import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.storage.api.ApiStorage;
import com.github.chenhaiyangs.gateway.service.storage.breaker.BreakerStorage;
import com.github.chenhaiyangs.gateway.service.storage.breaker.vo.CurcuitBeaker;
import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 这是一个定时的调度任务，可以动态修改熔断器的配置
 * @author chenhaiyang
 */
@Slf4j
@Component
public class DynamicConfigSource implements PolledConfigurationSource {

    @Resource
    private BreakerStorage breakerStorage;
    @Resource
    private ApiStorage apiStorage;

    @Override
    public PollResult poll(boolean initial, Object checkPoint) throws Exception {

        Map<Integer,CurcuitBeaker> breakers = breakerStorage.getAllBreakers();
        log.info("hystrix dynamic config now begin to refresh,polled breakers：{}", breakers);

        Map<String, Object> complete = new HashMap<>(100);

        breakers.forEach((apiId,breaker)->{
            Api api = apiStorage.getApiById(apiId);
            if(api==null){
                log.warn("breaker:{},selected api is null ",breaker);
                return;
            }
            String path = api.getPath();
            complete.put(String.format(Constant.LIMIT_KEY,path),breaker.getLimit());
            complete.put(String.format(Constant.PERCENT_KEY,path),breaker.getPercent());
            complete.put(String.format(Constant.FORCE_CLOSE_KEY,path),breaker.isForceClose());
            complete.put(String.format(Constant.RETRIES_TIME_KEY,path),breaker.getRetriesTime());
            complete.put(String.format(Constant.TIMEOUT_KEY,path),breaker.getTimeout());
        });
        return PollResult.createFull(complete);
    }
}