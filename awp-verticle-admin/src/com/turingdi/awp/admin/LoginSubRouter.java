package com.turingdi.awp.admin;

import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.db.AccountService;
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

/**
 * @author Leibniz.Hu
 * Created on 2017-10-13 12:44.
 */
public class LoginSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final JWTOptions JWT_OPTIONS = new JWTOptions().setExpiresInMinutes(60 * 4L);//4小时有效
    private AccountService wxAccServ;
    private Vertx vertx;
    private JWTAuth provider;

    public LoginSubRouter(AccountService wxAccServ, JWTAuth jwtProvider) {
        this.wxAccServ = wxAccServ;
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
        String username = req.getParam("username");
        String password = req.getParam("password");
        wxAccServ.login(username, password, acc -> {
            JsonObject result = new JsonObject();
            if(acc == null){//密码错误或用户不存在
                result.put("result", "fail");
                resp.end(result.toString());
            } else {
                //jwt保存
                String token = provider.generateToken(new JsonObject().put("id", acc.getInteger("id")).put("role", acc.getInteger("role")), JWT_OPTIONS);
                result.put("result", "success").put("token", token);
                resp.end(result.toString());
            }
        });
    }
}
