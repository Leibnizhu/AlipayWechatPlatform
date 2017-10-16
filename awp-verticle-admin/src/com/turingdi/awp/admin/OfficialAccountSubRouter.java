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
 * Created on 2017-10-13 12:44.
 */
public class OfficialAccountSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService wxAccServ;
    private JWTAuth provider;
    private Vertx vertx;

    public OfficialAccountSubRouter(AccountService wxAccServ, JWTAuth provider) {
        this.wxAccServ = wxAccServ;
        this.provider = provider;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router offAccRouter = Router.router(vertx);
        offAccRouter.route().handler(JWTAuthHandler.create(provider));
        offAccRouter.get("/").handler(this::getOfficialAccount);
        return offAccRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    private void getOfficialAccount(RoutingContext rc) {
        JsonObject jwtJson = rc.user().principal();
        int userId = jwtJson.getInteger("id");
        wxAccServ.getById(userId, offAcc -> {
            JsonObject result = new JsonObject()
                    .put("id", offAcc.getInteger("id"))
                    .put("name", offAcc.getString("name"))
                    .put("appid", offAcc.getString("appid"))
                    .put("appsecret", offAcc.getString("appsecret"))
                    .put("verify", offAcc.getString("verify"));
            rc.response().putHeader("content-type", "application/json; charset=utf-8").end(result.toString());
        });
    }
}
