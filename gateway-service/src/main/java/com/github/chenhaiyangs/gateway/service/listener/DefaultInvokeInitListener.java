package com.github.chenhaiyangs.gateway.service.listener;

import com.github.chenhaiyangs.gateway.InvokeInitListener;
import com.github.chenhaiyangs.gateway.common.model.Api;
import com.github.chenhaiyangs.gateway.service.constant.CommonHandlerConstant;
import com.github.chenhaiyangs.gateway.service.exception.WrongApiException;
import com.github.chenhaiyangs.gateway.service.storage.api.ApiStorage;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 每次网关的调用，都会调用该函数初始化一个属于该次请求的Api
 * api的路径匹配按照从大到小的规则。例如：
 * 后端配置了两个路径： 1，/user 2，/user/getbyid
 * 请求路径为 /user/getbyid，则会匹配2，则执行的一切网关规则都是2的，如果没有配置2，则/user/getbyid也会匹配上/user的api
 * @author chenhaiyang
 */
@Service
public class DefaultInvokeInitListener implements InvokeInitListener{

    @Resource
    private ApiStorage apiStorage;

    @Override
    public Api setApi(AbstractRequestWapper abstractRequestWapper) {
        String uri = abstractRequestWapper.getUri();
        //get请求切割uri
        if(uri.contains(CommonHandlerConstant.GET_PARAMETER)){
            uri=uri.substring(0,uri.indexOf(CommonHandlerConstant.GET_PARAMETER));
        }
        Api api;
        do{
            api= apiStorage.getApi(uri);
            uri = uri.substring(0,uri.lastIndexOf(CommonHandlerConstant.PATH_SEPARATE)) ;
        }while (api==null && uri.length()>0);

        if(api==null){
            throw new WrongApiException();
        }
        return api;
    }
}
