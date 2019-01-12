package com.github.chenhaiyangs.gateway.service.storage.blacklist;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.blacklist.mapper.IpBlackListMapper;
import com.github.chenhaiyangs.gateway.service.storage.blacklist.vo.IpBlackList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ip黑名单存储层
 * @author chenhaiyang
 */
@Component
@Slf4j
public class IpBlackListStorage {

    @Resource
    private IpBlackListMapper ipBlackListMapper;
    @Resource
    private OnDataChanged onDataChanged;

    /**
     * api的ip黑名单缓存，key为apiId，value为黑名单信息
     */
    private Map<Integer,IpBlackList> apiBlackListCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<IpBlackList> ipBlackLists =  ipBlackListMapper.findAll();
        ipBlackLists.forEach(blackList -> apiBlackListCache.put(blackList.getApiId(),blackList));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.IP_BLACK_LIST.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            IpBlackList ipBlackList = ipBlackListMapper.selectById(id);
            log.info("apiId:{} blacklist has been refreshed! select by db ,result:{}",id,ipBlackList);
            if(ipBlackList!=null) {
                apiBlackListCache.put(ipBlackList.getApiId(), ipBlackList);
            }
        });
    }

    /**
     * 根据apiId获取它的黑名单信息
     * @param apiId apiId
     * @return {@link IpBlackList}
     */
    public IpBlackList getIpBlackList(Integer apiId){
        return apiBlackListCache.get(apiId);
    }

}
