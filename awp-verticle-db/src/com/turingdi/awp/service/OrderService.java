package com.turingdi.awp.service;

import com.turingdi.awp.db.OrderDao;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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
        try {
            orderDao.getByOrderId(orderId, 1, callback);
        } catch (Exception e) {
            log.error("获取订单信息出错", e);
        }
    }

    public void getByWechatOrderId(String orderId, Handler<JsonObject> callback) {
        orderDao.getByOrderId(orderId, 0, callback);
    }

    public void updateAfterPaid(JsonObject order, Handler<Integer> callback) {
        try {
            orderDao.updateAfterPaid(order, callback);
        } catch (Exception e) {
            log.error("更新订单信息出错", e);
        }
    }
}
