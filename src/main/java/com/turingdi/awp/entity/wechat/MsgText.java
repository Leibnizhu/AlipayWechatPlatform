package com.turingdi.awp.entity.wechat;


/**
 * 文本消息
 * 
 *
 */
public class MsgText extends MsgBase{

	private String content;//消息内容
	private Integer baseId;//消息主表id
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getBaseId() {
		return baseId;
	}
	public void setBaseId(Integer baseId) {
		this.baseId = baseId;
	}

}