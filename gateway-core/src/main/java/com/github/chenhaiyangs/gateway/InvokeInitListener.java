package com.github.chenhaiyangs.gateway;

import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;

/**
 * 在InvokeInitListener中，网关每次开始调用handler链前调用该方法
 * 由用户实现返回一个合适的{@link com.github.chenhaiyangs.gateway.common.model.Api}
 * @author chenhaiyang
 */
public interface InvokeInitListener {
    /**
     * 设置api
     * @param abstractRequestWapper {@link AbstractRequestWapper}
     * @return {@link Api}
     */
    Api setApi(AbstractRequestWapper  abstractRequestWapper);
}
