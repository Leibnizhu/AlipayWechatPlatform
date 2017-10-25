package com.turingdi.awp.verticle;

import com.turingdi.awp.db.OrderDao;
import com.turingdi.awp.router.EventBusNamespace;
import com.turingdi.awp.service.OrderService;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.router.EventBusNamespace.ADDR_ORDER_DB;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-25 14:44.
 */
public class OrderDBVerticle extends AbstractDatabaseAccessVerticle {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private OrderService orderSrv;

    public OrderDBVerticle() {
        super(ADDR_ORDER_DB);
        OrderDao orderDao = new OrderDao();
        orderSrv = new OrderService(orderDao);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void processMethods(Message<JsonObject> msg, JsonArray params, EventBusNamespace method) {
        switch (method) {
            case COMMAND_INSERT_ORDER:
                insertOrder(msg, params);
                break;
            case COMMAND_GET_ORDER_BY_ALIPAY_ORDER_ID:
                getOrderByAlipayOrderId(msg, params);
                break;
            case COMMAND_GET_ORDER_BY_WECHAT_ORDER_ID:
                getOrderByWechatOrderId(msg, params);
                break;
            case COMMAND_UPDATE_PAID_ORDER:
                updateAfterPaid(msg, params);
                break;
            default:
                log.error("未能处理的请求方法：{}", method);
        }
    }

    private void insertOrder(Message<JsonObject> msg, JsonArray params) {
        JsonObject orderInsert = params.getJsonObject(0);
        orderSrv.insert(orderInsert, msg::reply);
    }

    private void getOrderByAlipayOrderId(Message<JsonObject> msg, JsonArray params) {
        String orderId = params.getString(0);
        orderSrv.getByAlipayOrderId(orderId, msg::reply);
    }

    private void getOrderByWechatOrderId(Message<JsonObject> msg, JsonArray params) {
        String orderId = params.getString(0);
        orderSrv.getByWechatOrderId(orderId, msg::reply);
    }

    private void updateAfterPaid(Message<JsonObject> msg, JsonArray params) {
        JsonObject orderUpdate = params.getJsonObject(0);
        orderSrv.updateAfterPaid(orderUpdate, msg::reply);
    }
}
