package com.turingdi.awp.db.pool;

import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * 数据库连接池管理器的接口
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-12 12:30.
 */
public interface ConnectionPoolManager {
    /**
     * 从数据库连接池获取一个连接
     */
    void getConnection(Handler<SQLConnection> callback);


    /**
     * 获取SQLClient对象本身
     */
    SQLClient getSQLClient();
}
