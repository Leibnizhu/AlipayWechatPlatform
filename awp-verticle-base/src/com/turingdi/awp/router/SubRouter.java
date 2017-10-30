package com.turingdi.awp.router;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * 子路由的接口
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-12 10:11.
 */
public interface SubRouter {
    /**
     * 获取子路由Router对象
     * 用于Router.mountSubRouter()绑定子路由
     */
    Router getSubRouter();
    
    /**
     * 设置VertX对象
     * 在调用getSubRouter()之前，应该先调用g=此方法，以便该接口的实现类进行初始化（通过Vertx产生Router并设置其路由规则）
     */
    SubRouter setVertx(Vertx vertx);
}
