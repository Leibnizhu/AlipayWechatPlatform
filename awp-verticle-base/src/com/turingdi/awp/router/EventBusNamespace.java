package com.turingdi.awp.router;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
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
    COMMAND_UPDATE_PAID_ORDER
    ;

    public String get(){
        return this.toString();
    }

    public static final String METHOD = "awp.method";
    public static final String PARAMS = "awp.params";

    public static JsonObject makeMessage(EventBusNamespace method, Object... params){
        JsonObject msg = new JsonObject();
        msg.put(METHOD, method.get());
        if(params != null){
            JsonArray paramArray = new JsonArray();
            for(Object param : params){
                paramArray.add(param);
            }
            msg.put(PARAMS, paramArray);
        }
        return msg;
    }
}
