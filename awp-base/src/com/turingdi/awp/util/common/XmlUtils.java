package com.turingdi.awp.util.common;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:15.
 */
public class XmlUtils {
    public static String simpleMapToXml(Map<String, Object> map, String encode) throws UnsupportedEncodingException {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<xml>");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            xmlBuilder.append("<").append(entry.getKey()).append(">")
                    .append(entry.getValue())
                    .append("</").append(entry.getKey()).append(">");
        }
        xmlBuilder.append("</xml>");
        return new String(xmlBuilder.toString().getBytes(), encode);
    }

    public static String simpleMapToXml(Map<String, Object> map) {
        try {
            return simpleMapToXml(map, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T xml2Object(String xmlStr, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T t = (T) unmarshaller.unmarshal(new StringReader(xmlStr));
            return t;
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析xml数据，并将解析后的数据传入map里面
     *
     * @param xmlData xml数据
     * @return 解析后的数据
     *
     * @author Leibniz
     */
    public static Map<String, String> xmltoMap(String xmlData) {
        StringReader read = new StringReader(xmlData);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        SAXBuilder sbx = new SAXBuilder();
        Document doc = null;
        try {
            doc = sbx.build(source);
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Element root = doc.getRootElement();
        List es = root.getChildren();
        Map<String, String> retMap = new HashMap<>();
        if (es != null && es.size() != 0) {
            for (Object obj : es) {
                if (obj instanceof Element) {
                    Element element = (Element) obj;
                    retMap.put(element.getName(), element.getText());
                }
            }
        }
        return retMap;
    }
}
