package com.turingdi.awp.entity.wechat;



/**
 * 图文消息
 * 
 *
 */
public class MsgNews extends MsgBase{
	private String title;// 标题
	private String author;// 作者
	private String brief;// 简介
	private String description;// 描述
	private String picPath;// 封面图片
	private Integer showPic;// 是否显示图片
	private String url;// 图文消息原文链接
	private String fromurl;// 外部链接
	private Long base_id;// 消息主表id
	private Integer enterprise_id;// 用户ID


	//以下属性为非数据库字段
	private String createTimeStr;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public Integer getShowPic() {
		return showPic;
	}

	public void setShowPic(Integer showPic) {
		this.showPic = showPic;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFromurl() {
		return fromurl;
	}

	public void setFromurl(String fromurl) {
		this.fromurl = fromurl;
	}

	public Long getBase_id() {
		return base_id;
	}

	public void setBase_id(Long base_id) {
		this.base_id = base_id;
	}

	public Integer getEnterprise_id() {
		return enterprise_id;
	}

	public void setEnterprise_id(Integer enterprise_id) {
		this.enterprise_id = enterprise_id;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
}