package com.turingdi.awp.admin;

import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.db.AccountService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-17 11:50.
 */
public class PaySettingSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService wxAccServ;
    private JWTAuth provider;
    private Vertx vertx;

    public PaySettingSubRouter(AccountService wxAccServ, JWTAuth provider) {
        this.wxAccServ = wxAccServ;
        this.provider = provider;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router paySetRouter = Router.router(vertx);
        paySetRouter.route("/*").handler(JWTAuthHandler.create(provider));
        paySetRouter.get("/:eid").handler(this::getPaySetting);
        return paySetRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    private void getPaySetting(RoutingContext rc) {
        Integer eid = Integer.parseInt(rc.request().getParam("eid"));
        wxAccServ.getById(eid, acc -> {
            JsonObject json = new JsonObject()
                    .put("wx", new JsonObject()
                            .put("appId", acc.getString("appid"))
                            .put("appSecret", acc.getString("appsecret"))
                            .put("mchId", acc.getString("mchId"))
                            .put("payKey", acc.getString("mchKey"))
                            .put("opened", acc.getInteger("wxPayOn")))
                    .put("zfb", new JsonObject()
                            .put("appId", acc.getString("zfbAppId"))
                            .put("appPrivKey", acc.getString("zfbPrivKey"))
                            .put("zfbPubKey", acc.getString("zfbPubKey"))
                            .put("opened", acc.getInteger("zfbPayOn")));
            rc.response().putHeader("content-type", "application/json; charset=utf-8").end(json.toString());
        });
    }
}
