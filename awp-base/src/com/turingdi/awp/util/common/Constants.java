package com.turingdi.awp.util.common;

import io.vertx.core.json.JsonObject;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-26 16:56.
 */
public class Constants {

    public static String PROJ_URL;
    public static String CERT_DIR;
    public static String JDBC_URL;
    public static String JDBC_USER;
    public static String JDBC_PSWD;
    public static String JDBC_DRIVER;

    public static void init(JsonObject config) {
        PROJ_URL = config.getString("projectUrl", "http://itq46u.natappfree.cc/");
        CERT_DIR = config.getString("certDir", "/home/leibniz/");
        JDBC_URL = config.getString("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/fission?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false");
        JDBC_USER = config.getString("jdbcUser", "root");
        JDBC_PSWD = config.getString("jdbcPassword", "turingdi");
        JDBC_DRIVER = config.getString("jdbcDriver", "com.mysql.cj.jdbc.Driver");
    }

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
