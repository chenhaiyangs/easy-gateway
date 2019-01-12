package com.github.chenhaiyangs.gateway.service.storage.mock;

import com.github.chenhaiyangs.gateway.service.storage.OnDataChanged;
import com.github.chenhaiyangs.gateway.service.storage.mock.mapper.MockMapper;
import com.github.chenhaiyangs.gateway.service.storage.mock.vo.MockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mock组件存储层
 * @author chenhaiyang
 */
@Component
@Slf4j
public class MockStorage {

    @Resource
    private MockMapper mockMapper;
    @Resource
    private OnDataChanged onDataChanged;
    /**
     * api的mock组件缓存map
     */
    private Map<Integer,MockVo> apiMockCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() throws Exception {
        List<MockVo> mockVos =  mockMapper.findAll();
        mockVos.forEach(mock -> apiMockCache.put(mock.getApiId(),mock));

        onDataChanged.addChildListener(OnDataChanged.CacheListNode.MOCK.getNode(), dataType -> {

            Integer id = Integer.parseInt(dataType.getChild());
            MockVo mockVo = mockMapper.selectById(id);
            log.info("apiId:{} mockvo has been refreshed! select by db ,result:{}",id,mockVo);
            if(mockVo!=null) {
                apiMockCache.put(mockVo.getApiId(), mockVo);
            }
        });
    }

    /**
     * 根据apiId获取mockVo
     * @param apiId apiId
     * @return {@link MockVo}
     */
    public MockVo getMockByApiId(Integer apiId){
        return apiMockCache.get(apiId);
    }
}
