package com.turingdi.awp.service;

import com.turingdi.awp.db.OrderDao;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 订单相关的服务类，基本与DAO类一致，不再做注释
 *
 * @author Leibniz.Hu
 * Created on 2017-10-18 17:36.
 */
public class OrderService {
    private Logger log = LoggerFactory.getLogger(getClass());
    private OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public void insert(JsonObject order, Handler<Integer> callback) {
        orderDao.insert(order, callback);
    }

    public void getByAlipayOrderId(String orderId, Handler<JsonObject> callback) {
        orderDao.getByOrderId(orderId, 1, callback);
    }

    public void getByWechatOrderId(String orderId, Handler<JsonObject> callback) {
        orderDao.getByOrderId(orderId, 0, callback);
    }

    public void updateAfterPaid(JsonObject order, Handler<Integer> callback) {
        orderDao.updateAfterPaid(order, callback);
    }
}
