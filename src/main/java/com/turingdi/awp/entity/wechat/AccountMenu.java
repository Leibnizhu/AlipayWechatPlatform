package com.turingdi.awp.entity.wechat;

/**
 * 账号菜单
 * 
 */

public class AccountMenu extends BaseEntity{

	private String mtype;//消息类型： click - 事件消息；view - 链接消息 
	
	/**
	 * 事件消息类型；即mtype = click; 系统定义了2中模式  key / fix 
	 * key 即是 inputcode ；
	 * fix 即是 固定消息id，在创建菜单时，用 _fix_开头，方便解析；
	 * 同样的开发者可以自行定义其他事件菜单
	 */
	private Long userid;
	private String eventType;
	private String name;
	private String inputcode;
	private String url;
	private Integer sort;
	private Long parentid;
	private String msgId;
	
	private String parentName;
	private Long gid;

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getMtype() {
		return mtype;
	}
	public void setMtype(String mtype) {
		this.mtype = mtype;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInputcode() {
		return inputcode;
	}
	public void setInputcode(String inputcode) {
		this.inputcode = inputcode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Long getParentid() {
		return parentid;
	}
	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public Long getGid() {
		return gid;
	}
	public void setGid(Long gid) {
		this.gid = gid;
	}
	
}

