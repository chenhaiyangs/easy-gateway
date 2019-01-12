package com.github.chenhaiyangs.gateway.service.handler.runtime.breaker;

/**
 * 一些与Hystrix有关的常量
 * @author chenhaiyang
 */
public interface Constant {
    /**
     * 每一个服务路径uri的最大的信号量
     */
    String LIMIT_KEY="breaker.command.%s.execution.isolation.semaphore.maxConcurrentRequests";
    /**
     * 超时时间
     */
    String TIMEOUT_KEY="breaker.command.%s.execution.isolation.thread.timeoutInMilliseconds";
    /**
     * 当前的错误率小于 %n，则不进行熔断
     */
    String PERCENT_KEY="breaker.command.%s.circuitBreaker.errorThresholdPercentage";
    /**
     * 是否开启强制关闭容器功能，0 不关闭，1 关闭
     */
    String FORCE_CLOSE_KEY="breaker.command.%s.circuitBreaker.forceClosed";
    /**
     * 如果已经触发了熔断，那么，多久以后放一个请求过去（单位：ms）默认为五秒
     */
    String RETRIES_TIME_KEY="breaker.command.%s.circuitBreaker.sleepWindowInMilliseconds";
}
