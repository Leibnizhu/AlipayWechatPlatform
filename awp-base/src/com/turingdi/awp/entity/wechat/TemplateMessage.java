package com.turingdi.awp.entity.wechat;

import io.vertx.core.json.JsonObject;
import java.util.Map;

/**
 * 发送的模板消息对象
 */
public class TemplateMessage {

	private String openid;//粉丝id
	private String templateId;//模板id
	private String url;//链接
	private String color = "#173177";//颜色
	private Map<String,String> dataMap;//参数数据

	public TemplateMessage(String openid, String templateId, String url, Map<String, String> dataMap) {
		this.openid = openid;
		this.templateId = templateId;
		this.url = url;
		this.dataMap = dataMap;
	}

	public TemplateMessage(){

	}

	public String getOpenid() {
		return openid;
	}

	public TemplateMessage setOpenid(String openid) {
		this.openid = openid;
		return this;
	}

	public String getTemplateId() {
		return templateId;
	}

	public TemplateMessage setTemplateId(String templateId) {
		this.templateId = templateId;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public TemplateMessage setUrl(String url) {
		this.url = url;
		return this;
	}

	public Map<String, String> getDataMap() {
		return dataMap;
	}

	public TemplateMessage setDataMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
		return this;
	}

	public String getColor() {
		return color;
	}

	public TemplateMessage setColor(String color) {
		this.color = color;
		return this;
	}
	
	@Override
	public String toString(){
		JsonObject jsObj = new JsonObject();
		jsObj.put("touser", openid);
		jsObj.put("template_id", templateId);
		jsObj.put("url", url);
		
		JsonObject data = new JsonObject();
		if(dataMap != null){
			for(String key : dataMap.keySet()){
				JsonObject item = new JsonObject();
				item.put("value", dataMap.get(key));
				item.put("color", color);
				data.put(key,item);
			}
		}
		jsObj.put("data", data);
		return jsObj.toString();
	}
	
	
}
