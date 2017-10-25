package com.turingdi.awp.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.turingdi.awp.entity.alipay.AliAccountInfo;
import com.turingdi.awp.entity.alipay.PayBizContent;
import com.turingdi.awp.entity.alipay.RefundBizContent;
import com.turingdi.awp.util.alipay.AliPayApi;
import com.turingdi.awp.util.alipay.AliPayCliFactory;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 支付服务类接口实现类
 *
 * @author Leibniz.Hu
 * Created on 2017-09-07 11:33.
 */
public class AlipayPayService {
    private Logger log = LoggerFactory.getLogger(getClass());

    private String zfbPayNotifyUrl;

    public AlipayPayService() {
        String projectUrl = Constants.PROJ_URL;
        this.zfbPayNotifyUrl = projectUrl + "pay/zfb/noti";
    }

    /**
     * 支付宝下单
     *
     * @param product  产品名
     * @param price    价格（单位：分）
     * @param orderId  本地订单ID
     * @param acc      企业用户账户对象
     * @param response HTTP响应
     * @author Leibniz
     */
    public void alipayOrder(String product, int price, String orderId, JsonObject acc, String successUrl, HttpServerResponse response) {
        try {
            String notifyUrl = zfbPayNotifyUrl; // 服务器后台回调通知的url
            AliAccountInfo aliAccountInfo = new AliAccountInfo(acc.getString("zfbappid"), acc.getString("zfbprivkey"), acc.getString("zfbpubkuey"), successUrl, notifyUrl, null); // 该对象保存了支付宝账号的相关信息，以及请求回调地址
            PayBizContent payBizContent = new PayBizContent(orderId, price + "", product, null); // 订单的信息
            AliPayApi.wapPay(aliAccountInfo, payBizContent, response); // 调用支付宝API的方法请求支付宝支付接口
        } catch (IOException e) {
            log.error(orderId + "下单失败，请查看微信支持开发配置是否正确", e);
        }
    }

    /**
     * 支付宝退款接口；
     *
     * @param bizContent   支付宝退款接口的业务内容请求JSON实例
     * @param acc      企业用户账户对象
     * 异步返回 是否调用成功
     *
     * @author Leibniz
     */
    public void alipayRefund(RefundBizContent bizContent, JsonObject acc, String successUrl, Handler<Boolean> callback) {
        String bizContentStr = bizContent.toString(); // 将参数bizContent转成字符串

        // 检查bizContent是否合法
        if (!bizContent.checkParameters()) {
            throw new IllegalArgumentException("错误的支付宝退款接口请求业务JSON：" + bizContentStr); // 抛出异常
        }

        String notifyUrl = zfbPayNotifyUrl; // 服务器后台回调通知的url
        AliAccountInfo aliAccountInfo = new AliAccountInfo(acc.getString("zfbappid"), acc.getString("zfbprivkey"), acc.getString("zfbpubkey"), successUrl, notifyUrl, null); // 该对象保存了支付宝账号的相关信息，以及请求回调地址
        AlipayClient alipayClient = AliPayCliFactory.getAlipayClient(aliAccountInfo); // 获取支付宝连接
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest(); // 创建退款请求
        request.setBizContent(bizContentStr); // 设置请求的bizContent
        AlipayTradeRefundResponse response = null;

        try {
            response = alipayClient.execute(request); // 发送退款请求并获得响应
        } catch (AlipayApiException e) { // 捕捉异常
            log.error("调用支付宝退款接口时抛出异常，请求的JSON：" + bizContentStr, e); // 打日志
        }

        // 判断是否有响应
        if (response != null) { // 响应成功
            // 判断退款是否成功
            if (response.isSuccess()) { // 退款成功
                log.info("调用成功"); // 打日志
                callback.handle(true);
                // 退款成功,其他都是错
            } else { // 退款失败
                log.error("调用支付宝退款接口错误，code={}，msg={}，sub_code={}，sub_msg={}", response.getCode(), response.getMsg(), response.getSubCode(), response.getSubMsg()); // 打日志
                callback.handle(false);
            }
        } else { // 响应失败
            log.error("调用支付宝退款接口时发生异常，返回响应对象为null！{}", bizContentStr); // 打日志
            callback.handle(false);
        }
    }
}
