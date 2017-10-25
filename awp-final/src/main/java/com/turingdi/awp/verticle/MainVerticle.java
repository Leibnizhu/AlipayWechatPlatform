package com.turingdi.awp.verticle;

import com.turingdi.awp.db.AccountDao;
import com.turingdi.awp.db.OrderDao;
import com.turingdi.awp.db.pool.HikariCPManager;
import com.turingdi.awp.router.admin.LoginSubRouter;
import com.turingdi.awp.router.admin.OfficialAccountSubRouter;
import com.turingdi.awp.router.admin.PaySettingSubRouter;
import com.turingdi.awp.router.api.*;
import com.turingdi.awp.service.AccountService;
import com.turingdi.awp.service.AlipayPayService;
import com.turingdi.awp.service.OrderService;
import com.turingdi.awp.service.WechatPayService;
import com.turingdi.awp.util.common.Constants;
import com.turingdi.awp.util.common.NetworkUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-11 20:37.
 */
public class MainVerticle extends AbstractVerticle{

    @Override
    public void start() throws Exception {
        super.start();
        HttpServer server = vertx.createHttpServer();
        //公用资源初始化
        Constants.init(config());
        NetworkUtils.init(vertx);
        HikariCPManager.init(vertx);
        JWTAuth jwtProvider = initJWTProvider();
        AccountDao accDao = new AccountDao();
        OrderDao orderDao = new OrderDao();
        AccountService accountSrv = new AccountService(accDao);
        AlipayPayService alipayServ = new AlipayPayService(accDao);
        WechatPayService wxPayServ = new WechatPayService(accDao);
        OrderService orderServ = new OrderService(orderDao);
        Router mainRouter = Router.router(vertx);
        vertx.deployVerticle(AccountDBVerticle.class.getName());
        //请求体解析
        mainRouter.route().handler(BodyHandler.create());
        //静态资源路由
        mainRouter.route("/static/*").handler(StaticHandler.create().setWebRoot("static"));
        mainRouter.route("/favicon.ico").handler(this::getLogo);
        mainRouter.route("/MP_verify_*").handler(this::getWechatVerify);
        //授权服务的子路由
        mainRouter.mountSubRouter("/oauth/wx", new WechatOauthSubRouter(accountSrv).setVertx(vertx).getSubRouter());
// TODO  支付宝授权     mainRouter.mountSubRouter("/oauth/zfb", new WechatOauthSubRouter(accountSrv).setVertx(vertx).getSubRouter());
        //支付服务子路由
        mainRouter.mountSubRouter("/pay/wx", new WechatPaySubRouter(orderServ, wxPayServ, accountSrv).setVertx(vertx).getSubRouter());
        mainRouter.mountSubRouter("/pay/zfb", new AlipayPaySubRouter(orderServ, alipayServ).setVertx(vertx).getSubRouter());
        //消息发送服务子路由
        mainRouter.mountSubRouter("/msg/wx", new WechatMessageSubRouter().setVertx(vertx).getSubRouter());
        //TODO 支付宝消息发送
        //JsTicket和AccessTOken服务子路由
        mainRouter.mountSubRouter("/tk/wx", new TokenSubRouter(accountSrv).setVertx(vertx).getSubRouter());
        //登录BMS的子路由
        mainRouter.mountSubRouter("/bms/login", new LoginSubRouter(accountSrv, jwtProvider).setVertx(vertx).getSubRouter());
        //公众号配置子路由
        mainRouter.mountSubRouter("/bms/offAcc", new OfficialAccountSubRouter(accountSrv, jwtProvider).setVertx(vertx).getSubRouter());
        //支付配置子路由
        mainRouter.mountSubRouter("/bms/pay", new PaySettingSubRouter(accountSrv, jwtProvider).setVertx(vertx).getSubRouter());
        server.requestHandler(mainRouter::accept)
                .listen(config().getInteger("serverPort", 8083));
    }

    private static final Pattern WECHAT_VERIFY = Pattern.compile("^MP_verify_(\\w{16})\\.txt$");

    /**
     * 返回微信安全域名验证文件的内容
     * @param rc
     */
    private void getWechatVerify(RoutingContext rc) {
        String uri = rc.request().uri().substring(1);
        Matcher matcher = WECHAT_VERIFY.matcher(uri);
        if(matcher.find()){
            rc.response().end(matcher.group(1));
        } else {
            rc.response().setStatusCode(404).end();
        }
    }

    /**
     * 返回网站LOGO
     * @param rc
     */
    private void getLogo(RoutingContext rc) {
        rc.response().sendFile("static/img/favicon.ico").close();
    }

    /**
     * 初始化JWT
     * @return
     */
    private JWTAuth initJWTProvider() {
        JsonObject jwtConfig = new JsonObject().put("keyStore", new JsonObject()
                .put("path", config().getString("keyPath","keystore.jceks")) //此处要与生成keystore的时候用的type、keypass一致
                .put("type", "jceks")
                .put("password", config().getString("keyPswd","secret")));
        return JWTAuth.create(vertx, jwtConfig);
    }
}