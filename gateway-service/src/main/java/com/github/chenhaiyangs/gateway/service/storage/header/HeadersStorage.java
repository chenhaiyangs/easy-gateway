package com.github.chenhaiyangs.gateway.service.storage.header;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.header.mapper.ResponseHeadersMapper;
import com.github.chenhaiyangs.gateway.service.storage.header.vo.ResponseHeader;
import com.github.chenhaiyangs.gateway.service.storage.mock.vo.MockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 针对单个api，在http-response-header添加信息的postheader
 * @author chenhaiyang
 */
@Component
@Slf4j
public class HeadersStorage {

    @Resource
    private ResponseHeadersMapper responseHeaderMapper;
    @Resource
    private OnDataChanged onDataChanged;

    /**
     * api的ip白名单缓存，key为apiId，value为api的白名单信息
     */
    private Map<Integer,ResponseHeader> apiResponseHeaderCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<ResponseHeader> responseHeaders =  responseHeaderMapper.findAll();
        responseHeaders.forEach(header -> apiResponseHeaderCache.put(header.getApiId(),header));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.HEADERS.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            ResponseHeader responseHeader = responseHeaderMapper.selectById(id);
            log.info("apiId:{} ressponseHeader has been refreshed! select by db ,result:{}",id,responseHeader);
            if(responseHeader!=null) {
                apiResponseHeaderCache.put(responseHeader.getApiId(), responseHeader);
            }
        });
    }

    /**
     * 根据apiId获取mockVo
     * @param apiId apiId
     * @return {@link MockVo}
     */
    public ResponseHeader getResponseHeaderByApiId(Integer apiId){
        return apiResponseHeaderCache.get(apiId);
    }
}
