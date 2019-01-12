package com.github.chenhaiyangs.gateway.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ThreadFactory
 * @author chenhaiyang
 */
@Slf4j
public class GateWayThreadFactory implements ThreadFactory{
    /**
     * 线程编号
     */
    private static final AtomicLong THREAD_NUMBER = new AtomicLong(1);
    /**
     * 线程组
     */
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("thread");

    @Override
    @SuppressWarnings("all")
    public Thread newThread(Runnable runnable) {
    return new Thread(THREAD_GROUP, runnable,
        THREAD_GROUP.getName() + "-" + "gateway_concurrent_thread" + "-" + THREAD_NUMBER.getAndIncrement());
    }
}
