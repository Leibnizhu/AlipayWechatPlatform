package com.turingdi.awp.db;

import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-12 12:30.
 */
public interface ConnectionPoolManager {
    void getConnection(Handler<SQLConnection> callback);

    SQLClient getSQLClient();
}
