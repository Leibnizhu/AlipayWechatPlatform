package com.turingdi.awp.router.api;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.turingdi.awp.entity.alipay.AliAccountInfo;
import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.alipay.AliPayApi;
import com.turingdi.awp.util.common.Constants;
import com.turingdi.awp.util.common.TuringBase64Util;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.entity.db.Account.JsonKey.*;
import static com.turingdi.awp.router.EventBusNamespace.*;
import static com.turingdi.awp.util.common.Constants.PROJ_URL;

/**
 * 支付宝授权的Controller/SubRouter
 *
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:54.
 */
public class AlipayOauthSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Vertx vertx = Constants.vertx();

    public static Router create() {
        return new AlipayOauthSubRouter().subRouter();
    }

    @Override
    public Router subRouter() {
        Router zfbOauthRouter = Router.router(vertx);
        zfbOauthRouter.get("/apply/:body").handler(this::applyForOauth);//申请支付宝授权
        zfbOauthRouter.route("/baseCb").handler(this::oauthBaseCallback);//静默授权回调
        zfbOauthRouter.route("/infoCb").handler(this::oauthInfoCallback);//用户信息授权回调
        return zfbOauthRouter;
    }

    /**
     * 申请支付宝授权
     * /awp/wxOauth/apply/{body}
     * web服务需要授权时，向用户发送重定向，重定向到当前接口
     * 参数只有一个，内容为JSON，请用http://localhost:8083/awp/base64.html进行加密
     * {
     * "eid":web项目使用的公众号在本项目中的用户ID
     * "type":0=静默授权，只能获取OpenID，1=正常授权，会弹出授权确认页面，可以获取到用户信息
     * "callback":授权成功后调用的web项目回调接口地址,请使用完整地址,
     * 回调时会使用GET方法，加上rs参数，
     * 如果静默授权，rs参数内容就是openid
     * 如果正常授权，rs参数内容是turingBase64加密的授权结果(JSON)
     * }
     *
     * @param rc Vertx的RoutingContext对象
     * @author Leibniz.Hu
     */
    private void applyForOauth(RoutingContext rc) {
        HttpServerResponse resp = rc.response();
        String decodedBody = TuringBase64Util.decode(rc.request().getParam("body"));
        JsonObject reqJson = new JsonObject(decodedBody);
        Integer eid = reqJson.getInteger("eid");
        int type = reqJson.getInteger("type");
        String callback = TuringBase64Util.encode(reqJson.getString("callback"));//授权后回调方法
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), ar -> {
            if (ar.succeeded()) {
                JsonObject acc = ar.result().body();
                String redirectAfterUrl = PROJ_URL + "oauth/zfb/" + (type == 0 ? "baseCb" : "infoCb") + "?eid=" + eid + "&visitUrl=" + callback;
                AliAccountInfo aliAcc = new AliAccountInfo(acc.getString(ZFBAPPID), acc.getString(ZFBPRIVKEY), acc.getString(ZFBPUBKEY), null, null, redirectAfterUrl);
                AliPayApi.auth(aliAcc, resp, type == 1);
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                resp.setStatusCode(500).end("EventBus error!");
            }
        });
    }

    /**
     * 支付宝静默授权的回调方法
     * 由支付宝服务器调用
     *
     * @param rc Vertx的RoutingContext对象
     * @author Leibniz.Hu
     */
    private void oauthBaseCallback(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        Integer eid = Integer.parseInt(req.getParam("eid"));
        getAccountAndExecute(resp, eid, aliAcc -> {
            AlipaySystemOauthTokenResponse oauthRes = AliPayApi.getUserId(aliAcc, req);
            oauthSuccessProcess(req, resp, oauthRes, url -> log.info("授权成功，OpenID={},{},准备跳转到{}", oauthRes.getUserId(), oauthRes.getAlipayUserId(), url));
        });
    }

    /**
     * 支付宝普通授权（获取用户信息）的回调方法
     * 由支付宝服务器调用
     *
     * @param rc Vertx的RoutingContext对象
     * @author Leibniz.Hu
     */
    private void oauthInfoCallback(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        Integer eid = Integer.parseInt(req.getParam("eid"));
        getAccountAndExecute(resp, eid, aliAcc -> {
            AlipayUserInfoShareResponse oauthRes = AliPayApi.getUserDetailInfo(aliAcc, req);
            oauthSuccessProcess(req, resp, oauthRes, url -> log.info("授权成功，OpenID={},准备跳转到{}", oauthRes.getUserId(), url));
        });
    }

    /**
     * 查询账户信息，并创建AliAccountInfo对象，调用回调方法
     *
     * @param resp     HTTP响应对象
     * @param eid      企业用户ID
     * @param callback 创建成功后的回调方法
     * @author Leibniz.Hu
     */
    private void getAccountAndExecute(HttpServerResponse resp, Integer eid, Handler<AliAccountInfo> callback) {
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), ar -> {
            if (ar.succeeded()) {
                JsonObject acc = ar.result().body();
                AliAccountInfo aliAcc = new AliAccountInfo(acc.getString(ZFBAPPID), acc.getString(ZFBPRIVKEY), acc.getString(ZFBPUBKEY), null, null, null);
                callback.handle(aliAcc);
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                resp.setStatusCode(500).end("EventBus error!");
            }
        });
    }

    /**
     * 解析授权前访问的地址，解析所需的json并编码，最后重定向
     *
     * @param req      HTTP请求对象
     * @param resp     HTTP响应对象
     * @param res      ALipay接口返回的响应
     * @param callback 解析地址后的处理方法
     * @author Leibniz.Hu
     */
    private void oauthSuccessProcess(HttpServerRequest req, HttpServerResponse resp, AlipayResponse res, Handler<String> callback) {
        if (res != null) {
            String visitUrl = req.getParam("visitUrl");//getRedirectAddress(request, REMOVE_PARAMS);
            if (visitUrl.length() > 0) {
                visitUrl = TuringBase64Util.decode(visitUrl).replaceAll("[\\s*\t\n\r]", "");
                callback.handle(visitUrl);
                resp.setStatusCode(302).putHeader("Location", visitUrl + (visitUrl.contains("?") ? "&rs=" : "?rs=") + TuringBase64Util.encode(getClearJson(res).toString())).end();
            } else {
                log.error("没有找到授权后回调地址" + req.absoluteURI());
                resp.end("未设置授权后回调地址");
            }
        }
    }

    /**
     * 解析支付宝授权后的响应对象，获取对应简洁的JSON
     *
     * @param res 支付宝授权后的响应对象
     * @return 简洁的JSON，不包含签名及重复的一些字段
     *
     * @author Leibniz.Hu
     */
    private JsonObject getClearJson(AlipayResponse res) {
        JsonObject bodyJson = new JsonObject(res.getBody());
        if (bodyJson.containsKey("alipay_user_info_share_response")) {
            return bodyJson.getJsonObject("alipay_user_info_share_response");
        } else if (bodyJson.containsKey("alipay_system_oauth_token_response")) {
            return bodyJson.getJsonObject("alipay_system_oauth_token_response");
        } else {
            return bodyJson;
        }
    }
}
