package com.turingdi.awp.entity.alipay;

import io.vertx.core.json.JsonObject;

/**
 * 发送的模板消息对象
 */
public class TemplateMessage {
	private String openid;//粉丝id
	private String templateId;//模板id
	private String url;//链接
	private String color = "#173177";//关键字颜色
	private String headColor = "#173177";//标题颜色
	private JsonObject dataMap;//参数数据

	// 构造方法
	public TemplateMessage(String openid, String templateId, String url, JsonObject dataMap) {
		this.openid = openid;
		this.templateId = templateId;
		this.url = url;
		this.dataMap = dataMap;
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

	public String getColor() {
		return color;
	}

	public TemplateMessage setColor(String color) {
		this.color = color;
		return this;
	}

	public String getHeadColor() {
		return headColor;
	}

	public TemplateMessage setHeadColor(String headColor) {
		this.headColor = headColor;
		return this;
	}

	public JsonObject getDataMap() {
		return dataMap;
	}

	public TemplateMessage setDataMap(JsonObject dataMap) {
		this.dataMap = dataMap;
		return this;
	}

	/**
	 * 重写toString方法；
	 * 方法将模板消息的内容一层一层封装成json对象，然后将最后的json对象转换成json字符串；
	 *
	 * @return json字符串
	 * Create by quandong
	 */
	@Override
	public String toString(){
		JsonObject jsObj = new JsonObject();
		JsonObject template = new JsonObject();
		JsonObject context = new JsonObject();

		// 构造参数数据的json对象
		if(dataMap != null){
			// 遍历参数数据，将每个item封装成json对象然后放到context的json对象中
			for(String key : dataMap.fieldNames()) {
				JsonObject item = new JsonObject(); // 用于存储一个item的json对象
				item.put("value", dataMap.getString(key)); // 每个item的值
				item.put("color", color); // item的颜色
				context.put(key,item); // 将item添加到context的json对象中
			}
		}
		context.put("url", url); // 添加跳转url
		context.put("head_color", headColor); // 添加标题颜色
		context.put("action_name", "点击查看详情"); // 提示信息

		// 将模版ID和内容添加到模板json对象中
		template.put("template_id", templateId);
		template.put("context", context);

		// 添加发送用户和模版ID
		jsObj.put("to_user_id", openid);
		jsObj.put("template", template);
		return jsObj.toString(); // 将json对象转换为字符串并返回
	}
}