package com.turingdi.awp.entity.alipay;

/**
 * 该类是存放生活号（原服务号）账号的信息，包括appId、应用私钥、支付宝公钥、回调Url、通知Url和重定向Url等；
 * Created by quandong on 17-6-21.
 */
public class AliAccountInfo {
    private String appId; // 应用号
    private String appPrivateKey; // 应用私钥
    private String alipayPublicKey; // 支付宝公钥
    private String returnUrl; // 回调Url，手机网页支付用到，不需用到时可置空
    private String notifyUrl; // 通知Url，手机网页支付用到，不需用到时可置空
    private String redirectUrl; // 重定向Url，授权用到，不需用到时可置空

    // 构造方法
    public AliAccountInfo(String appId, String appPrivateKey, String alipayPublicKey, String returnUrl, String notifyUrl, String redirectUrl) {
        this.appId = appId;
        this.appPrivateKey = appPrivateKey;
        this.alipayPublicKey = alipayPublicKey;
        this.returnUrl = returnUrl;
        this.notifyUrl = notifyUrl;
        this.redirectUrl = redirectUrl;
    }

    // 下面是属性的setter和getter方法
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppPrivateKey() {
        return appPrivateKey;
    }

    public void setAppPrivateKey(String appPrivateKey) {
        this.appPrivateKey = appPrivateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}