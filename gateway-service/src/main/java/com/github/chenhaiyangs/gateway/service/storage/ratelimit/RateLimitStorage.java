package com.github.chenhaiyangs.gateway.service.storage.ratelimit;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.mock.vo.MockVo;
import com.github.chenhaiyangs.gateway.service.storage.ratelimit.mapper.RateLimitMapper;
import com.github.chenhaiyangs.gateway.service.storage.ratelimit.vo.RateLimitVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * api的限流存储类
 * @author chenhaiyang
 */
@Component
@Slf4j
public class RateLimitStorage {
    @Resource
    private RateLimitMapper rateLimitMapper;
    @Resource
    private OnDataChanged onDataChanged;

    /**
     * api的限流组件缓存集
     */
    private Map<Integer,RateLimitVo> apiRateLimitVoCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<RateLimitVo> rateLimitVos =  rateLimitMapper.findAll();
        rateLimitVos.forEach(rateLimitVo -> apiRateLimitVoCache.put(rateLimitVo.getApiId(),rateLimitVo));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.RATE_LIMIT.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            RateLimitVo rateLimitVo = rateLimitMapper.selectById(id);
            log.info("apiId:{} ratelimit has been refreshed! select by db ,result:{}",id,rateLimitVo);
            if(rateLimitVo!=null) {
                apiRateLimitVoCache.put(rateLimitVo.getApiId(), rateLimitVo);
            }
        });
    }

    /**
     * 其他介质接收到数据变更的通知，刷新限流配置
     * @param onChanged {@link Consumer}
     * @throws Exception e
     */
    public void onDateChanged(Consumer<OnDataChanged.DataType> onChanged) throws Exception {
        onDataChanged.addChildListener(OnDataChanged.CacheListNode.RATE_LIMIT.getNode(),onChanged);
    }

    /**
     * 根据apiId获取RateLimit限流组件
     * @param apiId apiId
     * @return {@link MockVo}
     */
    public RateLimitVo getRateLimitVoByApiId(Integer apiId){
        return apiRateLimitVoCache.get(apiId);
    }

    /**
     * 根据Id查询rateLimitVo
     * @param id id
     * @return {@link RateLimitVo}
     */
    public RateLimitVo getRateLimitVoById(Integer id) {
        return rateLimitMapper.selectById(id);
    }
}
