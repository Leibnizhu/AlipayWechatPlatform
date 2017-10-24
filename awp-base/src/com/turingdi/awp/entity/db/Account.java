package com.turingdi.awp.entity.db;

import java.util.Date;

public class Account {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Integer role;
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

    public Integer getRole() {
        return role;
    }

    public Account setRole(Integer role) {
        this.role = role;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Account setEmail(String email) {
        this.email = email;
        return this;
    }

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

    public String getZfbappid() {
        return zfbAppId;
    }

    public Account setZfbappid(String zfbAppId) {
        this.zfbAppId = zfbAppId;
        return this;
    }

    public String getZfbprivkey() {
        return zfbPrivKey;
    }

    public Account setZfbprivkey(String zfbPrivKey) {
        this.zfbPrivKey = zfbPrivKey;
        return this;
    }

    public String getZfbpubkey() {
        return zfbPubKey;
    }

    public Account setZfbpubkey(String zfbPubKey) {
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

    public Date getCreatetime() {
        return createTime;
    }

    public Account setCreatetime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getMchurl() {
        return mchUrl;
    }

    public Account setMchurl(String mchUrl) {
        this.mchUrl = mchUrl;
        return this;
    }

    public String getMchkey() {
        return mchKey;
    }

    public Account setMchkey(String mchKey) {
        this.mchKey = mchKey;
        return this;
    }

    public String getMchid() {
        return mchId;
    }

    public Account setMchid(String mchId) {
        this.mchId = mchId;
        return this;
    }

    public Integer getWxpayon() {
        return wxPayOn;
    }

    public Account setWxpayon(Integer wxPayOn) {
        this.wxPayOn = wxPayOn;
        return this;
    }

    public Integer getZfbpayon() {
        return zfbPayOn;
    }

    public Account setZfbpayon(Integer zfbPayOn) {
        this.zfbPayOn = zfbPayOn;
        return this;
    }
}
