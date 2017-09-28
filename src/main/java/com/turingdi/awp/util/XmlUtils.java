package com.turingdi.awp.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:15.
 */
public class XmlUtils {
    public static String simpleMapToXml(Map<String, Object> map, String encode) throws UnsupportedEncodingException {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<xml>");
        for (Map.Entry entry : map.entrySet()) {
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
}
