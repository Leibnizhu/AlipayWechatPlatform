package com.turingdi.awp.entity.wechat;

import io.vertx.core.json.JsonObject;

import java.io.UnsupportedEncodingException;

/**
 * 账号粉丝用户信息
 * 
 */
public class AccountFans extends BaseEntity {

	private String openId;//openId，每个用户都是唯一的
	private Integer subscribeStatus;//订阅状态
	private String subscribeTime;//订阅时间
	private byte[] nickname;//昵称,二进制保存emoji表情
	private String nicknameStr;//昵称显示
	private String wxid;//微信号
	private Integer gender;//性别 0-女；1-男；2-未知
	private String language;//语言
	private String country;//国家
	private String province;//省
	private String city;//城市
	private String headimgurl;//头像
	private String remark;//备注
	private Integer status;//用户状态 1-可用；0-不可用
	private Integer enterpriseid;//对应企业用户的ID
	private Integer concern;
	private Integer charm;
	private Integer talent;
	private Integer firstcampid;//用户首次授权时候参与的活动ID

	public static AccountFans fromJson(JsonObject json){
		AccountFans user = new AccountFans();
		user.setOpenId(json.getString("openid"));
		user.setNickname(json.getString("nickname").getBytes());
		user.setHeadimgurl(json.getString("headimgurl"));
		user.setCountry(json.getString("country"));
		user.setProvince(json.getString("province"));
		user.setCity(json.getString("city"));
		user.setGender(json.getInteger("sex"));
		user.setLanguage(json.getString("language"));
		return user;
	}

	public Integer getFirstcampid() {
		return firstcampid;
	}

	public AccountFans setFirstcampid(Integer firstcampid) {
		this.firstcampid = firstcampid;
		return this;
	}

	public Integer getCharm() {
		return charm;
	}

	public void setCharm(Integer charm) {
		this.charm = charm;
	}

	public Integer getTalent() {
		return talent;
	}

	public void setTalent(Integer talent) {
		this.talent = talent;
	}

	public Integer getConcern() {
		return concern;
	}

	public void setConcern(Integer concern) {
		this.concern = concern;
	}

	public Integer getEnterpriseid() {
		return enterpriseid;
	}

	public AccountFans setEnterpriseid(Integer enterpriseid) {
		this.enterpriseid = enterpriseid;
		return this;
	}

	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getWxid() {
		return wxid;
	}
	public void setWxid(String wxid) {
		this.wxid = wxid;
	}
	public Integer getSubscribeStatus() {
		return subscribeStatus;
	}
	public void setSubscribeStatus(Integer subscribeStatus) {
		this.subscribeStatus = subscribeStatus;
	}
	public String getSubscribeTime() {
		return subscribeTime;
	}
	public void setSubscribeTime(String subscribeTime) {
		this.subscribeTime = subscribeTime;
	}
	public byte[] getNickname() {
		return nickname;
	}
	public void setNickname(byte[] nickname) {
		this.nickname = nickname;
	}
	public String getNicknameStr() {
		if(this.getNickname() != null){
			try {
				this.nicknameStr = new String(this.getNickname(),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return nicknameStr;
	}
	public void setNicknameStr(String nicknameStr) {
		this.nicknameStr = nicknameStr;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getHeadimgurl() {
		return headimgurl;
	}
	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}