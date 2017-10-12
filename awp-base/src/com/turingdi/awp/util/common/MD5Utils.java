package com.turingdi.awp.util.common;

import java.security.MessageDigest;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:14.
 */
public class MD5Utils {

    public static String getMD5(String message) {
        String md5str = null;
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //将消息变成byte数组
            byte[] input = message.getBytes();
            //计算后获得字节数组,这就是那128位了
            byte[] buff = md.digest(input);
            //把数组每一字节（一个字节占八位）换成16进制连成md5字符串
            md5str = CommonUtils.bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }
}