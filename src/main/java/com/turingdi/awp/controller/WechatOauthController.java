package com.turingdi.awp.controller;

import com.alibaba.fastjson.JSONObject;
import com.turingdi.awp.entity.WxAccount;
import com.turingdi.awp.service.WxAccountService;
import com.turingdi.awp.util.Constants;
import com.turingdi.awp.util.NetworkUtils;
import com.turingdi.awp.util.TuringBase64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.turingdi.awp.util.Constants.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:54.
 */
@Controller
@RequestMapping("wxOauth")
public class WechatOauthController {
    private Logger log = LoggerFactory.getLogger(getClass());
    private WxAccountService wxAccServ;
    private Constants constants;

    @Autowired
    public WechatOauthController(WxAccountService wxAccServ, Constants constants) {
        this.wxAccServ = wxAccServ;
        this.constants = constants;
    }

    /**
     * 授权完成后，跳转到原访问地址时，需要去掉的请求参数（无需填写"code"，"state"，"visitUrl"
     */
    private static final String[] REMOVE_PARAMS = {"appid", "appsecret", "eid"};

    @RequestMapping("apply/{eid}/{type}/{callback}")
    public String applyForOauth(@PathVariable int eid, @PathVariable int type, @PathVariable String callback) throws UnsupportedEncodingException {
        WxAccount account = wxAccServ.getById(eid);
        String redirectAfterUrl = constants.PROJ_URL + "wxOauth/" + (type == 0 ? "baseCb" : "infoCb") + "?eid=" + eid + "&visitUrl=" + callback;
        String returnUrl = String.format(
                (type == 0 ? OAUTH_BASE_API : OAUTH_INFO_API)
                , account.getAppid(), URLEncoder.encode(redirectAfterUrl, "UTF-8"));
        return "redirect:" + returnUrl;
    }

    @RequestMapping("baseCb")
    public String oauthBaseCallback(HttpServletRequest request, String code, int eid) {
        try {
            log.debug("code={},RemoteAddr={},RemoteHost={}", code, request.getRemoteAddr(), request.getRemoteHost());
            assert code != null;
            WxAccount account = wxAccServ.getById(eid);
            String openIdUrl = String.format(OPENID_API, account.getAppid(), account.getAppsecret(), code);
            JSONObject openIdJson = JSONObject.parseObject(NetworkUtils.postRequestWithData(openIdUrl, null, null));
            log.debug("授权返回的json数据：{}", openIdJson);
            //重定向到原访问URL
            if (openIdJson.containsKey("openid")) {
                String openId = openIdJson.getString("openid");
                String visitUrl = getRedirectAddr(request, REMOVE_PARAMS);
                if (visitUrl.length() > 0) {
                    return "redirect:" + visitUrl + (visitUrl.contains("?") ? "&openid=" : "?openid=") + openId;
                }
            } else if (openIdJson.containsKey("errcode")) {
                //有错误
                return "redirect:" + constants.PROJ_URL + "templates/error.html?st=8&errmsg=" + openIdJson.getString("errmsg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //跳到首页
        return "forward:error";
    }

    @RequestMapping("infoCb")
    public String oauthInfoCallback(HttpServletRequest request, String code, int eid) {
        try {
            log.debug("code={},远程地址={},远程域名={}", code, request.getRemoteAddr(), request.getRemoteHost());
            assert code != null;
            WxAccount account = wxAccServ.getById(eid);
            String openIdUrl = String.format(OPENID_API, account.getAppid(), account.getAppsecret(), code);
            JSONObject openIdJson = JSONObject.parseObject(NetworkUtils.postRequestWithData(openIdUrl, null, null));
            log.debug("授权返回的json数据：{}", openIdJson);
            if (openIdJson.containsKey("openid")) {
                String openId = openIdJson.getString("openid");
                JSONObject userInfoJson = null;
                try {
                    String userinfo_url = String.format(USERINFO_API, openIdJson.getString("access_token"), openId);
                    String strJson = NetworkUtils.postRequestWithData(userinfo_url, null, null);
                    userInfoJson = JSONObject.parseObject(strJson);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //重定向到原访问URL
                String visitUrl = getRedirectAddr(request, REMOVE_PARAMS);
                if (visitUrl.length() > 0) {
                    if (null != userInfoJson) {
                        visitUrl = TuringBase64Util.decode(visitUrl).replaceAll("[\\s*\t\n\r]", "");
                        log.debug("当前授权的用户信息:{}", userInfoJson.toString());
                        return "redirect:" + visitUrl + (visitUrl.contains("?") ? "&userinfo=" : "?userinfo=") + TuringBase64Util.encode(userInfoJson.toString());
                    } else {
                        return "redirect:" + visitUrl;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //跳到首页
        return "forward:error";
    }

    /**
     * 从请求中删掉不需要的请求参数，保留需要的，返回处理后的授权前请求路径
     *
     * @param request      Http请求对象
     * @param removeParams 需要删除的请求参数
     * @return 授权前请求路径，已去掉多余的请求参数
     *
     * @author Leibniz
     */
    private static String getRedirectAddr(HttpServletRequest request, String[] removeParams) {
        String visitUrl = request.getParameter("visitUrl");
        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        if (removeParams != null) {
            for (String param : removeParams) {
                parameterMap.remove(param);
            }
        }
        parameterMap.remove("code");
        parameterMap.remove("state");
        parameterMap.remove("visitUrl");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()[0]).append("&");
        }

        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        String split = visitUrl.contains("?") ? "&" : "?";
        return visitUrl + split + sb.toString();
    }
}
