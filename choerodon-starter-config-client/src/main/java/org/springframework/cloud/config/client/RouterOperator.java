package org.springframework.cloud.config.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 用于动态刷新路由信息的操作类
 * @author zhipeng.zuo
 */
public class RouterOperator {

    private static final Logger logger = LoggerFactory.getLogger(RouterOperator.class);

    private ApplicationEventPublisher publisher;

    private RouteLocator routeLocator;

    @Autowired
    public RouterOperator(ApplicationEventPublisher publisher, RouteLocator routeLocator) {

        this.publisher = publisher;
        this.routeLocator = routeLocator;
    }

    public void refreshRoutes() {
        logger.info("refresh zuul routes");
        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(routeLocator);
        publisher.publishEvent(routesRefreshedEvent);
    }

}

