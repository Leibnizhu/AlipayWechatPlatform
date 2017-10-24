package com.turingdi.awp.db;

import com.turingdi.awp.entity.db.Order;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-18 17:29.
 */
public class OrderDao extends BaseVertXDao {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void insert(Order order, Handler<Integer> callback) {
        if (order.getEid() == null)
            throw new IllegalArgumentException("Eid in Order object cannot be null!!!");
        if (order.getOrderId() == null || order.getOrderId().length() == 0)
            throw new IllegalArgumentException("OrderId in Order object cannot be null!!!");
        if (order.getCallback() == null || order.getCallback().length() == 0)
            throw new IllegalArgumentException("Callback in Order object cannot be null!!!");
        if (order.getType() == null)
            throw new IllegalArgumentException("Type in Order object cannot be null!!!");
        String sql = "INSERT INTO awp_order (eid,orderId,callback,type,createTime) VALUES (?,?,?,?,NOW())";
        JsonArray params = new JsonArray().add(order.getEid()).add(order.getOrderId()).add(order.getCallback()).add(order.getType());
        update(sql, params, callback);
    }

    public void getByOrderId(String orderId, Integer type, Handler<JsonObject> callback) {
        if (orderId == null || orderId.length() == 0)
            throw new IllegalArgumentException("OrderId in Order object cannot be null!!!");
        if (type == null)
            throw new IllegalArgumentException("Type in Order object cannot be null!!!");
        String sql = " select o.*,a.zfbPubKey from awp_order o LEFT JOIN awp_account a ON o.eid = a.id where o.orderId=? and o.type=?";
        JsonArray params = new JsonArray().add(orderId).add(type);
        query(sql, params, result -> {
            callback.handle(result.size() > 0 ? result.get(0) : null);
        });
    }

    public void updateAfterPaid(Order order, Handler<Integer> callback) {
        if (order.getPlatOrderId() == null || order.getPlatOrderId().length() == 0)
            throw new IllegalArgumentException("OrderId of pay platform cannot be null!!!");
        if (order.getOrderId() == null || order.getOrderId().length() == 0)
            throw new IllegalArgumentException("OrderId in Order object cannot be null!!!");
        if (order.getType() == null)
            throw new IllegalArgumentException("Type in Order object cannot be null!!!");
        String sql = "UPDATE awp_order SET platOrderId = ?, payTime = NOW() where orderId=? and type=?";
        JsonArray params = new JsonArray().add(order.getPlatOrderId()).add(order.getOrderId()).add(order.getType());
        update(sql, params, callback);
    }
}
