package com.github.chenhaiyangs.gateway.service.storage.sign;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.sign.mapper.SignConfigMapper;
import com.github.chenhaiyangs.gateway.service.storage.sign.vo.RequestSignConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 签名组件的配置
 * @author chenhaiyang
 */
@Component
@Slf4j
public class SignConfigStorage {

    @Resource
    private SignConfigMapper signConfigMapper;
    @Resource
    private OnDataChanged onDataChanged;
    /**
     * api的RequestSignConfig组件缓存map
     */
    private Map<Integer,RequestSignConfig> requestSignConfigMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<RequestSignConfig> requestSignConfigs =  signConfigMapper.findAll();
        requestSignConfigs.forEach(signConfig -> requestSignConfigMap.put(signConfig.getApiId(),signConfig));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.SIGN.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            RequestSignConfig requestSignConfig = signConfigMapper.selectById(id);
            log.info("apiId:{} requestSignConfig has been refreshed! select by db ,result:{}",id,requestSignConfig);
            if(requestSignConfig!=null) {
                requestSignConfigMap.put(requestSignConfig.getApiId(), requestSignConfig);
            }
        });
    }

    /**
     * 根据apiId获取RequestSignConfig
     * @param apiId apiId
     * @return {@link RequestSignConfig}
     */
    public RequestSignConfig getRequestSignByApiId(Integer apiId){
        return requestSignConfigMap.get(apiId);
    }
}
