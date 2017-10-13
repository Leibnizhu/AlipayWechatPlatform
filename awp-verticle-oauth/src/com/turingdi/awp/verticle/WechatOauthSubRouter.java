package com.turingdi.awp.verticle;

import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.db.AccountService;
import com.turingdi.awp.util.common.Constants;
import com.turingdi.awp.util.common.NetworkUtils;
import com.turingdi.awp.util.common.TuringBase64Util;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.turingdi.awp.util.common.Constants.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:54.
 */
public class WechatOauthSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService wxAccServ;
    private Constants constants;
    private Vertx vertx;
    public WechatOauthSubRouter(AccountService wxAccServ, Constants constants) {
        this.wxAccServ = wxAccServ;
        this.constants = constants;
    }


    /**
     * 授权完成后，跳转到原访问地址时，需要去掉的请求参数（无需填写"code"，"state"，"visitUrl"
     */
    private static final String[] REMOVE_PARAMS = {"appid", "appsecret", "eid"};
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
     */
    private void applyForOauth(RoutingContext rc) {
        String decodedBody = TuringBase64Util.decode(rc.request().getParam("body"));
        JsonObject reqJson = new JsonObject(decodedBody);
        Integer eid = reqJson.getInteger("eid");
        int type = reqJson.getInteger("type");
        String callback = TuringBase64Util.encode(reqJson.getString("callback"));//授权后回调方法
        wxAccServ.getById(eid, account -> {
            String redirectAfterUrl = constants.PROJ_URL + "wxOauth/" + (type == 0 ? "baseCb" : "infoCb") + "?eid=" + eid + "&visitUrl=" + callback;
            String returnUrl = null;
            try {
                returnUrl = String.format((type == 0 ? OAUTH_BASE_API : OAUTH_INFO_API)
                        , account.getString("appid"), URLEncoder.encode(redirectAfterUrl, "UTF-8"));
            } catch (UnsupportedEncodingException ignored) { //不可能出现的
            }
            rc.response().setStatusCode(302).putHeader("Location", returnUrl).end();
        });
    }

    /**
     * @param rc Vertx的RoutingContext对象
     */
    private void oauthBaseCallback(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String code = request.getParam("code");
        Integer eid = Integer.parseInt(request.getParam("eid"));
        log.debug("code={},远程地址={},远程域名={},绝对URI={}", code, request.remoteAddress(), request.host(), request.absoluteURI());
        assert code != null;
        wxAccServ.getById(eid, account -> {
            String openIdUrl = String.format(OPENID_API, account.getString("appid"), account.getString("appsecret"), code);
            try {
                NetworkUtils.asyncPostJson(vertx, openIdUrl, openIdJson -> {
                    log.debug("授权返回的json数据：{}", openIdJson);
                    //重定向到原访问URL
                    if (openIdJson.containsKey(WECHAT_JSON_OPENID_KEY)) {
                        String openId = openIdJson.getString(WECHAT_JSON_OPENID_KEY);
                        String visitUrl = request.getParam("visitUrl");//getRedirectAddress(request, REMOVE_PARAMS);
                        if (visitUrl.length() > 0) {
                            visitUrl = TuringBase64Util.decode(visitUrl).replaceAll("[\\s*\t\n\r]", "");
                            log.info("授权成功，OpenID={}，准备跳转到{}", openId, visitUrl);
                            response.setStatusCode(302).putHeader("Location", visitUrl + (visitUrl.contains("?") ? "&rs=" : "?rs=") +  TuringBase64Util.encode(openIdJson.toString())).end();
                        } else {
                            log.error("没有找到授权后回调地址" + request.absoluteURI());
                            response.end("未设置授权后回调地址");
                        }
                    } else if (openIdJson.containsKey(WECHAT_JSON_ERRCODE_KEY)) {
                        //有错误
                        response.setStatusCode(302).putHeader("Location", constants.PROJ_URL + "static/pageerror.html?st=8&errmsg=" + openIdJson.getString("errmsg")).end();
                    }
                });
            } catch (IOException e) {
                log.error("静默授权回调方法处理时发生错误！", e);
            }
        });
    }

    /**
     * @param rc Vertx的RoutingContext对象
     */
    private void oauthInfoCallback(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String code = request.getParam("code");
        Integer eid = Integer.parseInt(request.getParam("eid"));
        log.debug("code={},远程地址={},远程域名={},绝对URI={}", code, request.remoteAddress(), request.host(), request.absoluteURI());
        assert code != null;
        wxAccServ.getById(eid, account -> {
            String openIdUrl = String.format(OPENID_API, account.getString("appid"), account.getString("appsecret"), code);
            try {
                NetworkUtils.asyncPostJson(vertx, openIdUrl, openIdJson -> {
                    log.debug("授权返回的json数据：{}", openIdJson);
                    if (openIdJson.containsKey(WECHAT_JSON_OPENID_KEY)) {
                        String openId = openIdJson.getString(WECHAT_JSON_OPENID_KEY);
                        String userinfo_url = String.format(USERINFO_API, openIdJson.getString(WECHAT_JSON_ACCESSTOKEN_KEY), openId);
                        try {
                            NetworkUtils.asyncPostJson(vertx, userinfo_url, userInfoJson -> {
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
                            });
                        } catch (IOException e) {
                            log.error("调用微信用户信息接口时抛出异常！", e);
                        }
                    }
                });
            } catch (IOException e) {
                log.error("静默授权回调方法处理时发生错误！", e);
            }
        });
    }
}
