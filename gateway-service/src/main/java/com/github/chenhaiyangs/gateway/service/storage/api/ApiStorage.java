package com.github.chenhaiyangs.gateway.service.storage.api;

import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.api.mapper.ApiMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * api存储
 * @author chenhaiyang
 */
@Component
@Slf4j
public class ApiStorage {

    @Resource
    private ApiMapper apiMapper;
    @Resource
    private OnDataChanged onDataChanged;
    /**
     * apiCache缓存,key为path
     */
    private Map<String,Api> apiCaches = new ConcurrentHashMap<>();
    /**
     * apiCache的缓存，key为Id
     */
    private Map<Integer,Api> apiCachesById = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<Api> apiList =  apiMapper.findAll();
        apiList.forEach(api -> {
            apiCaches.put(api.getPath(),api);
            apiCachesById.put(api.getId(),api);
        });


        onDataChanged.addChildListener(OnDataChanged.CacheListNode.API.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            Api api = apiMapper.selectApiById(id);
            log.info("apiId:{} has been refreshed! select by db ,result:{}",id,api);
            if(api!=null) {
                apiCaches.put(api.getPath(), api);
                apiCachesById.put(api.getId(),api);
            }
        });
    }

    /**
     * 根据path获取一个api
     * @param path path
     * @return {@link Api}
     */
    public Api getApi(String path){
        return apiCaches.get(path);
    }

    /**
     * 根据apiId获取一个api
     * @param id id
     * @return {@link Api}
     */
    public Api getApiById(Integer id){
        return apiCachesById.get(id);
    }
}
