package com.turingdi.awp.router.bms;

import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.entity.db.Account.JsonKey.ID;
import static com.turingdi.awp.entity.db.Account.JsonKey.NAME;
import static com.turingdi.awp.entity.db.Account.JsonKey.ROLE;
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
    private static final JWTOptions JWT_OPTIONS = new JWTOptions().setExpiresInMinutes(60 * 4L);//4小时有效
    private Vertx vertx = Constants.vertx();
    private Router loginRouter;
    private JWTAuth provider;

    public static Router create(JWTAuth jwtProvider){
        return new LoginSubRouter(jwtProvider).subRouter();
    }

    private LoginSubRouter(JWTAuth jwtProvider) {
        this.provider = jwtProvider;
        loginRouter = Router.router(vertx);
        loginRouter.post("/").handler(this::login);
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
     *      登录失败：{result:fail}
     *      登录成功{result:success,token:*,name:*, role:*,id:*,email:*}
     */
    private void login(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        String email = req.getParam("username");
        String password = req.getParam("password");
        log.debug("有用户(email={})尝试登录，登录密码MD5={}", email, password);
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_EMAIL_LOGIN, email, password),
                ar -> {
                    if (ar.succeeded()) {
                        JsonObject acc = ar.result().body();
                        JsonObject result = new JsonObject();
                        if (acc == null) {//密码错误或用户不存在
                            log.warn("用户(email={})登录失败，密码错误或用户不存在", email);
                            result.put("result", "fail");
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
}
