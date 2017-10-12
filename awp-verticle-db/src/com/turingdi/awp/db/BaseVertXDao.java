package com.turingdi.awp.db;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-12 13:07.
 */
class BaseVertXDao {
    private static Logger LOG = LoggerFactory.getLogger(BaseVertXDao.class);
    static ConnectionPoolManager hikariCPM;

    /**
     * 无参数查询
     *
     * @param sql
     * @param callback
     */
    static void query(String sql, Handler<List<JsonObject>> callback) {
        query(sql, null, callback);
    }

    /**
     * 带参数查询
     *
     * @param sql
     * @param params
     * @param callback
     */
    static void query(String sql, JsonArray params, Handler<List<JsonObject>> callback) {
        hikariCPM.getConnection(conn -> {
            conn.queryWithParams(sql, params, ar -> {
                        if (ar.succeeded()) {
                            callback.handle(ar.result().getRows());
                        } else {
                            LOG.error("读取数据库失败");
                        }
                    });
        });
    }
}
