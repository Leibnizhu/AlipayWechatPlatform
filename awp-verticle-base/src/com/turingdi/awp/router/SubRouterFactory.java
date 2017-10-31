package com.turingdi.awp.router;

import io.vertx.ext.web.Router;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-31 10:16.
 */
public interface SubRouterFactory {
    Router create(SubRouterType type);

    enum SubRouterType{
        WECHAT_OAUTH,
        ALIPAY_OAUTH,
        WECHAT_PAY,
        ALIPAY_PAY,
        WECHAT_MSG,
        ALIPAY_MSG,
        WECHAT_TOKEN,
        ALIPAY_TOKEN,
        BMS_LOGIN,
        BMS_ACCOUNT,
        BMS_PAY
    }
}
