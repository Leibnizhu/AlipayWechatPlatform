package com.turingdi.awp.db;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.util.common.Constants.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-12 12:28.
 */
public class HikariCPManager implements ConnectionPoolManager{
    private Logger log = LoggerFactory.getLogger(getClass());
    private volatile static HikariCPManager INSTANCE = null;
    private JDBCClient client;

    private HikariCPManager(Vertx vertx){
        JsonObject vertxConfig = vertx.getOrCreateContext().config();
        JsonObject config = new JsonObject()
                .put("provider_class", vertxConfig.getString("provider_class", "io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider"))
                .put("jdbcUrl", JDBC_URL)
                .put("driverClassName", JDBC_DRIVER)
                .put("username", JDBC_USER)
                .put("password", JDBC_PSWD)
                .put("minimumIdle", vertxConfig.getInteger("minimumIdle", 2))
                .put("maximumPoolSize", vertxConfig.getInteger("maximumPoolSize", 10));
        this.client = JDBCClient.createShared(vertx, config, "HikariCP");
    }

    static HikariCPManager getInstance(Vertx vertx){
        if (INSTANCE == null) {
            //锁定代码块
            synchronized (HikariCPManager.class) {
                //第二重判断
                if (INSTANCE == null) {
                    INSTANCE = new HikariCPManager(vertx); //创建单例实例
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getConnection(Handler<SQLConnection> callback) {
        client.getConnection(ar -> {
            if (ar.succeeded()) {
                callback.handle(ar.result());
            } else {
                // 获取连接失败 - 处理异常
                log.error("获取数据库链接失败");
            }
        });
    }

    @Override
    public SQLClient getSQLClient() {
        return this.client;
    }
}
