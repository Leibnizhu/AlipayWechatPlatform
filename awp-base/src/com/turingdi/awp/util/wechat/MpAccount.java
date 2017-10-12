package com.turingdi.awp.util.wechat;

import java.io.Serializable;

/**
 * 微信公众号信息
 */
public class MpAccount implements Serializable{
	private static final long serialVersionUID = -6315146640254918207L;

	private Long id;//ID,对应enterprise_user表的id字段
	
	private String account;//账号
	private String appid;//appid
	private String appsecret;//appsecret
	private String url;//验证时用的url
	private String token;//token
	
	//ext
	private Integer msgcount;//自动回复消息条数;默认是5条

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppsecret() {
		return appsecret;
	}
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Integer getMsgcount() {
		if(msgcount == null)
			msgcount = 5;//默认5条
		return msgcount;
	}
	public void setMsgcount(Integer msgcount) {
		this.msgcount = msgcount;
	}
	
}
