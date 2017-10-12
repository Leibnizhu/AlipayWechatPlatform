package com.turingdi.awp.util.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改了规则的URL安全的Base64算法工具
 * 加密和解密都有
 * 分享监测JS在页面商用这个算法加密解密
 * trfr参数想解析的先用这个解码
 *
 * Created by leibniz on 2017-04-14.
 */
public class TuringBase64Util {
    private static String base64Code = "DJOA7qGmIkxoKdPFXiSE8wCeHZ1vcTbrVh3B9LfWNpUlsnu-gjYzMyt0452RQ6,a";

    /**
     * 加密方法
     *
     * @param srcData 待加密字符串
     * @return 加密后的字符串
     */
    public static String encode(String srcData) {
        if (srcData == null || srcData.length() == 0) {
            return srcData;
        }
        char[] chArr = srcData.toCharArray();
        StringBuilder asciiBin;
        StringBuilder asciiBin_all = new StringBuilder();
        for (char aChArr : chArr) {
            //将字符转换成ASCII编码再转换成对应二进位
            asciiBin = new StringBuilder(Integer.toBinaryString((int) aChArr));
            //给不足8位的在高位补0直到补足8位
            while (asciiBin.length() < 8) {
                asciiBin.insert(0, "0");
            }
            //最后把所有二进位拼接成一个字串
            asciiBin_all.append(asciiBin);
        }
        //若长度不能被6整除，则在低位补0到能被6整除为止
        while (asciiBin_all.length() % 6 != 0) {
            asciiBin_all.append("0");
        }
        String asciiBinStr = asciiBin_all.toString();
        //按6个一组拆分成字串数组
        List<String> bin6List = new ArrayList<>();
        String temp;
        while (asciiBinStr.length() / 6 > 0) {
            temp = asciiBinStr.substring(0, 6);
            asciiBinStr = asciiBinStr.substring(6);
            bin6List.add(temp);
        }
        String[] bin6Str = bin6List.toArray(new String[bin6List.size()]);
        int[] index = new int[bin6Str.length];
        //确定最终补位长度
        int overLen = 0;
        if (srcData.length() % 3 != 0) {
            overLen = 3 - srcData.length() % 3;
        }
        //设定存放最终编码的容器
        char[] code = new char[index.length + overLen];
        for (int i = 0; i < index.length; i++) {
            //将二进位转换成十进制数字
            index[i] = Integer.parseInt(bin6Str[i], 2);
            //Base64 : Value -> Encoding
            code[i] = base64Code.charAt(index[i]);
        }
        switch (overLen) {
            case 2:
                code[code.length - 2] = '~';//不需要break
            case 1:
                code[code.length - 1] = '~';
            default:
        }
        return String.valueOf(code);
    }

    /**
     * 解密方法
     *
     * @param srcData 待解密字符串
     * @return 解密后的原文
     */
    public static String decode(String srcData) {
        //检测元数据中“=”的个数，并将之去除
        int counter = 0;
        if (srcData.contains("=")) {
            counter = 1;
            if (srcData.substring(srcData.length() - 2, srcData.length() - 1).equals("~")) {
                counter = 2;
            }
        }
        srcData = srcData.replaceAll("~", "");
        //将密文根据Base64编码表转换成对应Value，再转换成二进位 ，然后将所有二进位补足6位，最后将所有二进位存进一个字串
        char[] srcCh = srcData.toCharArray();
        StringBuilder bin6SB = new StringBuilder();
        int index;
        StringBuilder bin6Str;
        for (char aSrcCh : srcCh) {
            //获得Base64编码表的Value
            index = base64Code.indexOf(aSrcCh);
            //将Value转为二进位
            bin6Str = new StringBuilder(Integer.toBinaryString(index));
            //在长度不足6位的二进位的高位上补0直到补足6位，再保存进字串
            while (bin6Str.length() < 6) {
                bin6Str.insert(0, "0");
            }
            bin6SB.append(bin6Str);
        }
        String bin6Str_all = bin6SB.toString();
        //如果二进位字串后有多补的0，将之去除
        if (counter == 1) {
            bin6Str_all = bin6Str_all.substring(0, bin6Str_all.length() - 2);
        } else if (counter == 2) {
            bin6Str_all = bin6Str_all.substring(0, bin6Str_all.length() - 4);
        }
        //按8个一组拆分成字串数组
        List<String> bin8List = new ArrayList<>();
        String temp;
        while (bin6Str_all.length() / 6 > 0) {
            temp = bin6Str_all.substring(0, 8);
            bin6Str_all = bin6Str_all.substring(8);
            bin8List.add(temp);
        }
        String[] bin8Str = bin8List.toArray(new String[bin8List.size()]);
        //将该字串数组的每个元素（即一组二进位）转成十进制数，再强制转换成char类型
        char[] ascii = new char[bin8Str.length];
        for (int i = 0; i < ascii.length; i++) {
            ascii[i] = (char) Integer.parseInt(bin8Str[i], 2);
        }
        return String.valueOf(ascii);
    }
}