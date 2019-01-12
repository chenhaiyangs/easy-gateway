package com.github.chenhaiyangs.gateway.service.storage.token;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.token.mapper.TokenConfigMapper;
import com.github.chenhaiyangs.gateway.service.storage.token.vo.RequestTokenConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * api的token配置存储
 * @author chenhaiyang
 */
@Component
@Slf4j
public class TokenConfigStorage {

    @Resource
    private TokenConfigMapper tokenConfigMapper;
    @Resource
    private OnDataChanged onDataChanged;
    /**
     * api的RequestSignConfig组件缓存map
     */
    private Map<Integer,RequestTokenConfig> requestTokenConfigMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<RequestTokenConfig> requestTokenConfigs =  tokenConfigMapper.findAll();
        requestTokenConfigs.forEach(tokenConfig -> requestTokenConfigMap.put(tokenConfig.getApiId(),tokenConfig));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.TOKEN.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            RequestTokenConfig requestTokenConfig = tokenConfigMapper.selectById(id);
            log.info("apiId:{} requestTokenConfig has been refreshed! select by db ,result:{}",id,requestTokenConfig);
            if(requestTokenConfig!=null) {
                requestTokenConfigMap.put(requestTokenConfig.getApiId(), requestTokenConfig);
            }
        });
    }

    /**
     * 根据apiId获取RequestTokenConfig
     * @param apiId apiId
     * @return {@link RequestTokenConfig}
     */
    public RequestTokenConfig getRequestTokenConfigByApiId(Integer apiId){
        return requestTokenConfigMap.get(apiId);
    }
}
