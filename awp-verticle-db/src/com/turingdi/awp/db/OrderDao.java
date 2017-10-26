package com.turingdi.awp.db;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static com.turingdi.awp.entity.db.Order.JsonKey.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-18 17:29.
 */
public class OrderDao extends BaseVertXDao {

    public void insert(JsonObject order, Handler<Integer> callback) {
        Integer eid = order.getInteger(EID);
        if (eid == null)
            throw new IllegalArgumentException("Eid in Order object cannot be null!!!");
        String orderId = order.getString(ORDERID);
        if (orderId == null || orderId.length() == 0)
            throw new IllegalArgumentException("OrderId in Order object cannot be null!!!");
        String orderCallback = order.getString(CALLBACK);
        if (orderCallback == null || orderCallback.length() == 0)
            throw new IllegalArgumentException("Callback in Order object cannot be null!!!");
        Integer type = order.getInteger(TYPE);
        if (type == null)
            throw new IllegalArgumentException("Type in Order object cannot be null!!!");
        String sql = "INSERT INTO awp_order (eid,orderId,callback,type,createTime) VALUES (?,?,?,?,NOW())";
        JsonArray params = new JsonArray().add(eid).add(orderId).add(orderCallback).add(type);
        update(sql, params, callback);
    }

    public void getByOrderId(String orderId, Integer type, Handler<JsonObject> callback) {
        if (orderId == null || orderId.length() == 0)
            throw new IllegalArgumentException("OrderId in Order object cannot be null!!!");
        if (type == null)
            throw new IllegalArgumentException("Type in Order object cannot be null!!!");
        String sql = " select o.*,a.zfbPubKey from awp_order o LEFT JOIN awp_account a ON o.eid = a.id where o.orderId=? and o.type=?";
        JsonArray params = new JsonArray().add(orderId).add(type);
        query(sql, params, result -> callback.handle(result.size() > 0 ? result.get(0) : null));
    }

    public void updateAfterPaid(JsonObject order, Handler<Integer> callback) {
        String platOrderId = order.getString(PLATORDERID);
        if (platOrderId == null || platOrderId.length() == 0) {
            throw new IllegalArgumentException("OrderId of pay platform cannot be null!!!");
        }
        String orderId = order.getString(ORDERID);
        if (orderId == null || orderId.length() == 0)
            throw new IllegalArgumentException("OrderId in Order object cannot be null!!!");
        Integer type = order.getInteger(TYPE);
        if (type == null)
            throw new IllegalArgumentException("Type in Order object cannot be null!!!");
        String sql = "UPDATE awp_order SET platOrderId = ?, payTime = NOW() where orderId=? and type=?";
        JsonArray params = new JsonArray().add(platOrderId).add(orderId).add(type);
        update(sql, params, callback);
    }
}
