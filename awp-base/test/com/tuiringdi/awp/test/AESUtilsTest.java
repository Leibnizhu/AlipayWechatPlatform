package com.tuiringdi.awp.test;

import com.turingdi.awp.util.common.AESUtils;
import org.junit.Test;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-16 11:29.
 */
public class AESUtilsTest {
    private static final String PASSWORD = "password";
    private String encrypted;

    @Test
    public void testAll(){
        testEncrypt("韩寒会画画后悔画韩红");
        testDecrypt();
    }

    private void testEncrypt(String content){
        System.out.println("原文:\t"+content);
        encrypted = AESUtils.encryptString(content, PASSWORD);
        System.out.println("密文:\t"+encrypted);
    }

    private void testDecrypt(){
        String decrypted = AESUtils.decryptString(encrypted, PASSWORD);
        System.out.println("解密后:\t"+decrypted);
    }
}
