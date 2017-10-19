package com.turingdi.awp.util.common;

/**
 * 修改了规则的URL安全的Base64算法工具
 * 加密和解密都有
 * 分享监测JS在页面商用这个算法加密解密
 * trfr参数想解析的先用这个解码
 *
 * Created by leibniz on 2017-04-14.
 */
public class TuringBase64Util {
    private static String base64Code = "DJOA7qGmIkxoKdPFXiSE8wCeHZ1vcTbrVh3B9LfWNpUlsnu-gjYzMyt0452RQ6,a~";

    /**
     * 加密方法
     *
     * @param srcData 待加密字符串
     * @return 加密后的字符串
     */
    public static String encode(String srcData) {
        StringBuilder output = new StringBuilder();
        int chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        char[] utftext = new char[srcData.length() * 3];
        int j = 0;
        for (int n = 0; n < srcData.length(); n++) {
            char c = srcData.charAt(n);
            if (c < 128) {
                utftext[j++] = (char) c;
            } else if ((c > 127) && (c < 2048)) {
                utftext[j++] = (char) ((c >> 6) | 192);
                utftext[j++] = (char) ((c & 63) | 128);
            } else {
                utftext[j++] = (char) ((c >> 12) | 224);
                utftext[j++] = (char) (((c >> 6) & 63) | 128);
                utftext[j++] = (char) ((c & 63) | 128);
            }
        }
        for (j = 0; j < utftext.length; j++) {
            if (utftext[j] == 0) {
                break;
            }
        }
        int i = 0;
        while (i < j) {
            chr1 = utftext[i++];
            chr2 = utftext[i++];
            chr3 = utftext[i++];
            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;
            if (chr2 == 0) {
                enc3 = enc4 = 64;
            } else if (chr3 == 0) {
                enc4 = 64;
            }
            output.append(base64Code.charAt(enc1)).append(base64Code.charAt(enc2))
                    .append(base64Code.charAt(enc3)).append(base64Code.charAt(enc4));
        }
        return output.toString();
    }

    /**
     * 解密方法
     *
     * @param srcData 待解密字符串
     * @return 解密后的原文
     */
    public static String decode(String srcData) {
        srcData = srcData.replaceAll("[^A-Za-z0-9\\-,~]", "");
        //将密文根据Base64编码表转换成对应Value，再转换成二进位 ，然后将所有二进位补足6位，最后将所有二进位存进一个字串
        int[] tmp = new int[srcData.length() / 4 * 3 + 3];
        int chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        int i = 0;
        int tmpI = 0;
        while (i < srcData.length()) {
            enc1 = base64Code.indexOf(srcData.charAt(i++));
            enc2 = base64Code.indexOf(srcData.charAt(i++));
            enc3 = base64Code.indexOf(srcData.charAt(i++));
            enc4 = base64Code.indexOf(srcData.charAt(i++));
            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;
            tmp[tmpI++] = chr1;
            if (enc3 != 64) {
                tmp[tmpI++] = chr2;
            }
            if (enc4 != 64) {
                tmp[tmpI++] = chr3;
            }
        }
        int j = 0;
        i = 0;
        int c, c2, c3;
        char[] f = new char[tmp.length];
        while (j < tmp.length) {
            c = tmp[j];
            if (c < 128) {
                f[i++] = (char) c;
                j++;
            } else if ((c > 191) && (c < 224)) {
                c2 = tmp[j + 1];
                f[i++] = (char) ((((c & 31) << 6) | (c2 & 63)) & 0xffff);
                j += 2;
            } else {
                c2 = tmp[j + 1];
                c3 = tmp[j + 2];
                f[i++] = (char) ((((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63)) & 0xffff);
                j += 3;
            }
        }

        for (i = 0; i < f.length; i++) {
            if (f[i] == 0) {
                break;
            }
        }
        return new String(f, 0, i);
    }
}