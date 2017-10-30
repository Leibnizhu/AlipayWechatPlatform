package com.turingdi.awp.router.api;

import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.common.NetworkUtils;
import com.turingdi.awp.util.common.TuringBase64Util;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.turingdi.awp.entity.db.Account.JsonKey.WXAPPID;
import static com.turingdi.awp.entity.db.Account.JsonKey.WXAPPSECRET;
import static com.turingdi.awp.router.EventBusNamespace.*;
import static com.turingdi.awp.util.common.Constants.*;

/**
 * 微信授权的Controller/SubRouter
 *
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:54.
 */
public class WechatOauthSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Vertx vertx;

    private static final String WECHAT_JSON_OPENID_KEY = "openid";
    private static final String WECHAT_JSON_ERRCODE_KEY = "errcode";
    private static final String WECHAT_JSON_ACCESSTOKEN_KEY = "access_token";

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router wxOauthRouter = Router.router(vertx);
        wxOauthRouter.get("/apply/:body").handler(this::applyForOauth);//申请微信授权
        wxOauthRouter.get("/baseCb").handler(this::oauthBaseCallback);//静默授权回调
        wxOauthRouter.get("/infoCb").handler(this::oauthInfoCallback);//用户信息授权回调
        return wxOauthRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    /**
     * 申请微信授权
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
                JsonObject account = ar.result().body();
                String redirectAfterUrl = PROJ_URL + "oauth/wx/" + (type == 0 ? "baseCb" : "infoCb") + "?eid=" + eid + "&visitUrl=" + callback;
                String returnUrl = null;
                try {
                    returnUrl = String.format((type == 0 ? OAUTH_BASE_API : OAUTH_INFO_API)
                            , account.getString(WXAPPID), URLEncoder.encode(redirectAfterUrl, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) { //不可能出现的
                }
                resp.setStatusCode(302).putHeader("Location", returnUrl).end();
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                resp.setStatusCode(500).end("EventBus error!");
            }
        });
    }

    /**
     * 微信静默授权的回调方法
     * 由微信服务器调用
     *
     * @param rc Vertx的RoutingContext对象
     * @author Leibniz.Hu
     */
    private void oauthBaseCallback(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String code = request.getParam("code");
        Integer eid = Integer.parseInt(request.getParam("eid"));
        log.debug("微信静默授权回调方法接收到请求：code={},远程地址={},远程域名={},绝对URI={}", code, request.remoteAddress(), request.host(), request.absoluteURI());
        assert code != null;
        Future.<Message<JsonObject>>future(f ->
                vertx.eventBus().send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), f)
        ).compose(msg -> Future.<JsonObject>future(f -> {
                    JsonObject account = msg.body();
                    String openIdUrl = String.format(OPENID_API, account.getString(WXAPPID), account.getString(WXAPPSECRET), code);
                    NetworkUtils.asyncPostJson(openIdUrl, f);
                })
        ).setHandler(res -> {
            if (res.succeeded()) {
                JsonObject openIdJson = res.result();
                log.debug("授权返回的json数据：{}", openIdJson);
                //重定向到原访问URL
                if (openIdJson.containsKey(WECHAT_JSON_OPENID_KEY)) {
                    String openId = openIdJson.getString(WECHAT_JSON_OPENID_KEY);
                    String visitUrl = request.getParam("visitUrl");//getRedirectAddress(request, REMOVE_PARAMS);
                    if (visitUrl.length() > 0) {
                        visitUrl = TuringBase64Util.decode(visitUrl).replaceAll("[\\s*\t\n\r]", "");
                        log.info("授权成功，OpenID={}，准备跳转到{}", openId, visitUrl);
                        response.setStatusCode(302).putHeader("Location", visitUrl + (visitUrl.contains("?") ? "&rs=" : "?rs=") + TuringBase64Util.encode(openIdJson.toString())).end();
                    } else {
                        log.error("没有找到授权后回调地址" + request.absoluteURI());
                        response.end("未设置授权后回调地址");
                    }
                } else if (openIdJson.containsKey(WECHAT_JSON_ERRCODE_KEY)) {
                    //有错误
                    response.setStatusCode(302).putHeader("Location", PROJ_URL + "static/pageerror.html?st=8&errmsg=" + openIdJson.getString("errmsg")).end();
                }
            } else {
                log.error("抛出异常", res.cause());
                response.setStatusCode(500).end();
            }
        });
    }

    /**
     * 微信普通授权（获取用户信息）的回调方法
     * 由微信服务器调用
     *
     * @param rc Vertx的RoutingContext对象
     * @author Leibniz.Hu
     */
    private void oauthInfoCallback(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String code = request.getParam("code");
        Integer eid = Integer.parseInt(request.getParam("eid"));
        log.debug("微信普通授权回调方法接收到请求：code={},远程地址={},远程域名={},绝对URI={}", code, request.remoteAddress(), request.host(), request.absoluteURI());
        assert code != null;
        Future.<Message<JsonObject>>future(f ->
                vertx.eventBus().send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), f)
        ).compose(msg -> Future.<JsonObject>future(f -> {
                    JsonObject account = msg.body();
                    String openIdUrl = String.format(OPENID_API, account.getString(WXAPPID), account.getString(WXAPPSECRET), code);
                    NetworkUtils.asyncPostJson(openIdUrl, f);
                })
        ).compose(openIdJson -> Future.<JsonObject>future(f -> {
                    log.debug("授权返回的json数据：{}", openIdJson);
                    if (openIdJson.containsKey(WECHAT_JSON_OPENID_KEY)) {
                        String openId = openIdJson.getString(WECHAT_JSON_OPENID_KEY);
                        String userinfo_url = String.format(USERINFO_API, openIdJson.getString(WECHAT_JSON_ACCESSTOKEN_KEY), openId);
                        NetworkUtils.asyncPostJson(userinfo_url, f);
                    }
                })
        ).setHandler(res -> {
            if (res.succeeded()) {
                JsonObject userInfoJson = res.result();
                //重定向到原访问URL
                String visitUrl = request.getParam("visitUrl");//getRedirectAddress(request, REMOVE_PARAMS);
                if (visitUrl.length() > 0) {
                    if (userInfoJson.size() > 0) {
                        visitUrl = TuringBase64Util.decode(visitUrl).replaceAll("[\\s*\t\n\r]", "");
                        log.debug("当前授权的用户信息:{}", userInfoJson.toString());
                        response.setStatusCode(302).putHeader("Location", visitUrl + (visitUrl.contains("?") ? "&rs=" : "?rs=") + TuringBase64Util.encode(userInfoJson.toString())).end();
                    } else {
                        response.setStatusCode(302).putHeader("Location", visitUrl).end();
                    }
                } else {
                    log.error("没有找到授权后回调地址" + request.absoluteURI());
                    response.end("未设置授权后回调地址");
                }
            } else {
                log.error("抛出异常", res.cause());
                response.setStatusCode(500).end();
            }
        });
    }
}
