package com.turingdi.awp.entity.wechat;

import com.turingdi.awp.entity.db.Account;
import com.turingdi.awp.util.common.CommonUtils;
import com.turingdi.awp.util.common.SHA1Utils;
import com.turingdi.awp.util.wechat.WxApiClient;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

import java.security.DigestException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 16:42.
 */
public class WechatJdk {
    private Map<String, String> map = new TreeMap<>();

    public WechatJdk(HttpServerRequest request, JsonObject account) {
        this(request, account, null);
    }

    public WechatJdk(HttpServerRequest request, JsonObject account, String url) {
        map.put("noncestr", CommonUtils.getRandomID());
        map.put("jsapi_ticket", WxApiClient.getJSTicket(account.mapTo(Account.class)));
        map.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        if(null != url) {
            map.put("url", url);
        } else {
            map.put("url", request.absoluteURI());
        }
        map.put("signture", getWechatConfigSign(map));
    }

    public Map<String, String> getMap(){
        return map;
    }

    /**
     *  该方法传入随机字符串，微信js-jdk的全局票据，时间戳，url这四个参数，先对4个参数进行首字母排序，然后根据js-jdk签名
     *  规则进行字符串拼接，并对拼完后的字符串进行SHA1签名
     * @param map
     * @return   SHA1签名
     */
    private String getWechatConfigSign(Map<String, String> map) {
        StringBuilder entityBuilder = getSignString(map);
        String signkey = entityBuilder.toString().substring(0, entityBuilder.toString().length()-1);
        //进行sha1签名
        try {
            return SHA1Utils.SHA1(signkey);
        } catch (DigestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private StringBuilder getSignString(Map<String, String> map){
        StringBuilder entityBuilder = new StringBuilder();
        //遍历map，分别拿出key和value
        for (Map.Entry<String, String> entry : map.entrySet()) {
            entityBuilder.append(entry.getKey()).append("=");
            entityBuilder.append(entry.getValue()).append("&");
        }
        return entityBuilder;
    }
}
