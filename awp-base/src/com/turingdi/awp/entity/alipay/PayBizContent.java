package com.turingdi.awp.entity.alipay;

import com.alipay.api.AlipayConstants;
import com.turingdi.awp.util.common.CommonUtils;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 该类是保存订单内容信息的类，包括本地订单号、订单金额和订单标题；
 * Created by quandong on 17-6-21.
 */
public class PayBizContent {
    private String outTradeNo; // 本地订单号
    private String totalAmount; // 支付金额，单位分
    private String subject; // 订单标题
    private String passbackParams; // 需要回调的参数

    // 构造方法
    public PayBizContent(String outTradeNo, String totalAmount, String subject, String passbackParams) {
        this.outTradeNo = outTradeNo;
        this.totalAmount = totalAmount;
        this.subject = subject;
        this.passbackParams = passbackParams;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("out_trade_no", this.outTradeNo);
        json.put("total_amount", CommonUtils.getStringFromIntFen(Integer.parseInt(this.totalAmount)));
        json.put("subject", this.subject);
        json.put("product_code", "QUICK_WAP_PAY");

        if(null != this.passbackParams) {
            try {
                json.put("passback_params", URLEncoder.encode(this.passbackParams, AlipayConstants.CHARSET_UTF8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return json.toString();
    }

    // 下面是属性的setter和getter方法
    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}