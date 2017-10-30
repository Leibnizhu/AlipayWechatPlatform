package com.turingdi.awp.verticle;

import com.turingdi.awp.db.OrderDao;
import com.turingdi.awp.router.EventBusNamespace;
import com.turingdi.awp.service.OrderService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.router.EventBusNamespace.ADDR_ORDER_DB;

/**
 * AWP_ORDER表相关的数据库操作Verticle服务
 *
 * @author Leibniz.Hu
 * Created on 2017-10-25 14:44.
 */
@SuppressWarnings("unchecked")
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

    /**
     * 分发请求并处理
     * 下面具体的方法如方法名所示，与DAO层方法命名一致，在此不表
     *
     * @author Leibniz.Hu
     */
    @Override
    protected <T> void processMethods(Handler<T> replyMsg, JsonArray params, EventBusNamespace method) {
        switch (method) {
            case COMMAND_INSERT_ORDER:
                insertOrder(replyMsg, params);
                break;
            case COMMAND_GET_ORDER_BY_ALIPAY_ORDER_ID:
                getOrderByAlipayOrderId(replyMsg, params);
                break;
            case COMMAND_GET_ORDER_BY_WECHAT_ORDER_ID:
                getOrderByWechatOrderId(replyMsg, params);
                break;
            case COMMAND_UPDATE_PAID_ORDER:
                updateAfterPaid(replyMsg, params);
                break;
            default:
                log.error("未能处理的请求方法：{}", method);
        }
    }

    private void insertOrder(Handler replyMsg, JsonArray params) {
        JsonObject orderInsert = params.getJsonObject(0);
        orderSrv.insert(orderInsert, replyMsg);
    }

    private void getOrderByAlipayOrderId(Handler replyMsg, JsonArray params) {
        String orderId = params.getString(0);
        orderSrv.getByAlipayOrderId(orderId, replyMsg);
    }

    private void getOrderByWechatOrderId(Handler replyMsg, JsonArray params) {
        String orderId = params.getString(0);
        orderSrv.getByWechatOrderId(orderId, replyMsg);
    }

    private void updateAfterPaid(Handler replyMsg, JsonArray params) {
        JsonObject orderUpdate = params.getJsonObject(0);
        orderSrv.updateAfterPaid(orderUpdate, replyMsg);
    }
}
