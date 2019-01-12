package com.github.chenhaiyangs.gateway.service.handler.runtime.breaker;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Hystrix监控器
 * @author chenhaiyang
 */
@Component
@SuppressWarnings("all")
@Slf4j
public class DashBoardEnabled {

    private HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
    @Value("#{gatawayBasic['hystrix.port']}")
    private String port;

    /**
     * 启动tomcat，进而可以监控Hystrix的调用，展示DashBoard
     * @throws LifecycleException 异常
     */
    @PostConstruct
    public void init() throws LifecycleException {
        new Thread(new TomcatRunnable()).start();
    }

    private class TomcatRunnable implements Runnable{

        @Override
        public void run() {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(Integer.valueOf(port));
            tomcat.getHost().setAutoDeploy(false);
            log.error("loading tomcat port:{}",port);
            /*
             * tomcat连接器配置，Hystrix 并不需要多少连接，在这里写死
             */
            Connector connector = tomcat.getConnector();
            connector.setAttribute("acceptCount",10);
            connector.setAttribute("maxConnections",20);
            connector.setAttribute("maxThreads",10);

            StandardContext standardContext = new StandardContext();
            standardContext.setPath("/gateway");
            standardContext.addLifecycleListener(new Tomcat.FixContextListener());
            tomcat.getHost().addChild(standardContext);
            // 创建Servlet
            tomcat.addServlet("/gateway", "hystrixMetricsStreamServlet", streamServlet);
            // Servlet映射
            standardContext.addServletMappingDecoded("/breaker.stream", "hystrixMetricsStreamServlet");
            //启动tomcat容器
            try {
                tomcat.start();
            } catch (LifecycleException e) {
                log.error("tomcat start fail err:{}",e.getMessage(),e);
            }
            tomcat.getServer().await();
        }
    }
}
