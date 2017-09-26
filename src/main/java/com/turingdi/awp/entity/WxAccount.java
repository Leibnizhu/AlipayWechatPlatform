package com.turingdi.awp.entity;

import java.util.Date;

public class WxAccount {
	private Long  id ;
	private String  name  ;
	private String  account  ;
	private String  appid  ;
	private String  appsecret  ;
	private String  url  ;
	private String  token  ;
	private int  msgCount  ;
	private Date  createTime  ;
	private String  type  ;
	private String  encodingAESKey  ;
	private String wxAccount;
	private String msgDomain;//企业用户用于公众号消息的域名
	private int siteid;//piwik采集的siteid
	private String mchUrl;
	private String mchKey;
	private String mchId;
	private int wxPayOn;
	private int zfbPayOn;
	private String zfbAppId;
	private String zfbPrivKey;
	private String zfbPubKey;

	public String getZfbAppId() {
		return zfbAppId;
	}

	public WxAccount setZfbAppId(String zfbAppId) {
		this.zfbAppId = zfbAppId;
		return this;
	}

	public String getZfbPrivKey() {
		return zfbPrivKey;
	}

	public WxAccount setZfbPrivKey(String zfbPrivKey) {
		this.zfbPrivKey = zfbPrivKey;
		return this;
	}

	public String getZfbPubKey() {
		return zfbPubKey;
	}

	public WxAccount setZfbPubKey(String zfbPubKey) {
		this.zfbPubKey = zfbPubKey;
		return this;
	}

	public Long getId() {
		return id;
	}

	public WxAccount setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public WxAccount setName(String name) {
		this.name = name;
		return this;
	}

	public String getAccount() {
		return account;
	}

	public WxAccount setAccount(String account) {
		this.account = account;
		return this;
	}

	public String getAppid() {
		return appid;
	}

	public WxAccount setAppid(String appid) {
		this.appid = appid;
		return this;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public WxAccount setAppsecret(String appsecret) {
		this.appsecret = appsecret;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public WxAccount setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getToken() {
		return token;
	}

	public WxAccount setToken(String token) {
		this.token = token;
		return this;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public WxAccount setMsgCount(int msgCount) {
		this.msgCount = msgCount;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public WxAccount setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public String getType() {
		return type;
	}

	public WxAccount setType(String type) {
		this.type = type;
		return this;
	}

	public String getEncodingAESKey() {
		return encodingAESKey;
	}

	public WxAccount setEncodingAESKey(String encodingAESKey) {
		this.encodingAESKey = encodingAESKey;
		return this;
	}

	public String getWxAccount() {
		return wxAccount;
	}

	public WxAccount setWxAccount(String wxAccount) {
		this.wxAccount = wxAccount;
		return this;
	}

	public String getMsgDomain() {
		return msgDomain;
	}

	public WxAccount setMsgDomain(String msgDomain) {
		this.msgDomain = msgDomain;
		return this;
	}

	public int getSiteid() {
		return siteid;
	}

	public WxAccount setSiteid(int siteid) {
		this.siteid = siteid;
		return this;
	}

	public String getMchUrl() {
		return mchUrl;
	}

	public WxAccount setMchUrl(String mchUrl) {
		this.mchUrl = mchUrl;
		return this;
	}

	public String getMchKey() {
		return mchKey;
	}

	public WxAccount setMchKey(String mchKey) {
		this.mchKey = mchKey;
		return this;
	}

	public String getMchId() {
		return mchId;
	}

	public WxAccount setMchId(String mchId) {
		this.mchId = mchId;
		return this;
	}

	public int getWxPayOn() {
		return wxPayOn;
	}

	public WxAccount setWxPayOn(int wxPayOn) {
		this.wxPayOn = wxPayOn;
		return this;
	}

	public int getZfbPayOn() {
		return zfbPayOn;
	}

	public WxAccount setZfbPayOn(int zfbPayOn) {
		this.zfbPayOn = zfbPayOn;
		return this;
	}
}
