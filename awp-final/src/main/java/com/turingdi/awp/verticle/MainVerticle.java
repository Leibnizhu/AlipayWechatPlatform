package com.turingdi.awp.verticle;

import com.turingdi.awp.db.pool.HikariCPManager;
import com.turingdi.awp.router.admin.LoginSubRouter;
import com.turingdi.awp.router.admin.OfficialAccountSubRouter;
import com.turingdi.awp.router.admin.PaySettingSubRouter;
import com.turingdi.awp.router.api.*;
import com.turingdi.awp.util.common.Constants;
import com.turingdi.awp.util.common.NetworkUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主Verticle，工作：
 * 1. 初始化公共资源
 * 2. 部署其他DB相关的Verticle
 * 3. 配置Router并n启动Web服务器，监听
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-11 20:37.
 */
public class MainVerticle extends AbstractVerticle {
    private Handler<RoutingContext> jwtHandler;
    private JWTAuth jwtProvider;
    private Router mainRouter;

    @Override
    public void start() throws Exception {
        super.start();
        initComponents();//初始化工具类/组件
        deployDAOVerticles();//部署数据库访问的Verticle
        mountSubRouters();//挂载所有子路由
        startServer();//启动服务器
    }

    /**
     * 初始化工具类/组件
     */
    private void initComponents() {
        Constants.init(context);
        NetworkUtils.init();
        HikariCPManager.init();
        jwtProvider = initJWTProvider();
        jwtHandler = JWTAuthHandler.create(jwtProvider);
        mainRouter = Router.router(vertx);
    }

    /**
     * 初始化JWT
     */
    private JWTAuth initJWTProvider() {
        JsonObject jwtConfig = new JsonObject().put("keyStore", new JsonObject()
                .put("path", config().getString("keyPath", "keystore.jceks")) //此处要与生成keystore的时候用的type、keypass一致
                .put("type", "jceks")
                .put("password", config().getString("keyPswd", "secret")));
        return JWTAuth.create(vertx, jwtConfig);
    }

    /**
     * 部署数据库访问的Verticle
     */
    private void deployDAOVerticles() {
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        vertx.deployVerticle(AccountDBVerticle.class.getName(), options);
        vertx.deployVerticle(OrderDBVerticle.class.getName(), options);
    }

    /**
     * 挂载所有子路由
     */
    private void mountSubRouters() {
        //请求体解析
        mainRouter.route().handler(BodyHandler.create());
        //静态资源路由
        mainRouter.route("/static/*").handler(StaticHandler.create().setWebRoot("static"));
        mainRouter.route("/favicon.ico").handler(this::getLogo);
        mainRouter.route("/MP_verify_*").handler(this::getWechatVerify);
        //授权服务的子路由
        mainRouter.mountSubRouter("/oauth/wx", WechatOauthSubRouter.create());
        //TODO 支付宝授权     mainRouter.mountSubRouter("/oauth/zfb", AlipayOauthSubRouter.create());
        //支付服务子路由
        mainRouter.mountSubRouter("/pay/wx", WechatPaySubRouter.create());
        mainRouter.mountSubRouter("/pay/zfb", AlipayPaySubRouter.create());
        //消息发送服务子路由
        mainRouter.mountSubRouter("/msg/wx", WechatMessageSubRouter.create());
        //TODO 支付宝消息发送
        //JsTicket和AccessTOken服务子路由
        mainRouter.mountSubRouter("/tk/wx", TokenSubRouter.create());
        //登录BMS的子路由
        mainRouter.mountSubRouter("/bms/login", LoginSubRouter.create(jwtProvider));
        //公众号配置子路由
        mainRouter.mountSubRouter("/bms/offAcc", OfficialAccountSubRouter.create(jwtHandler));
        //支付配置子路由
        mainRouter.mountSubRouter("/bms/pay", PaySettingSubRouter.create(jwtHandler));
    }

    /**
     * 启动服务器
     */
    private void startServer() {
        vertx.createHttpServer().requestHandler(mainRouter::accept).listen(config().getInteger("serverPort", 8083));
    }

    private static final Pattern WECHAT_VERIFY = Pattern.compile("^MP_verify_(\\w{16})\\.txt$");

    /**
     * 返回微信安全域名验证文件的内容
     */
    private void getWechatVerify(RoutingContext rc) {
        String uri = rc.request().uri().substring(1);
        Matcher matcher = WECHAT_VERIFY.matcher(uri);
        if (matcher.find()) {
            rc.response().end(matcher.group(1));
        } else {
            rc.response().setStatusCode(404).end();
        }
    }

    /**
     * 返回网站LOGO
     */
    private void getLogo(RoutingContext rc) {
        rc.response().sendFile("static/img/favicon.ico").close();
    }
}
