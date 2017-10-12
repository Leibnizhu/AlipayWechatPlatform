package com.turingdi.awp;

import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.verticle.WechatOauthSubRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-11 20:37.
 */
public class MainVerticle extends AbstractVerticle{
    @Override
    public void start() throws Exception {
        super.start();
        HttpServer server = vertx.createHttpServer();
        Router mainRouter = Router.router(vertx);
        //静态资源路由
        mainRouter.route("/static/*").handler(StaticHandler.create().setWebRoot("static"));
        //微信授权的子路由
        SubRouter wechatOauthRouter = new WechatOauthSubRouter(null, null).setVertx(vertx);
        mainRouter.mountSubRouter("/wxOauth", wechatOauthRouter.getSubRouter());
        //TODO 其他子路由
        //如 mainRouter.mountSubRouter("/……"， ……);
        server.requestHandler(mainRouter::accept).listen(8080);
    }
}
