package com.turingdi.awp.util.common;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:13.
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");
    private static Logger LOG = LoggerFactory.getLogger(CommonUtils.class);


    /**
     * 关闭资源用
     * @param resources 待关闭的资源
     */
    public static void closeResources(Closeable... resources) {
        try {
            for(Closeable resource : resources)
                resource.close();
        } catch (IOException e) {
            LOG.error("关闭资源时抛出异常：" + e.getMessage());
        }
    }

    /**
     * 将字节数组转换为16进制的字符串
     *
     * @param bytes 字节数组
     * @return 16进制的字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (byte aByte : bytes) {
            digital = aByte;
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                result.append("0");
            }
            result.append(Integer.toHexString(digital));
        }
        return result.toString().toUpperCase();
    }

    /**
     * Map按字典排序（中文）
     *
     * @param map 待排序的Map
     * @return 返回按字典排序的map
     * @author lws, Leibniz 2016-12-17
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> map) {
        //进行map检查
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Object> sortMap = new TreeMap<>(String::compareTo);
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 基于StringBuffer的字符串替代方法，如果StringBuffer中没有目标关键词，则不替换，跳过
     *
     * @param sb          需要被替换的StringBuffer
     * @param word        需要被替代的目标关键词
     * @param replacement 关键词需要替代成的字符串
     */
    public static void replaceStringBuffer(StringBuffer sb, String word, String replacement) {
        int startIndex = sb.indexOf(word);
        if (-1 != startIndex) {
            sb.replace(startIndex, startIndex + word.length(), replacement == null ? "" : replacement);
        }
    }

    /**
     * 判断所有参数都非空
     *
     * @param strs 待检查的字符串，可以多个
     * @return true：所有参数非空，false：至少有一个参数为空字符串或null
     */
    public static boolean notEmptyString(String... strs) {
        boolean notEmpty;
        for (String str : strs) {
            notEmpty = str != null && str.trim().length() > 0;
            if (!notEmpty) return false;
        }
        return true;
    }

    /**
     * 随机打乱一个数组
     *
     * @param source 需要打乱的数组
     * @param <T> 数组的类型
     * @return 打乱后的数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] shuffleArray(T[] source) {
        List<T> list = Arrays.asList(source);
        Collections.shuffle(list);
        return (T[]) list.toArray();
    }

    /**
     * 获取一个随机的UUID，长度为32位，每一位都是16进制的字符(0-9a-f)
     *
     * @return 32位的随机UUID
     */
    public static String getRandomID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Unicode编码解码
     *
     * @param s 已进行Unicode的字符串
     * @return Unicode解码之后的字符串
     */
    public static String unicodeDecode(String s) {
        Matcher m = UNICODE_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer(s.length());
        while (m.find()) {
            m.appendReplacement(sb, Character.toString((char) Integer.parseInt(m.group(1), 16)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {

        if (!containsEmoji(source)) {
            return source;//如果不包含，直接返回
        }
        //到这里铁定包含
        StringBuilder buf = null;

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);

            if (isEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }

                buf.append(codePoint);
            } else {
            }
        }

        if (buf == null) {
            return source;//如果没有找到 emoji表情，则返回源字符串
        } else {
            if (buf.length() == len) {//这里的意义在于尽可能少的toString，因为会重新生成字符串
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }

    }

    private static boolean containsEmoji(String source) {
        if (!notEmptyString(source)) {
            return false;
        }

        int len = source.length();

        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    public static String getStringFromIntFen(int money) {
        String moneyStr = String.valueOf(money);
        int length = moneyStr.length();
        if (length == 1) {
            return "0.0" + moneyStr;
        } else if (length == 2) {
            return "0." + moneyStr;
        } else if (length > 2) {
            return moneyStr.substring(0, length - 2) + "." + moneyStr.substring(length - 2);
        } else {
            return "";
        }
    }
}
