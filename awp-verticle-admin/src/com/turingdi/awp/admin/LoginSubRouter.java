package com.turingdi.awp.admin;

import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.db.AccountService;
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

/**
 * @author Leibniz.Hu
 * Created on 2017-10-13 12:44.
 */
public class LoginSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService wxAccServ;
    private Constants constants;
    private Vertx vertx;
    private JWTAuth provider;

    public LoginSubRouter(AccountService wxAccServ, Constants constants) {
        this.wxAccServ = wxAccServ;
        this.constants = constants;
        if(this.vertx != null){
            this.initJWTProvider();
        }
    }

    private void initJWTProvider() {
        //TODO 从配置文件读取
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", "keystore.jceks") //此处要与生成keystore的时候用的type、keypass一致
                .put("type", "jceks")
                .put("password", "secret"));
        this.provider = JWTAuth.create(vertx, config);
    }

    public JWTAuth getJWTProvider() {
        return provider;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        this.initJWTProvider();
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
                String token = provider.generateToken(new JsonObject().put("id", acc.getInteger("id")), new JWTOptions());
                result.put("result", "success").put("token", token);
                resp.end(result.toString());
            }
        });
    }
}
