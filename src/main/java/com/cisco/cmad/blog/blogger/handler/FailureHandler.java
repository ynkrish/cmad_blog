package com.cisco.cmad.blog.blogger.handler;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by kyechcha on 01-May-16.
 */
public class FailureHandler implements Handler<RoutingContext> {

    Logger logger = LoggerFactory.getLogger(FailureHandler.class);

    public static FailureHandler create() {
        return new FailureHandler();
    }

    @Override
    public void handle(RoutingContext rc) {
        int failCode = rc.statusCode();
        logger.error("In FailureHandler, Status code :" + failCode);
        HttpServerResponse response = rc.response();
        response.setStatusCode(failCode).end();
    }
}
