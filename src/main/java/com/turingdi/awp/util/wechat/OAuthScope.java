package com.turingdi.awp.util.wechat;

/**
 * 消息类型
 * 
 */

public enum OAuthScope {

	Base("snsapi_base"),//用户openid
	Userinfo("userinfo");//用户信息
	
	private String name;
	
	private OAuthScope(String name) {
	     this.name = name;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
