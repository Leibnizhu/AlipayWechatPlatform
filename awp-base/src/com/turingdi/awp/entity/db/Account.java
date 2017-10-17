package com.turingdi.awp.entity.db;

import java.util.Date;

public class Account {
    private Long id;
    private String name;
    private String password;
    private String account;
    private String appid;
    private String appsecret;
    private String verify;
    private String url;
    private String token;
    private Date createTime;
    private Integer wxPayOn;
    private String mchUrl;
    private String mchKey;
    private String mchId;
    private Integer zfbPayOn;
    private String zfbAppId;
    private String zfbPrivKey;
    private String zfbPubKey;

    public String getVerify() {
        return verify;
    }

    public Account setVerify(String verify) {
        this.verify = verify;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getZfbAppId() {
        return zfbAppId;
    }

    public Account setZfbAppId(String zfbAppId) {
        this.zfbAppId = zfbAppId;
        return this;
    }

    public String getZfbPrivKey() {
        return zfbPrivKey;
    }

    public Account setZfbPrivKey(String zfbPrivKey) {
        this.zfbPrivKey = zfbPrivKey;
        return this;
    }

    public String getZfbPubKey() {
        return zfbPubKey;
    }

    public Account setZfbPubKey(String zfbPubKey) {
        this.zfbPubKey = zfbPubKey;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Account setName(String name) {
        this.name = name;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public Account setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getAppid() {
        return appid;
    }

    public Account setAppid(String appid) {
        this.appid = appid;
        return this;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public Account setAppsecret(String appsecret) {
        this.appsecret = appsecret;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Account setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getToken() {
        return token;
    }

    public Account setToken(String token) {
        this.token = token;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Account setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getMchUrl() {
        return mchUrl;
    }

    public Account setMchUrl(String mchUrl) {
        this.mchUrl = mchUrl;
        return this;
    }

    public String getMchKey() {
        return mchKey;
    }

    public Account setMchKey(String mchKey) {
        this.mchKey = mchKey;
        return this;
    }

    public String getMchId() {
        return mchId;
    }

    public Account setMchId(String mchId) {
        this.mchId = mchId;
        return this;
    }

    public Integer getWxPayOn() {
        return wxPayOn;
    }

    public Account setWxPayOn(Integer wxPayOn) {
        this.wxPayOn = wxPayOn;
        return this;
    }

    public Integer getZfbPayOn() {
        return zfbPayOn;
    }

    public Account setZfbPayOn(Integer zfbPayOn) {
        this.zfbPayOn = zfbPayOn;
        return this;
    }
}
