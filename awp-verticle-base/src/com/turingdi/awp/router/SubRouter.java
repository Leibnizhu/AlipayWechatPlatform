package com.turingdi.awp.router;

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
    Router subRouter();

}
