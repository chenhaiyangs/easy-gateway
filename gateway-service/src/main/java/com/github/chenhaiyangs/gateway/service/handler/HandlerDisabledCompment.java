package com.github.chenhaiyangs.gateway.service.handler;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 公共组件禁用集功能需要使用的Bean,用于包装disabledHandlerGroup,解决在构造函数时的注入问题
 * @author chenhaiyang
 */
@Component
public class HandlerDisabledCompment {

    @Resource(name = "disabledHandlerGroup")
    private AbstractGeneralConfigGroup disabledHandlerGroup;

    /**
     * 是否包含某个key
     * @param key key
     * @return if contains key
     */
    public boolean containsKey(String key){
        return disabledHandlerGroup.containsKey(key);
    }

    /**
     * 根据key获取value
     * @param key key
     * @return value
     */
    public String get(String key){
        return disabledHandlerGroup.get(key);
    }
}
