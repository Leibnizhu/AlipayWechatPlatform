package com.turingdi.awp.util.common;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 16:50.
 */
public class SHA1Utils {
    public static String SHA1(String decrypt) throws DigestException {
        //获取信息摘要 - 参数字典排序后字符串
        try {
            //指定sha1算法
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-1");
            digest.update(decrypt.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            // 字节数组转换为 十六进制 数
            for (byte aMessageDigest : messageDigest) {
                String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new DigestException("签名错误！");
        }
    }

    /**
     * 获取参数的字典排序
     *
     * @param maps 参数key-value map集合
     * @return 排序后的字符串
     */
    public static String getOrderByLexicographic(Map<String, Object> maps) {
        return splitParams(lexicographicOrder(getParamsName(maps)), maps);
    }

    /**
     * 获取参数名称 key
     *
     * @param maps 参数key-value map集合
     * @return 参数名字List
     */
    private static List<String> getParamsName(Map<String, Object> maps) {
        return new ArrayList<>(maps.keySet());
    }

    /**
     * 参数名称按字典排序
     *
     * @param paramNames 参数名称List集合
     * @return 排序后的参数名称List集合
     */
    private static List<String> lexicographicOrder(List<String> paramNames) {
        Collections.sort(paramNames);
        return paramNames;
    }

    /**
     * 拼接排序好的参数名称和参数值
     *
     * @param paramNames 排序后的参数名称集合
     * @param maps       参数key-value map集合
     * @return String 拼接后的字符串
     */
    private static String splitParams(List<String> paramNames, Map<String, Object> maps) {
        StringBuilder paramStr = new StringBuilder();
        for (String paramName : paramNames) {
            paramStr.append(paramName).append(maps.get(paramName));
        }
        return paramStr.toString();
    }
}
