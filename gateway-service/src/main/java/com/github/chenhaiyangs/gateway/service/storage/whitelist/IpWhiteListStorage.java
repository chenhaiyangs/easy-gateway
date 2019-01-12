package com.github.chenhaiyangs.gateway.service.storage.whitelist;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.whitelist.mapper.IpWhiteListMapper;
import com.github.chenhaiyangs.gateway.service.storage.whitelist.vo.IpWhiteList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网关组件集ip白名单列表
 * 与黑名单不同的是，当api配置了白名单组件，只有在白名单列表中的ip地址才能访问本api。其他请求通通不能访问本api
 * @author chenhaiyang
 */
@Component
@Slf4j
public class IpWhiteListStorage {

    @Resource
    private IpWhiteListMapper ipWhiteListMapper;
    @Resource
    private OnDataChanged onDataChanged;

    /**
     * api的ip白名单缓存，key为apiId，value为api的白名单信息
     */
    private Map<Integer,IpWhiteList> apiWhiteListCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<IpWhiteList> ipWhiteLists =  ipWhiteListMapper.findAll();
        ipWhiteLists.forEach(whiteList -> apiWhiteListCache.put(whiteList.getApiId(),whiteList));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.IP_WHITE_LIST.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            IpWhiteList ipWhiteList = ipWhiteListMapper.selectById(id);
            log.info("apiId:{} whitelist has been refreshed! select by db ,result:{}",id,ipWhiteList);
            if(ipWhiteList!=null) {
                apiWhiteListCache.put(ipWhiteList.getApiId(), ipWhiteList);
            }
        });
    }

    /**
     * 根据apiId获取它的白名单信息
     * @param apiId apiId
     * @return {@link IpWhiteList}
     */
    public IpWhiteList getIpWhiteList(Integer apiId){
        return apiWhiteListCache.get(apiId);
    }

}
