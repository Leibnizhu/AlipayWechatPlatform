package com.turingdi.awp.db;

import com.turingdi.awp.db.pool.ConnectionPoolManager;
import com.turingdi.awp.db.pool.HikariCPManager;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基础DAO类
 * 提供查询、插入/删除/更新的包装方法
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-12 13:07.
 */
class BaseVertXDao {
    private static Logger LOG = LoggerFactory.getLogger(BaseVertXDao.class);
    private static ConnectionPoolManager hikariCPM = HikariCPManager.getInstance();
    protected static String KEYS = "keys";
    protected static String UPDATED = "updated";

    /**
     * 无参数查询
     *
     * @param sql 查询语句
     * @param callback 查询结束后的回调方法
     * @author Leibniz.Hu
     */
    static void query(String sql, Handler<List<JsonObject>> callback) {
        query(sql, null, callback);
    }

    /**
     * 带参数查询
     * 
     * @param sql 查询语句
     * @param params 查询参数
     * @param callback 查询结束后的回调方法
     * @author Leibniz.Hu
     */
    static void query(String sql, JsonArray params, Handler<List<JsonObject>> callback) {
        LOG.debug("准备执行SELECT: \"{}\", 参数:{}", sql, params);
        hikariCPM.getConnection(conn -> conn.queryWithParams(sql, params, ar -> {
            if (ar.succeeded()) {
                List<JsonObject> rows = ar.result().getRows();
                LOG.debug("查询到{}行记录……", rows.size());
                LOG.trace("查询结果:{}。", rows);
                callback.handle(rows);
            } else {
                Throwable cause = ar.cause();
                LOG.error("读取数据库失败:{}!", cause);
                throw new RuntimeException(cause);
            }
            conn.close();
        }));
    }

    /**
     * 插入、更新、删除，带参数
     * 
     * @param sql 插入/删除/更新语句
     * @param params SQL参数
     * @param callback 执行结束的回调方法，内容是影响的数据库行数
     * @author Leibniz.Hu
     */
    static void update(String sql, JsonArray params, Handler<JsonObject> callback) {
        LOG.debug("准备执行INSERT/UPDATE/DELETE语句: \"{}\", 参数:{}", sql, params);
        hikariCPM.getConnection(conn -> conn.updateWithParams(sql, params, ar -> {
            if (ar.succeeded()) {
                JsonObject updated = ar.result().toJson();
                LOG.debug("影响了{}行记录，更新返回数据{}……", ar.result().getUpdated(), updated);
                callback.handle(updated);
            } else {
                Throwable cause = ar.cause();
                LOG.error("读取数据库失败:{}!", ar.cause());
                throw new RuntimeException(cause);
            }
            conn.close();
        }));
    }
}
