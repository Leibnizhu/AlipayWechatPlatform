package com.turingdi.awp.router.bms;

import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.entity.db.Account.JsonKey.*;
import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * 管理后台的登录Controller/SubRouter
 * 主要用于登录。
 * 至于退出登录，由于使用了JWT授权，无法实现（只能等令牌过期，或者js清空相关cookie/LocalStorage）
 *
 * @author Leibniz.Hu
 * Created on 2017-10-13 12:44.
 */
public class LoginSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final JWTOptions JWT_OPTIONS = new JWTOptions().setExpiresInMinutes(60 * 4);//4小时有效
    private Vertx vertx = Constants.vertx();
    private Router loginRouter;
    private JWTAuth provider;

    public static Router create(JWTAuth jwtProvider) {
        return new LoginSubRouter(jwtProvider).subRouter();
    }

    private LoginSubRouter(JWTAuth jwtProvider) {
        this.provider = jwtProvider;
        loginRouter = Router.router(vertx);
        loginRouter.post("/").handler(this::login);
        loginRouter.post("/reg").handler(this::register);
    }

    @Override
    public Router subRouter() {
        return loginRouter;
    }

    /**
     * 登录。
     * 请求方法：POST
     * 请求参数：username=登录邮箱，password=登录密码的MD5
     * 返回： JSON，
     * 登录失败：{result:fail}
     * 登录成功{result:success,token:*,name:*, role:*,id:*,email:*}
     */
    private void login(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        String email = req.getParam("username");
        String password = req.getParam("password");
        log.debug("有用户(email={})尝试登录，登录密码MD5={}", email, password);
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_EMAIL_LOGIN, email),
                ar -> {
                    if (ar.succeeded()) {
                        JsonObject acc = ar.result().body();
                        JsonObject result = new JsonObject();
                        if (acc == null) {//密码错误或用户不存在
                            log.warn("用户(email={})登录失败，用户不存在", email);
                            result.put("result", "EMAIL_NO_EXIST");
                            resp.end(result.toString());
                        } else if(!password.equals(acc.getString(PASSWORD))){
                            log.warn("用户(email={})登录失败，密码({})不正确", email, password);
                            result.put("result", "PASSWORD_ERROR");
                            resp.end(result.toString());
                        } else {
                            //jwt保存
                            Integer id = acc.getInteger(ID);
                            Integer role = acc.getInteger(ROLE);
                            String name = acc.getString(NAME);
                            String token = provider.generateToken(new JsonObject().put("id", id).put("role", role),
                                    JWT_OPTIONS);
                            log.info("用户({},{})登录成功，ID={},角色={},token={}", name, email, id, role == 0 ? "管理员" : "普通用户", token);
                            result.put("result", "success").put("token", token).put("name", name).put("role", role)
                                    .put("id", id).put("email", acc.getString("email"));
                            resp.end(result.toString());
                        }
                    } else {
                        log.error("EventBus消息响应错误", ar.cause());
                        resp.setStatusCode(500).end("EventBus error!");
                    }
                });
    }

    private void register(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        String email = req.getParam("email");
        String name = req.getParam("name");
        String password = req.getParam("password");
        String rePassword = req.getParam("repassword");

        //参数判定
        if (email == null || email.trim().length() == 0) {
            log.error("输入的邮箱为空");
            resp.setStatusCode(500).end("EMPTY_EMAIL");
            return;
        }
        if (name == null || name.trim().length() == 0) {
            log.error("输入的用户名为空");
            resp.setStatusCode(500).end("EMPTY_NAME");
            return;
        }
        if (password == null || password.trim().length() == 0) {
            log.error("输入的密码为空");
            resp.setStatusCode(500).end("EMPTY_PSWD");
            return;
        }
        if (rePassword == null || rePassword.trim().length() == 0) {
            log.error("输入的重复密码为空");
            resp.setStatusCode(500).end("EMPTY_REPEAT_PSWD");
            return;
        }
        if (!password.trim().equals(rePassword.trim())) {
            log.error("输入的新密码({})与重复密码不一致({})", password, rePassword);
            resp.setStatusCode(500).end("PSWD_NOT_EQUAL");
            return;
        }

        //注册处理
        log.debug("有用户注册请求，email={}登录密码MD5={}。", email, password);
        vertx.eventBus().<Integer>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_REGISTER, email, password, name),
                ar -> {
                    if (ar.succeeded()) {
                        JsonObject result = new JsonObject();
                        Integer id = ar.result().body();
                        if(id == -1){
                            resp.setStatusCode(500).end("DUPLICATE_EMAIL");
                            return;
                        }
                        //jwt保存
                        String token = provider.generateToken(new JsonObject().put("id", id).put("role", 1), JWT_OPTIONS);
                        log.info("用户({},{})注册成功，ID={},角色=普通用户,token={}", name, email, id, token);
                        result.put("result", "success").put("token", token).put("name", name).put("role", 1)
                                .put("id", id).put("email", email);
                        resp.end(result.toString());
                    } else {
                        log.error("EventBus消息响应错误", ar.cause());
                        resp.setStatusCode(500).end("EventBus error!");
                    }
                });
    }
}
