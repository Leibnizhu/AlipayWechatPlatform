package com.turingdi.awp.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-12 10:11.
 */
public interface SubRouter {
    Router getSubRouter();

    SubRouter setVertx(Vertx vertx);
}
