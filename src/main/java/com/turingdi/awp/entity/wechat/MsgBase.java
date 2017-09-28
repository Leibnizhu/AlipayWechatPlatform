package com.turingdi.awp.entity.wechat;

/**
 * 消息基本信息
 * 
 *
 */
public class MsgBase extends BaseEntity {

	private String msgType;//消息类型;
	private String inputCode;//关注者发送的消息
	private String rule;//规则，目前是 “相等”
	private Integer enable;//是否可用
	private Integer readCount;//消息阅读数
	private Integer favourCount;//消息点赞数

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getInputCode() {
		return inputCode;
	}

	public void setInputCode(String inputCode) {
		this.inputCode = inputCode;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}

	public Integer getFavourCount() {
		return favourCount;
	}

	public void setFavourCount(Integer favourCount) {
		this.favourCount = favourCount;
	}
}