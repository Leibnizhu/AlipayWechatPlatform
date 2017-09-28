package com.turingdi.awp.entity.wechat;
/*
*@author lqq
*@date创建时间:2016年12月30日
@introduce
*@parameter
*@introduce此类为t_wxcms_wxuser表的实体类
*/
public class WxUser {
	private String openId;//识别微信用户的标识
	private String name;//用户名
	private Integer loginNum;//登录次数
	private Integer transpondNum;//转发次数
	private Integer phone;//手机号
	private String email;//用户邮箱
	private String picture;//用户头像
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getLoginNum() {
		return loginNum;
	}
	public void setLoginNum(Integer loginNum) {
		this.loginNum = loginNum;
	}
	public Integer getTranspondNum() {
		return transpondNum;
	}
	public void setTranspondNum(Integer transpondNum) {
		this.transpondNum = transpondNum;
	}
	public Integer getPhone() {
		return phone;
	}
	public void setPhone(Integer phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public String toString(){
		return "[openId = "+openId+" name = "+name+" picture = "+picture+" loginNum = "+loginNum+" transpondNum = "+transpondNum
				+" phone = "+phone+" email = "+email+"]";
	}
}
