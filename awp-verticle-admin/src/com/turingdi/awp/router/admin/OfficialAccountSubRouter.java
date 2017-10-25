package com.turingdi.awp.router.admin;

import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-13 12:44.
 */
public class OfficialAccountSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private JWTAuth provider;
    private Vertx vertx;

    public OfficialAccountSubRouter(JWTAuth provider) {
        this.provider = provider;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router offAccRouter = Router.router(vertx);
        offAccRouter.route("/*").handler(JWTAuthHandler.create(provider));
        offAccRouter.get("/").handler(this::getSelfAccount);
        offAccRouter.get("/all").handler(this::getAccountList);
        offAccRouter.get("/:id").handler(this::getAccountById);
        offAccRouter.put("/").handler(this::updateOfficialAccount);
        offAccRouter.put("/pswd").handler(this::updateEmailPassword);
        return offAccRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    /**
     * 查询指定ID的账户信息，无需权限判定
     */
    private void getSelfAccount(RoutingContext rc) {
        JsonObject jwtJson = rc.user().principal();
        int userId = jwtJson.getInteger("id");
        queryAccountAndResponse(rc, userId);
    }

    /**
     * 查询指定ID的账户信息，需要进行权限判定
     */
    private void getAccountById(RoutingContext rc) {
        if (forbidAccess(rc, true)) {
            return;
        }
        int queryId = Integer.parseInt(rc.request().getParam("id"));
        queryAccountAndResponse(rc, queryId);
    }

    private void queryAccountAndResponse(RoutingContext rc, int queryId) {
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, queryId), ar -> {
            if(ar.succeeded()){
                responseOneAccount(rc, ar.result().body());
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                rc.response().setStatusCode(500).end("EventBus error!");
            }
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

    /**
     * @param checkId true=需要管理员权限，或者非管理员但ID相等才允许访问；false=只有管理员允许访问
     * @return true=禁止访问 false=允许访问
     */
    private boolean forbidAccess(RoutingContext rc, boolean checkId) {
        JsonObject jwtJson = rc.user().principal();
        int role = jwtJson.getInteger("role");
        if (checkId) {
            int queryId = Integer.parseInt(rc.request().getParam("id"));
            int jwtId = jwtJson.getInteger("id");
            if (role != 0 && queryId != jwtId) {
                rc.response().setStatusCode(403).end();
                return true;
            }
        } else {
            if ((role != 0)) {
                rc.response().setStatusCode(403).end();
                return true;
            }
        }
        return false;
    }

    private void getAccountList(RoutingContext rc) {
        if (forbidAccess(rc, false)) {
            return;
        }
        vertx.eventBus().<JsonArray>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ALL_ACCOUNT), ar -> {
            HttpServerResponse response = rc.response();
            if(ar.succeeded()){
                JsonArray rows = ar.result().body();
                response.putHeader("content-type", "application/json; charset=utf-8").end(rows.toString());
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                response.setStatusCode(500).end("EventBus error!");
            }
        });
    }

    private void updateOfficialAccount(RoutingContext rc) {
        if (forbidAccess(rc, true)) {
            return;
        }
        HttpServerRequest req = rc.request();
        Long id = Long.valueOf(req.getParam("id"));
        String name = req.getParam("name");
        String appid = req.getParam("appid");
        String appsecret = req.getParam("appsecret");
        String verify = req.getParam("verify");
        JsonObject updateAcc = new JsonObject().put("id", id).put("name", name).put("appid", appid).put("appsecret", appsecret).put("verify", verify);
        vertx.eventBus().<Integer>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_UPDATE_NORMAL, updateAcc), ar -> {
            HttpServerResponse response = rc.response();
            if(ar.succeeded()){
                Integer rows = ar.result().body();
                response.putHeader("content-type", "application/json; charset=utf-8").end(rows > 0 ? "success" : "fail");
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                response.setStatusCode(500).end("EventBus error!");
            }
        });
    }

    private void updateEmailPassword(RoutingContext rc) {
        if (forbidAccess(rc, true)) {
            return;
        }
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response().putHeader("content-type", "text/plain; charset=utf-8");
        Long id = Long.valueOf(req.getParam("id"));
        String oldPassword = req.getParam("oldPassword");
        Future.<Message<JsonObject>>future(f ->
            vertx.eventBus().send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_ID_LOGIN, id, oldPassword), f)
        ).compose(msg ->
            Future.<Message<Integer>>future(f -> {
                JsonObject acc = msg.body();
                if (acc == null) {
                    rc.response().end("errPswd");
                    return;
                }
                String email = req.getParam("email");
                String newPassword = req.getParam("newPassword");
                boolean needUpdatePassword = false;
                if (newPassword != null && newPassword.trim().length() > 0) {
                    String rePassword = req.getParam("rePassword");
                    if (rePassword == null || rePassword.trim().length() == 0) {
                        resp.setStatusCode(500).end("EMPTY_REPEAT_PSWD");
                        return;
                    }
                    if (!newPassword.trim().equals(rePassword.trim())) {
                        resp.setStatusCode(500).end("PSWD_NOT_EQUAL");
                        return;
                    }
                    needUpdatePassword = true;
                }
                JsonObject updateAcc = new JsonObject().put("id", id).put("email", email);
                if (needUpdatePassword) {
                    updateAcc.put("password", newPassword);
                }
                vertx.eventBus().send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_UPDATE_NORMAL, updateAcc), f);
            })
        ).setHandler(res -> {
            if(res.succeeded()){
                Integer rows = res.result().body();
                rc.response().end(rows > 0 ? "success" : "fail");
            }
        });
    }
}
