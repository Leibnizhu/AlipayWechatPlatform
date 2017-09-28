package com.turingdi.awp.entity.wechat;


import com.turingdi.awp.entity.Account;
import com.turingdi.awp.util.CommonUtils;
import com.turingdi.awp.util.MD5Utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-07 13:29.
 */
public class WechatPay {
    private Map<String, Object> map = new TreeMap<>();

    public WechatPay(String prepay_id, Account wxAccount) {
        map.put("appId", wxAccount.getAppid());
        map.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("nonceStr", CommonUtils.getRandomID());
        map.put("package", "prepay_id=" + prepay_id);
        map.put("signType", "MD5");
        map.put("paysign", getWeixinPaySign(map, wxAccount.getMchKey()));
        map.put("packages", prepay_id);
    }

    public static String getWeixinPaySign(Map<String, Object> map, String key) {
        StringBuilder entityBuilder = getSignString(map);
        entityBuilder.append("key=").append(key);

        return MD5Utils.getMD5(entityBuilder.toString()).toUpperCase();
    }

    public static StringBuilder getSignString(Map<String, Object> map) {
        StringBuilder entityBuilder = new StringBuilder();
        //遍历map，分别拿出key和value
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            entityBuilder.append(entry.getKey()).append("=");
            entityBuilder.append(entry.getValue()).append("&");
        }
        return entityBuilder;
    }

    public Map<String, Object> getMap() {
        return map;
    }
}


