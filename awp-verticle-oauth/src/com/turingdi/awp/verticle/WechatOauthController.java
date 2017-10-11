package com.turingdi.awp.verticle;

import com.turingdi.awp.entity.Account;
import com.turingdi.awp.service.AccountService;
import com.turingdi.awp.util.Constants;
import com.turingdi.awp.util.NetworkUtils;
import com.turingdi.awp.util.TuringBase64Util;
import net.sf.json.JSONObject;
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
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:54.
 */
@Controller
@RequestMapping("wxOauth")
public class WechatOauthController {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService wxAccServ;
    private Constants constants;

    @Autowired
    public WechatOauthController(AccountService wxAccServ, Constants constants) {
        this.wxAccServ = wxAccServ;
        this.constants = constants;
    }

    /**
     * 授权完成后，跳转到原访问地址时，需要去掉的请求参数（无需填写"code"，"state"，"visitUrl"
     */
    private static final String[] REMOVE_PARAMS = {"appid", "appsecret", "eid"};

    /**
     * 申请微信授权
     * /awp/wxOauth/apply/{body}
     * web服务需要授权时，向用户发送重定向，重定向到当前接口
     * 参数只有一个，内容为JSON，请用http://localhost:8083/awp/base64.html进行加密
     * {
     *     "eid":web项目使用的公众号在本项目中的用户ID
     *     "type":0=静默授权，只能获取OpenID，1=正常授权，会弹出授权确认页面，可以获取到用户信息
     *     "callback":授权成功后调用的web项目回调接口地址,请使用完整地址,
     *                  回调时会使用GET方法，加上rs参数，
     *                  如果静默授权，rs参数内容就是openid
     *                  如果正常授权，rs参数内容是turingBase64加密的授权结果(JSON)
     * }
     *
     * @param body json格式的请求数据,用TURINGBase64进行编码
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value = "apply/{body}", method = GET)
    public String applyForOauth(@PathVariable String body) throws UnsupportedEncodingException {
        body = TuringBase64Util.decode(body);
        JSONObject reqJson = JSONObject.fromObject(body);
        int eid = reqJson.getInt("eid");
        int type = reqJson.getInt("type");
        String callback = TuringBase64Util.encode(reqJson.getString("callback"));
        Account account = wxAccServ.getById(eid);
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
            Account account = wxAccServ.getById(eid);
            String openIdUrl = String.format(OPENID_API, account.getAppid(), account.getAppsecret(), code);
            JSONObject openIdJson = JSONObject.fromObject(NetworkUtils.postRequestWithData(openIdUrl, null, null));
            log.debug("授权返回的json数据：{}", openIdJson);
            //重定向到原访问URL
            if (openIdJson.containsKey("openid")) {
                String openId = openIdJson.getString("openid");
                String visitUrl = getRedirectAddr(request, REMOVE_PARAMS);
                if (visitUrl.length() > 0) {
                    return "redirect:" + visitUrl + (visitUrl.contains("?") ? "&rs=" : "?rs=") + openId;
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
            Account account = wxAccServ.getById(eid);
            String openIdUrl = String.format(OPENID_API, account.getAppid(), account.getAppsecret(), code);
            JSONObject openIdJson = JSONObject.fromObject(NetworkUtils.postRequestWithData(openIdUrl, null, null));
            log.debug("授权返回的json数据：{}", openIdJson);
            if (openIdJson.containsKey("openid")) {
                String openId = openIdJson.getString("openid");
                JSONObject userInfoJson = null;
                try {
                    String userinfo_url = String.format(USERINFO_API, openIdJson.getString("access_token"), openId);
                    String strJson = NetworkUtils.postRequestWithData(userinfo_url, null, null);
                    userInfoJson = JSONObject.fromObject(strJson);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //重定向到原访问URL
                String visitUrl = getRedirectAddr(request, REMOVE_PARAMS);
                if (visitUrl.length() > 0) {
                    if (null != userInfoJson) {
                        visitUrl = TuringBase64Util.decode(visitUrl).replaceAll("[\\s*\t\n\r]", "");
                        log.debug("当前授权的用户信息:{}", userInfoJson.toString());
                        return "redirect:" + visitUrl + (visitUrl.contains("?") ? "&rs=" : "?rs=") + TuringBase64Util.encode(userInfoJson.toString());
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
