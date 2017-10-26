package com.turingdi.awp.entity.db;

import java.util.Date;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-18 17:30.
 */
public class Order {
    private Integer eid;
    private String orderId;
    private String platOrderId;
    private String callback;
    private Integer type; //0=微信支付 1=支付宝
    private Date createTime;
    private Date payTime;

    public static class JsonKey{
        public static String EID = "eid";
        public static String ORDERID = "orderid";
        public static String PLATORDERID = "platorderid";
        public static String CALLBACK = "callback";
        public static String TYPE = "type";
        public static String CREATETIME = "createtime";
        public static String PAYTIME = "paytime";
    }

    public String getPlatOrderId() {
        return platOrderId;
    }

    public Order setPlatOrderId(String platOrderId) {
        this.platOrderId = platOrderId;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Order setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getPayTime() {
        return payTime;
    }

    public Order setPayTime(Date payTime) {
        this.payTime = payTime;
        return this;
    }

    public Integer getEid() {
        return eid;
    }

    public Order setEid(Integer eid) {
        this.eid = eid;
        return this;
    }

    public String getOrderId() {
        return orderId;
    }

    public Order setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public String getCallback() {
        return callback;
    }

    public Order setCallback(String callback) {
        this.callback = callback;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public Order setType(Integer type) {
        this.type = type;
        return this;
    }
}
