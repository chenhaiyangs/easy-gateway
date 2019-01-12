package com.github.chenhaiyangs.gateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.locks.LockSupport;

/**
 * api网关核心启动类
 * @author chenhaiyang
 */
@Slf4j
public class ApiGateWayApplication {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"config-init.xml"});
        context.start();
        context.registerShutdownHook();

        log.info("gateway service is started successfully !");
        LockSupport.park();
    }


}
