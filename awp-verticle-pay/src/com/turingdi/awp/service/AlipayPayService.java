package com.turingdi.awp.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.turingdi.awp.dao.AccountDao;
import com.turingdi.awp.entity.*;
import com.turingdi.awp.entity.alipay.*;
import com.turingdi.awp.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 支付服务类接口实现类
 *
 * @author Leibniz.Hu
 * Created on 2017-09-07 11:33.
 */
@Service
public class AlipayPayService {
    private Logger log = LoggerFactory.getLogger(getClass());

    private String projectUrl;
    private String zfbPayNotifyUrl;
    private String zfbPayReturnUrl;

    private final AccountDao wxDao;

    @Autowired
    public AlipayPayService(Constants constants, AccountDao wxDao) {
        this.wxDao = wxDao;
        this.projectUrl = constants.PROJ_URL;
        this.zfbPayNotifyUrl = projectUrl + "mb/pay/zfbNotify";
        this.zfbPayReturnUrl = projectUrl + "mb/pay/success";
    }

    /**
     * 支付宝下单
     *
     * @param product      产品名
     * @param price        价格（单位：分）
     * @param orderId      本地订单ID
     * @param enterpriseId 企业用户ID
     * @param response     HTTP响应
     * @author Leibniz
     */
    public void alipayOrder(String product, int price, String orderId, int enterpriseId, HttpServletResponse response) {
        Account wxAccount = wxDao.getById(enterpriseId);
        try {
            String returnUrl = zfbPayReturnUrl; // 前端回调Url，即支付完成后要跳到的页面链接
            String notifyUrl = zfbPayNotifyUrl; // 服务器后台回调通知的url
            AliAccountInfo aliAccountInfo = new AliAccountInfo(wxAccount.getZfbAppId(), wxAccount.getZfbPrivKey(), wxAccount.getZfbPubKey(), returnUrl, notifyUrl, null); // 该对象保存了支付宝账号的相关信息，以及请求回调地址
            PayBizContent payBizContent = new PayBizContent(orderId, price + "", product, null); // 订单的信息
            AliPayApi.wapPay(aliAccountInfo, payBizContent, response); // 调用支付宝API的方法请求支付宝支付接口
        } catch (IOException e) {
            e.printStackTrace();
            log.error(orderId + "下单失败，请查看微信支持开发配置是否正确");
        }
    }

    /**
     * 支付宝退款接口；
     *
     * @param bizContent   支付宝退款接口的业务内容请求JSON实例
     * @param enterpriseId 企业用户ID
     * @return 是否调用成功
     *
     * @author Leibniz
     */
    public boolean alipayRefund(RefundBizContent bizContent, int enterpriseId) {
        String bizContentStr = bizContent.toString(); // 将参数bizContent转成字符串

        // 检查bizContent是否合法
        if (!bizContent.checkParameters()) {
            throw new IllegalArgumentException("错误的支付宝退款接口请求业务JSON：" + bizContentStr); // 抛出异常
        }

        Account wxAccount = wxDao.getById(enterpriseId);
        String returnUrl = zfbPayReturnUrl; // 前端回调Url，即支付完成后要跳到的页面链接
        String notifyUrl = zfbPayNotifyUrl; // 服务器后台回调通知的url
        AliAccountInfo aliAccountInfo = new AliAccountInfo(wxAccount.getZfbAppId(), wxAccount.getZfbPrivKey(), wxAccount.getZfbPubKey(), returnUrl, notifyUrl, null); // 该对象保存了支付宝账号的相关信息，以及请求回调地址
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
                return true; // 退款成功
            } else { // 退款失败
                log.error("调用支付宝退款接口错误，code={}，msg={}，sub_code={}，sub_msg={}", new Object[]{response.getCode(), response.getMsg(), response.getSubCode(), response.getSubMsg()}); // 打日志
            }
        } else { // 响应失败
            log.error("调用支付宝退款接口时发生异常，返回响应对象为null！{}", bizContentStr); // 打日志
        }

        return false; // 退款失败
    }
}
