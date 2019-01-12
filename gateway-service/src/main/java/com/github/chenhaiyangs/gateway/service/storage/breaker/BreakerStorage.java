package com.github.chenhaiyangs.gateway.service.storage.breaker;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.breaker.mapper.CurcuitBeakerMapper;
import com.github.chenhaiyangs.gateway.service.storage.breaker.vo.CurcuitBeaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 熔断器api数据存储层
 * @author chenhaiyang
 */
@Component
@Slf4j
public class BreakerStorage {
    @Resource
    private CurcuitBeakerMapper curcuitBeakerMapper;
    @Resource
    private OnDataChanged onDataChanged;

    /**
     * api的的熔断器配置缓存，key为apiId，value为熔断器缓存
     */
    private Map<Integer,CurcuitBeaker> apiCurcuitBeakerCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<CurcuitBeaker> curcuitBeakers =  curcuitBeakerMapper.findAll();
        curcuitBeakers.forEach(curcuitBeaker -> apiCurcuitBeakerCache.put(curcuitBeaker.getApiId(),curcuitBeaker));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.CIRCUIT_BREAKER.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            CurcuitBeaker curcuitBeaker = curcuitBeakerMapper.selectById(id);
            log.info("apiId:{} curcuitBeaker has been refreshed! select by db ,result:{}",id,curcuitBeaker);
            if(curcuitBeaker!=null) {
                apiCurcuitBeakerCache.put(curcuitBeaker.getApiId(), curcuitBeaker);
            }
        });
    }

    /**
     * 根据apiId获取熔断器
     * @param id apiId
     * @return {@link CurcuitBeaker}
     */
    public CurcuitBeaker getBreakerByApiId(Integer id) {
        return apiCurcuitBeakerCache.get(id);
    }

    /**
     * 获取全部的熔断器配置
     * @return {@link CurcuitBeaker}
     */
    public Map<Integer, CurcuitBeaker> getAllBreakers() {
        return apiCurcuitBeakerCache;
    }
}
