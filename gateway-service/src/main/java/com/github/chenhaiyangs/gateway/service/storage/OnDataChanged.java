package com.github.chenhaiyangs.gateway.service.storage;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * 当数据变更时，接受通知
 * @author chenhaiyang
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class OnDataChanged {
    /**
     * 作为zk的根节点
     */
    private static final String ROOT_PARH="/easy-gateway";
    /**
     * 分割符
     */
    private static final String SEPARATOR="/";
    /**
     * zookeeper客户端
     */
    private CuratorFramework client;
    @Autowired
    public OnDataChanged(CuratorFramework client) {
        this.client=client;
        client.start();
    }

    public void addChildListener(String node, Consumer<DataType> onChanged) throws Exception {

        log.info("addListener node:{}",node);
        PathChildrenCache cache = new PathChildrenCache(client, ROOT_PARH+SEPARATOR+node, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            /**
             * 程序初始化时该instener不调用
             */
            private volatile boolean isInit =true;
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {

                if(isInit){
                    isInit=false;
                    return;
                }

                if(pathChildrenCacheEvent==null || pathChildrenCacheEvent.getData()==null){
                    log.warn("pathChildrenCacheEvent or Data is null!");
                    return;
                }
                String path =pathChildrenCacheEvent.getData().getPath();
                String child = path.substring(path.lastIndexOf(SEPARATOR)+1,path.length());
                DataType dataType = new DataType();
                dataType.setChild(child);

                switch (pathChildrenCacheEvent.getType()) {
                    case CHILD_ADDED:
                    case CHILD_UPDATED:
                        dataType.setEvent(EventType.MODIFY.type);
                        onChanged.accept(dataType);
                        break;
                    case CHILD_REMOVED:
                        dataType.setEvent(EventType.DELETE.type);
                        onChanged.accept(dataType);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Data
    public class DataType{
        /**
         * child节点名，不带路径
         */
        private String child;
        /**
         * 事件类型，1：修改／新增（刷新缓存） 2，删除，从缓存中删除该key
         */
        private int event;
    }
    /**
     * 事件类型{@link DataType}的event枚举
     */
    public enum EventType{
        /**
         * 事件类型：修改
         */
        MODIFY(1),
        /**
         * 事件类型：删除
         */
        DELETE(2);
        @Getter
        private int type;
        EventType(int type) {
            this.type = type;
        }
    }

    /**
     * zookeeper上缓存的列表类型{@link #addChildListener} 第一个入参
     */
    public enum CacheListNode{
        /**
         * node类型：api
         */
        API("api"),
        /**
         * ip黑名单组件
         */
        IP_BLACK_LIST("ipblacklist"),
        /**
         * ip白名单组件
         */
        IP_WHITE_LIST("ipwhitelist"),
        /**
         * mock组件
         */
        MOCK("mock"),
        /**
         * 响应头组件
         */
        HEADERS("headers"),
        /**
         * 熔断器组件
         */
        CIRCUIT_BREAKER("breakers"),
        /**
         * rest路由
         */
        REST_ROUTE("restroute"),
        /**
         * 限流组件
         */
        RATE_LIMIT("ratelimit"),
        /**
         * 请求签名组件
         */
        SIGN("sign"),
        /**
         * 请求token组件
         */
        TOKEN("token");
        @Getter
        private String node;
        CacheListNode(String node) {
            this.node = node;
        }
    }

}
