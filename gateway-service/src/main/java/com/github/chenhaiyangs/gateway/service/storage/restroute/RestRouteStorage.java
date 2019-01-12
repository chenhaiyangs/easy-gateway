package com.github.chenhaiyangs.gateway.service.storage.restroute;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.restroute.mapper.RestRouteMapper;
import com.github.chenhaiyangs.gateway.service.storage.restroute.vo.RestRoute;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * rest路由组件缓存层
 * @author chenhaiyang
 */
@Component
@Slf4j
public class RestRouteStorage {

    @Resource
    private RestRouteMapper restRouteMapper;

    @Getter
    @Resource
    private OnDataChanged onDataChanged;

    /**
     * api的rest路由地址，key为apiId，value为restRoute
     */
    private Map<Integer,RestRoute> apiRestRouteCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<RestRoute> restRoutes =  restRouteMapper.findAll();
        restRoutes.forEach(restRoute -> apiRestRouteCache.put(restRoute.getApiId(),restRoute));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.REST_ROUTE.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            RestRoute restRoute = restRouteMapper.selectById(id);
            log.info("apiId:{} restRoute has been refreshed! select by db ,result:{}",id,restRoute);
            if(restRoute!=null) {
                apiRestRouteCache.put(restRoute.getApiId(), restRoute);
            }
        });
    }

    /**
     * 其他介质接收负载均衡存储层的变更事件通知，主要用于刷新缓存等操作。
     * @param onChanged {@link Consumer}
     * @throws Exception e
     */
    public void onDateChanged(Consumer<OnDataChanged.DataType> onChanged) throws Exception {
        onDataChanged.addChildListener(OnDataChanged.CacheListNode.REST_ROUTE.getNode(),onChanged);
    }

    /**
     * 根据apiId获取restRoute组件
     * @param id id
     * @return {@link RestRoute}
     */
    public RestRoute getRestRouteByApiId(Integer id){
        return apiRestRouteCache.get(id);
    }

    /**
     * 根据id获取rest路由组件
     * @param id id
     * @return 结果
     */
    public RestRoute getRestRouteById(Integer id) {
        return restRouteMapper.selectById(id);
    }
}
