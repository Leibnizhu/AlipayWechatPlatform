package com.turingdi.awp.entity.alipay;

import com.turingdi.awp.util.common.CommonUtils;
import net.sf.json.JSONObject;

/**
 * 支付宝退款接口的业务内容请求实体类
 *
 * Created by leibniz on 17-7-20.
 */
public class RefundBizContent {
    private String outTradeNo;//订单支付时传入的商户订单号,不能和 trade_no同时为空。
    private String tradeNo;//支付宝交易号，和商户订单号不能同时为空
    private int refundAmount;//需要退款的金额，该金额不能大于订单金额,单位为分；发送请求时单位为元，支持两位小数
    private String refundReason;//退款的原因说明,可选
    private String storeId;//商户的门店编号，对应到倍轻松的柜台号（BoxId）

    /**
     * 检查字段是否正确
     *
     * @return true=字段正常，false=字段有错误，不能请求退款接口
     * @author Leibniz
     */
    public boolean checkParameters(){
        // outTradeNo不能和 trade_no同时为空。
        if(!CommonUtils.notEmptyString(this.outTradeNo) && !CommonUtils.notEmptyString(this.tradeNo)){
            return false;
        }
        //退款金额必须大于0
        if(this.refundAmount <= 0){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        JSONObject bizContentJson = new JSONObject();
        if(CommonUtils.notEmptyString(this.outTradeNo)){
            bizContentJson.put("out_trade_no", this.outTradeNo);
        }
        if(CommonUtils.notEmptyString(this.tradeNo)){
            bizContentJson.put("trade_no", this.tradeNo);
        }
        bizContentJson.put("refund_amount", CommonUtils.getStringFromIntFen(this.refundAmount));
        if(CommonUtils.notEmptyString(this.refundReason)){
            bizContentJson.put("refund_reason", this.refundReason);
        }
        if(CommonUtils.notEmptyString(this.storeId)){
            bizContentJson.put("store_id", this.storeId);
        }
        return bizContentJson.toString();
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public RefundBizContent setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
        return this;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public RefundBizContent setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
        return this;
    }

    public int getRefundAmount() {
        return refundAmount;
    }

    public RefundBizContent setRefundAmount(int refundAmount) {
        this.refundAmount = refundAmount;
        return this;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public RefundBizContent setRefundReason(String refundReason) {
        this.refundReason = refundReason;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }

    public RefundBizContent setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }
}
