package com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit;

import com.github.chenhaiyangs.gateway.GateWayResponse;
import com.github.chenhaiyangs.gateway.HandlerContext;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.error.ErrorEnum;
import com.github.chenhaiyangs.gateway.service.handler.HandlerDisabledCompment;
import com.github.chenhaiyangs.gateway.service.handler.pre.AbstractPreHandler;
import com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension.RateLimiter;
import com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension.inmemory.InMemoryRateLimiter;
import com.github.chenhaiyangs.gateway.service.handler.pre.ratelimit.extension.redis.RedisRateLimiter;
import com.github.chenhaiyangs.gateway.service.storage.api.ApiStorage;
import com.github.chenhaiyangs.gateway.service.storage.ratelimit.RateLimitStorage;
import com.github.chenhaiyangs.gateway.service.storage.ratelimit.vo.RateLimitVo;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流处理Handler
 * @see <a href=https://github.com/mokies/ratelimitj>ratelimitj</a>
 * @author chenhaiyang
 */
@Slf4j
@Service
public class RateLimitHandler extends AbstractPreHandler{

    /**
     * redis相关的配置
     */
    @SuppressWarnings("all")
    @Value("#{redisGroup['redis.host']}")
    private String redisHost;
    @SuppressWarnings("all")
    @Value("#{redisGroup['redis.port']}")
    private int port;
    @SuppressWarnings("all")
    @Value("#{redisGroup['redis.password']}")
    private String password;


    @Resource
    private RateLimitStorage rateLimitStorage;
    @Resource
    private ApiStorage apiStorage;
    /**
     * 针对每一个api的限流组件，key为apiId，value为 RateLimiter
     */
    private Map<Integer,RateLimiter> requestRateLimiters = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitHandler(HandlerDisabledCompment handlerDisabledCompment) {
        super(handlerDisabledCompment);
    }

    @PostConstruct
    public void init() throws Exception {

        rateLimitStorage.onDateChanged( dataType -> {
            /*
             * 当rateLimit的配置更新时收到监听，handler清空缓存的RateLimiter
             */
            Integer id = Integer.parseInt(dataType.getChild());
            log.info("ratelimit config has been changed,now begin to refresh ratelimitconfig,id:{}",id);
            RateLimitVo rateLimitVo = rateLimitStorage.getRateLimitVoById(id);
            if(rateLimitVo!= null) {
                Api api = apiStorage.getApiById(rateLimitVo.getApiId());
                if (api != null) {
                    RateLimiter rateLimiter = requestRateLimiters.get(api.getId());
                    if (rateLimiter != null) {
                        rateLimiter.reset(api.getPath());
                        requestRateLimiters.remove(rateLimitVo.getApiId());
                    }
                }
            }
        });
    }

    @Override
    public GateWayResponse iHandle(HandlerContext handlerContext) {

        AbstractRequestWapper requestWapper = handlerContext.getRequestWapper();
        Api api = handlerContext.getApi();
        RateLimitVo  rateLimitVo = rateLimitStorage.getRateLimitVoByApiId(api.getId());
        if(rateLimitVo==null ||rateLimitVo.isForbidden()){
            return new GateWayResponse();
        }
        RateLimiter rateLimiter = requestRateLimiters.get(api.getId());
        if(rateLimiter==null){
            if(RateLimitVo.RateLimitType.REDIS.getValue()==rateLimitVo.getType()){
                log.info("create redisRateLimiter,api:{}",api);
                rateLimiter = new RedisRateLimiter(redisHost,port,password,rateLimitVo.getLimitMap());
            }
            if(RateLimitVo.RateLimitType.LOCAL.getValue()==rateLimitVo.getType()){
                log.info("create InMemoryRateLimiter,api:{}",api);
                rateLimiter = new InMemoryRateLimiter(rateLimitVo.getLimitMap());
            }
            if(rateLimiter==null){
                return new GateWayResponse();
            }
            requestRateLimiters.put(api.getId(),rateLimiter);
        }
        if(!rateLimiter.acquire(api.getPath())){
            log.error("请求id:{},请求api:{},请求被限流",requestWapper.getRequestId(),api);
            return new GateWayResponse(
                    ErrorEnum.RATE_LIMIT_HANDLER.getCode(),
                    ErrorEnum.WHITE_LIST_HANDLER.getMsg());
        }
        return new GateWayResponse();

    }
    @Override
    public int index() {
        return 3;
    }

    @Override
    public String getName() {
        return "rateLimitHandler";
    }
}
