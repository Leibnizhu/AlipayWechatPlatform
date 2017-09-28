package com.turingdi.awp.entity.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;

/**
 * Created by quandong on 17-6-27.
 */
public class AliPayCliFactory {
    private static final String ALIPAY_GATEWAY ="https://openapi.alipay.com/gateway.do"; // 支付宝网关，固定
    private static final String ZHIMA_GATEWAY ="https://zmopenapi.zmxy.com.cn/openapi.do"; // 芝麻信用网关，固定
    //    private static final String ALIPAY_GATEWAY = "https://openapi.alipaydev.com/gateway.do"; // 沙箱网关
    private static DefaultZhimaClient zhimaClient;

    /**
     * 获取AlipayClient实例
     *
     * @param aliAccountInfo 保存了支付宝账户的信息，包括appId、用户私钥、支付宝公钥和回调地址等，具体参考AliAccountInfo类的属性说明
     * @return AlipayClient实例
     * @author quandong
     * @comment Leibniz
     */
    public static AlipayClient getAlipayClient(AliAccountInfo aliAccountInfo) {
        return new DefaultAlipayClient(ALIPAY_GATEWAY, aliAccountInfo.getAppId(), aliAccountInfo.getAppPrivateKey(), AlipayConstants.FORMAT_JSON, AlipayConstants.CHARSET_UTF8, aliAccountInfo.getAlipayPublicKey(), AlipayConstants.SIGN_TYPE_RSA2);
    }

    /**
     * 获取ZhimaClient实例
     *
     * @param aliAccountInfo 保存了支付宝账户的信息，包括appId、用户私钥、支付宝公钥和回调地址等，具体参考AliAccountInfo类的属性说明
     * @return DefaultZhimaClient实例
     * @author Leibniz
     */
    public static DefaultZhimaClient getZhimaClient(AliAccountInfo aliAccountInfo){
        if(null == zhimaClient){
            zhimaClient = new DefaultZhimaClient(ZHIMA_GATEWAY, aliAccountInfo.getAppId(), aliAccountInfo.getAppPrivateKey(), aliAccountInfo.getAlipayPublicKey());
        }
        return zhimaClient;
    }
}