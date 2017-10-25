package com.turingdi.awp.router.admin;

import com.turingdi.awp.router.SubRouter;
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

import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-13 12:44.
 */
public class LoginSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final JWTOptions JWT_OPTIONS = new JWTOptions().setExpiresInMinutes(60 * 4L);//4小时有效
    private Vertx vertx;
    private JWTAuth provider;

    public LoginSubRouter(JWTAuth jwtProvider) {
        this.provider = jwtProvider;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router loginRouter = Router.router(vertx);
        loginRouter.post("/").handler(this::login);
        return loginRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    private void login(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        String email = req.getParam("username");
        String password = req.getParam("password");
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_EMAIL_LOGIN, email, password), ar -> {
            if(ar.succeeded()){
                JsonObject acc = ar.result().body();
                JsonObject result = new JsonObject();
                if (acc == null) {//密码错误或用户不存在
                    result.put("result", "fail");
                    resp.end(result.toString());
                } else {
                    //jwt保存
                    Integer id = acc.getInteger("id");
                    Integer role = acc.getInteger("role");
                    String name = acc.getString("name");
                    String token = provider.generateToken(new JsonObject().put("id", id).put("role", role), JWT_OPTIONS);
                    result.put("result", "success").put("token", token).put("name", name).put("role", role).put("id", id).put("email", acc.getString("email"));
                    resp.end(result.toString());
                }
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                resp.setStatusCode(500).end("EventBus error!");
            }
        });
    }
}
