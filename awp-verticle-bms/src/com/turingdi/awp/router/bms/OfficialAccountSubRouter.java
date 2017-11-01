package com.turingdi.awp.router.bms;

import com.turingdi.awp.router.impl.JwtAccessSubRouter;
import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.entity.db.Account.JsonKey.*;
import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * 用户基本配置的Controller/SubRouter
 * 需要JWT授权才能访问
 * 功能：
 * 1. 获取自己的公众号配置
 * 2. 获取所有用户的列表(管理员)
 * 3. 获取i指定用户的公众号配置(管理员)
 * 4. 更新用户公众号配置
 * 5. 更新登录邮箱/密码
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-13 12:44.
 */
public class OfficialAccountSubRouter extends JwtAccessSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Vertx vertx = Constants.vertx();
    private Router offAccRouter;

    public static Router create(Handler<RoutingContext> jwtHandler){
        return new OfficialAccountSubRouter(jwtHandler).subRouter();
    }

    private OfficialAccountSubRouter(Handler<RoutingContext> jwtHandler) {
        offAccRouter = Router.router(vertx);
        offAccRouter.route("/*").handler(jwtHandler);
        offAccRouter.get("/").handler(this::getSelfAccount);
        offAccRouter.get("/all").handler(this::getAccountList);
        offAccRouter.get("/:id").handler(this::getAccountById);
        offAccRouter.put("/").handler(this::updateOfficialAccount);
        offAccRouter.put("/pswd").handler(this::updateEmailPassword);
    }

    @Override
    public Router subRouter() {
        return offAccRouter;
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
        if (forbidAccess(rc, "id", true)) {
            return;
        }
        int queryId = Integer.parseInt(rc.request().getParam("id"));
        queryAccountAndResponse(rc, queryId);
    }

    /**
     * 按ID查询公众号配置，并返回
     * 失败则返回500错误
     */
    private void queryAccountAndResponse(RoutingContext rc, int queryId) {
        log.debug("即将发送EventBus消息查询(ID={}的)公众号配置", queryId);
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, queryId), ar -> {
            if(ar.succeeded()){
                log.debug("通过EventBus查询(ID={}的)公众号配置成功", queryId);
                responseOneAccount(rc, ar.result().body());
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                rc.response().setStatusCode(500).end("EventBus error!");
            }
        });
    }

    /**
     * 将有效的信息塞入JSON并响应
     * 不是将查询到的json直接返回，是为了避免多余的敏感信息泄露
     */
    private void responseOneAccount(RoutingContext rc, JsonObject offAcc) {
        JsonObject result = new JsonObject()
                .put("id", offAcc.getInteger(ID))
                .put("name", offAcc.getString(NAME))
                .put("appid", offAcc.getString(WXAPPID))
                .put("appsecret", offAcc.getString(WXAPPSECRET))
                .put("verify", offAcc.getString(VERIFY))
                .put("role", offAcc.getInteger(ROLE))
                .put("projUrl", Constants.PROJ_URL);
        rc.response().putHeader("content-type", "application/json; charset=utf-8").end(result.toString());
    }

    /**
     * 查询所有账号
     * 返回JSON包括id,name,email字段
     * 需要管理员权限
     */
    private void getAccountList(RoutingContext rc) {
        if (forbidAccess(rc, "id", false)) {
            return;
        }
        log.debug("即将发送EventBus消息查询所有账号");
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

    /**
     * 更新公众号配置
     * 请求方法：PUT
     * 请求参数：id,name邮箱,appid,appsecret,verify
     * 响应：success或fail
     */
    private void updateOfficialAccount(RoutingContext rc) {
        if (forbidAccess(rc, "id", true)) {
            return;
        }
        HttpServerRequest req = rc.request();
        Long id = Long.valueOf(req.getParam("id"));
        String name = req.getParam("name");
        String appid = req.getParam("appid");
        String appsecret = req.getParam("appsecret");
        String verify = req.getParam("verify");
        JsonObject updateAcc = new JsonObject().put(ID, id).put(NAME, name).put(WXAPPID, appid).put(WXAPPSECRET, appsecret).put(VERIFY, verify);
        log.debug("更新公众号配置：{}", updateAcc);
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

    /**
     * 更新邮箱密码
     * 请求方法：PUT
     * 请求参数：id,oldPassword旧密码,newPassword新密码,rePasswordo重复密码
     * 响应：success或fail
     */
    private void updateEmailPassword(RoutingContext rc) {
        if (forbidAccess(rc, "id", true)) {
            return;
        }
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response().putHeader("content-type", "text/plain; charset=utf-8");
        Long id = Long.valueOf(req.getParam("id"));
        String oldPassword = req.getParam("oldPassword");
        log.debug("即将发送EventBus消息查询(ID={},密码={})是否正确", id, oldPassword);
        Future.<Message<JsonObject>>future(f ->
            vertx.eventBus().send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_ID_LOGIN, id, oldPassword), f)
        ).compose(msg ->
            Future.<Message<Integer>>future(f -> {
                JsonObject acc = msg.body();
                if (acc == null) {
                    log.warn("账户({})不存在或密码({})错误", id, oldPassword);
                    rc.response().end("errPswd");
                    return;
                }
                String email = req.getParam("email");
                String newPassword = req.getParam("newPassword");
                boolean needUpdatePassword = false;
                if (newPassword != null && newPassword.trim().length() > 0) {
                    String rePassword = req.getParam("rePassword");
                    if (rePassword == null || rePassword.trim().length() == 0) {
                        log.error("输入的重复密码为空");
                        resp.setStatusCode(500).end("EMPTY_REPEAT_PSWD");
                        return;
                    }
                    if (!newPassword.trim().equals(rePassword.trim())) {
                        log.error("输入的新密码({})与重复密码不一致({})", newPassword, rePassword);
                        resp.setStatusCode(500).end("PSWD_NOT_EQUAL");
                        return;
                    }
                    needUpdatePassword = true;
                }
                JsonObject updateAcc = new JsonObject().put(ID, id).put(EMAIL, email);
                if (needUpdatePassword) {
                    updateAcc.put(PASSWORD, newPassword);
                }
                vertx.eventBus().send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_UPDATE_NORMAL, updateAcc), f);
            })
        ).setHandler(res -> {
            if(res.succeeded()){
                Integer rows = res.result().body();
                log.info("更新密码/邮箱完毕，影响了{}条数据库记录", rows);
                rc.response().end(rows > 0 ? "success" : "fail");
            } else {
                log.error("更新密码/邮箱时抛出异常", res.cause());
                rc.response().setStatusCode(500).end();
            }
        });
    }
}
