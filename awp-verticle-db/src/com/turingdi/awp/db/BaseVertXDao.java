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
    static ConnectionPoolManager hikariCPM = HikariCPManager.getInstance();

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
        LOG.debug("excuting SELECT SQL: \"{}\", params:{}", sql, params);
        hikariCPM.getConnection(conn -> {
            conn.queryWithParams(sql, params, ar -> {
                        if (ar.succeeded()) {
                            List<JsonObject> rows = ar.result().getRows();
                            LOG.debug("Got {} row(s)", rows.size());
                            LOG.trace("Query results:{}", rows);
                            callback.handle(rows);
                        } else {
                            LOG.error("读取数据库失败:{}", ar.cause());
                        }
                        conn.close();
                    });
        });
    }

    static void update(String sql, JsonArray params, Handler<Integer> callback) {
        LOG.debug("excuting INSERT/UPDATE/DELETE SQL: \"{}\", params:{}", sql, params);
        hikariCPM.getConnection(conn -> {
            conn.updateWithParams(sql, params, ar -> {
                if (ar.succeeded()) {
                    int updated = ar.result().getUpdated();
                    LOG.debug("Affects {} row(s)", updated);
                    callback.handle(updated);
                } else {
                    LOG.error("读取数据库失败:{}", ar.cause());
                }
                conn.close();
            });
        });
    }
}
