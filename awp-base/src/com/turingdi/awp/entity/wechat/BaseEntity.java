package com.turingdi.awp.entity.wechat;

import java.util.Date;

public class BaseEntity {
	private Long id;
	private Date createTime = new Date();//创建时间
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatetime() {
		return createTime;
	}

	public void setCreatetime(Date createTime) {
		this.createTime = createTime;
	}
}
