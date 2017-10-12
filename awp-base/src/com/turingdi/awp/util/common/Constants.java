package com.turingdi.awp.util.common;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:56.
 */
public class Constants {

    public final static String PROJ_URL = "http://itq46u.natappfree.cc/";

    public final static String CERT_DIR = "/home/leibniz/";

    /**
     * 微信公众号授权URL，scope=snsapi_userinfo，可用于获取用户信息
     */
    public final static String OAUTH_INFO_API = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo#wechat_redirect";

    /**
     * 微信公众号授权URL，scope=snsapi_base，只能用于获取用户OpenID
     */
    public final static String OAUTH_BASE_API = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base#wechat_redirect";

    /**
     * 微信公众号获取OpenID的API地址
     */
    public static final String OPENID_API = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 微信公众号获取用户信息的API地址
     */
    public static final String USERINFO_API = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
}
