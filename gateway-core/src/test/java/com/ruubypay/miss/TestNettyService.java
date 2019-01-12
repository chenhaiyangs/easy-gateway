package com.ruubypay.miss;

import com.github.chenhaiyangs.gateway.*;
import com.github.chenhaiyangs.gateway.invocation.rest.RestfulRouteInvocation;
import com.github.chenhaiyangs.gateway.invocation.rest.httpclient.RestTemplate;
import com.github.chenhaiyangs.gateway.server.netty.config.NettyConfig;
import com.github.chenhaiyangs.gateway.wapper.AbstractRequestWapper;

public class TestNettyService {

    public static void main(String[] args) throws Exception {
        NettyConfig nettyConfig =NettyConfig.builder()
                .port(8888)
                .sobackLog(1024)
                .aggregator(1000)
                .build();


        RestTemplate restTemplate = new RestTemplate()
                .timeout(300,300)
                .maxTotal(3000)
                .setDefaultMaxPerRoute(500)
                .build();

        GateWayExecutor exector = new GateWayExecutor()
                .setRoute(new RestfulRouteInvocation(restTemplate))
                .runtimeHandle(new RuntimeHandler() {
                    @Override
                    public GateWayResponse execute(RouteInvocation routeInvocation, HandlerContext handlerContext) throws Exception {
                        System.out.println("我是第一个运行时网关，我执行了，index为1");
                        GateWayResponse response = routeInvocation.invoke(handlerContext);
                        System.out.println("我是第一个运行时网关，执行完");
                        return response;
                    }

                    @Override
                    public int index() {
                        return 1;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public void destory() {

                    }
                }).runtimeHandle(new RuntimeHandler() {
                    @Override
                    public GateWayResponse execute(RouteInvocation routeInvocation, HandlerContext handlerContext) throws Exception {
                          System.out.println("我是第二个运行时网关，我执行了，index为2");
                          GateWayResponse response = routeInvocation.invoke(handlerContext);
                          System.out.println("我是第二个运行时网关，执行完");
                          return response;
                    }

                    @Override
                    public int index() {
                        return 2;
                    }

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public void destory() {

                    }
                }).build();
        exector.execute(new AbstractRequestWapper() {
            @Override
            public AbstractRequestWapper buildRequestWapper() {
                return null;
            }
        });


//        HttpService service = new NettyServiceImpl(nettyConfig,exector);
//        service.start();
    }
}
