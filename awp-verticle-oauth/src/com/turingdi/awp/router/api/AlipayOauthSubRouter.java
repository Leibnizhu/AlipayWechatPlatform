package com.turingdi.awp.router.api;

import com.turingdi.awp.router.SubRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支付宝授权的Controller/SubRouter
 *
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:54.
 */
public class AlipayOauthSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Vertx vertx;

    public static Router create(){
        return new AlipayOauthSubRouter().subRouter();
    }

    @Override
    public Router subRouter() {
        Router zfbOauthRouter = Router.router(vertx);
//        zfbOauthRouter.get("/oauth/:body").handler(this::applyForOauth);//申请授权
        return zfbOauthRouter;
    }

    /*
     * TODO 支付宝授权
     */
}
