package com.turingdi.awp.router.impl;

import com.turingdi.awp.router.SubRouterFactory;
import com.turingdi.awp.router.bms.LoginSubRouter;
import com.turingdi.awp.router.bms.OfficialAccountSubRouter;
import com.turingdi.awp.router.bms.PaySettingSubRouter;
import com.turingdi.awp.router.api.*;
import io.vertx.core.Handler;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-31 10:23.
 */
public class SubRouterFactoryImpl implements SubRouterFactory {
    private Handler<RoutingContext> jwtHandler;
    private JWTAuth jwtProvider;

    public static SubRouterFactory of(JWTAuth jwtProvider){
        return new SubRouterFactoryImpl(jwtProvider);
    }

    private SubRouterFactoryImpl(JWTAuth jwtProvider){
        this.jwtProvider = jwtProvider;
        this.jwtHandler = JWTAuthHandler.create(jwtProvider);
    }

    @Override
    public Router create(SubRouterType type) {
        Router router = null;
        switch (type){
            case WECHAT_OAUTH:
                router = WechatOauthSubRouter.create();
                break;
            case ALIPAY_OAUTH:
                router = AlipayOauthSubRouter.create();
                break;
            case WECHAT_PAY:
                router = WechatPaySubRouter.create();
                break;
            case ALIPAY_PAY:
                router = AlipayPaySubRouter.create();
                break;
            case WECHAT_MSG:
                router = WechatMessageSubRouter.create();
                break;
            case ALIPAY_MSG:
                router = null;
                break;
            case WECHAT_TOKEN:
                router = TokenSubRouter.create();
                break;
            case ALIPAY_TOKEN:
                router = null;
                break;
            case BMS_LOGIN:
                router = LoginSubRouter.create(jwtProvider);
                break;
            case BMS_ACCOUNT:
                router = OfficialAccountSubRouter.create(jwtHandler);
                break;
            case BMS_PAY:
                router = PaySettingSubRouter.create(jwtHandler);
                break;
        }
        return router;
    }
}
