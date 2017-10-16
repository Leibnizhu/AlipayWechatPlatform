package com.turingdi.awp.admin;

import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.db.AccountService;
import com.turingdi.awp.entity.db.Account;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
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
        offAccRouter.route("/*").handler(JWTAuthHandler.create(provider));
        offAccRouter.get("/").handler(this::getOfficialAccount);
        offAccRouter.get("/all").handler(this::getAccountList);
        offAccRouter.get("/:id").handler(this::getAccountById);
        offAccRouter.put("/").handler(this::updateOfficialAccount);
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
            responseOneAccount(rc, offAcc);
        });
    }

    private void getAccountById(RoutingContext rc) {
        JsonObject jwtJson = rc.user().principal();
        int role = jwtJson.getInteger("role");
        if(role != 0){
            rc.response().setStatusCode(403).end();
            return;
        }
        int queryEid = Integer.parseInt(rc.request().getParam("id"));
        wxAccServ.getById(queryEid, offAcc -> {
            responseOneAccount(rc, offAcc);
        });
    }

    private void responseOneAccount(RoutingContext rc, JsonObject offAcc) {
        JsonObject result = new JsonObject()
                .put("id", offAcc.getInteger("id"))
                .put("name", offAcc.getString("name"))
                .put("appid", offAcc.getString("appid"))
                .put("appsecret", offAcc.getString("appsecret"))
                .put("verify", offAcc.getString("verify"))
                .put("role", offAcc.getInteger("role"))
                .put("projUrl", Constants.PROJ_URL);
        rc.response().putHeader("content-type", "application/json; charset=utf-8").end(result.toString());
    }

    private void getAccountList(RoutingContext rc) {
        wxAccServ.getAccountList(rows -> {
            rc.response().putHeader("content-type", "application/json; charset=utf-8").end(rows.toString());
        });
    }

    private void updateOfficialAccount(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        Long id = Long.valueOf(req.getParam("id"));
        String name = req.getParam("name");
        String appid = req.getParam("appid");
        String appsecret = req.getParam("appsecret");
        String verify = req.getParam("verify");
        Account acc = new Account().setId(id).setName(name).setAppid(appid).setAppsecret(appsecret).setVerify(verify);
        wxAccServ.update(acc, rows -> {
            rc.response().putHeader("content-type", "application/json; charset=utf-8").end(rows > 0 ? "success" : "fail");
        });
    }
}
