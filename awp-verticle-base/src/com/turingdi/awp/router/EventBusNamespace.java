package com.turingdi.awp.router;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Eventbus中涉及到的所有常量定义
 * 同时提供一个makeMessage()方法用于规范EventBud中发送的消息内容（JSON）
 *
 * @author Leibniz.Hu
 * Created on 2017-10-24 22:16.
 */
public enum EventBusNamespace {
    //地址命名
    ADDR_ACCOUNT_DB,
    ADDR_ORDER_DB,
    //Account命令的命名
    COMMAND_GET_ACCOUNT_BY_ID,
    COMMAND_EMAIL_LOGIN,
    COMMAND_ID_LOGIN,
    COMMAND_GET_ALL_ACCOUNT,
    COMMAND_UPDATE_NORMAL,
    COMMAND_UPDATE_WECHATPAY,
    COMMAND_UPDATE_ALIPAY,
    //Order命令的命名
    COMMAND_INSERT_ORDER,
    COMMAND_GET_ORDER_BY_ALIPAY_ORDER_ID,
    COMMAND_GET_ORDER_BY_WECHAT_ORDER_ID,
    COMMAND_UPDATE_PAID_ORDER;

    public String get() {
        return this.toString();
    }

    //EventBus消息的方法名key
    public static final String PARAMS = "awp.params";
    //EventBus消息的参数列表key
    public static final String METHOD = "awp.method";

    /**
     * 规范EventBud中发送的消息内容（JSON）
     *
     * @param method 需要执行的方法
     * @param params 执行方法所需的参数
     * @return 统一规范的在EventBus中发布的消息JSON内容
     *
     * @author Leibniz
     */
    public static JsonObject makeMessage(EventBusNamespace method, Object... params) {
        JsonObject msg = new JsonObject();
        msg.put(METHOD, method.get());
        if (params != null) {
            JsonArray paramArray = new JsonArray();
            for (Object param : params) {
                paramArray.add(param);
            }
            msg.put(PARAMS, paramArray);
        }
        return msg;
    }
}
