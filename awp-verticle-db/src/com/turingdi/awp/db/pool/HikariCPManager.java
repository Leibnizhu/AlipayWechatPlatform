package com.turingdi.awp.db.pool;

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
 * ConnectionPoolManager接口的HikariCPi连接池实现类
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-12 12:28.
 */
public class HikariCPManager implements ConnectionPoolManager {
    private Logger log = LoggerFactory.getLogger(getClass());
    private volatile static HikariCPManager INSTANCE = null;
    private JDBCClient client;

    /**
     * 因为是单例，这个唯一的构造器是私有的
     * 主要任务是初始化连接池
     * 从vertx配置中读取u数据库相关配置，然后创建JDBCClient，保存到私有变量
     * @author Leibniz.Hu
     */
    private HikariCPManager(Vertx vertx) {
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

    /**
     * 初始化的方法
     * 传入Vertx对象，用于调用私有构造器，产生单例对象
     */
    public static HikariCPManager init(Vertx vertx) {
        if (INSTANCE != null) {
            throw new RuntimeException("HikariCPManager is already initialized, please do not call init() any more!!!");
        }
        INSTANCE = new HikariCPManager(vertx); //创建单例实例
        return INSTANCE;
    }

    /**
     * 获取单例对象的方法
     */
    public static HikariCPManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("HikariCPManager is still not initialized!!!");
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
                log.error("获取数据库链接失败", ar.cause());
            }
        });
    }

    @Override
    public SQLClient getSQLClient() {
        return this.client;
    }
}
