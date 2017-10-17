package com.turingdi.awp.util.wechat;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.turingdi.awp.entity.wechat.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

/**
 * xml 消息处理工具类
 * 
 */

@SuppressWarnings("unchecked")
public class MsgXmlUtil {

	//将request 消息 转换成 请求消息对象
	
	public static MsgRequest parseXml(HttpServletRequest request) throws Exception {
		MsgRequest msgReq = new MsgRequest();
		
		// 解析XML
		InputStream inputStream = request.getInputStream();
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		
		// 遍历节点，封装成对象
		for (Element e : elementList){
			String name = e.getName();
			String text = e.getText();
			
			if("MsgType".equals(name)){
				msgReq.setMsgType(text);
			}else if("MsgId".equals(name)){
				msgReq.setMsgId(text);
			}else if("FromUserName".equals(name)){
				msgReq.setFromUserName(text);
			}else if("ToUserName".equals(name)){
				msgReq.setToUserName(text);
			}else if("CreateTime".equals(name)){
				msgReq.setCreateTime(text);
				
			}else if("Content".equals(name)){//文本消息
				msgReq.setContent(text);
			}else if("PicUrl".equals(name)){//图片消息
				msgReq.setPicUrl(text);
			}else if("Location_X".equals(name)){//地理位置消息
				msgReq.setLocation_X(text);
			}else if("Location_Y".equals(name)){
				msgReq.setLocation_Y(text);
			}else if("Scale".equals(name)){
				msgReq.setScale(text);
			}else if("Label".equals(name)){
				msgReq.setLabel(text);
			}else if("Event".equals(name)){//事件消息
				msgReq.setEvent(text);
			}else if("EventKey".equals(name)){
				msgReq.setEventKey(text);
			}
		}
		
		inputStream.close();
		inputStream = null;
		return msgReq;
	}
	
	public static String textToXml(MsgResponseText text) {
		xstream.alias("xml", text.getClass());
		return xstream.toXML(text);
	}
	
	public static String imageToXml(MsgResponseImage image) {
		xstream.alias("xml", image.getClass());
		return xstream.toXML(image);
	}

	public static String voiceToXml(MsgResponseVoice voice) {
		xstream.alias("xml", voice.getClass());
		return xstream.toXML(voice);
	}

	public static String videoToXml(MsgResponseVideo video) {
		xstream.alias("xml", video.getClass());
		return xstream.toXML(video);
	}

	public static String musicToXml(MsgResponseMusic music) {
		xstream.alias("xml", music.getClass());
		return xstream.toXML(music);
	}

	public static String newsToXml(MsgResponseNews news) {
		xstream.alias("xml", news.getClass());
		xstream.alias("item", new Article().getClass());
		return xstream.toXML(news);
	}
	
	/**
	 * 扩展xstream，让xml节点增加CDATA标记
	 */
	public static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				boolean CDATA = true;
				
				@SuppressWarnings("rawtypes")
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}
				protected void writeText(QuickWriter writer, String text) {
					if (CDATA) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});
	
}
