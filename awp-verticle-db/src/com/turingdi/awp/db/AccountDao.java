package com.turingdi.awp.db;


import com.turingdi.awp.entity.db.Account;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AccountDao extends BaseVertXDao {
    private Logger log = LoggerFactory.getLogger(getClass());

    public AccountDao(Vertx vertx) {
        hikariCPM = HikariCPManager.getInstance(vertx);
    }

    List<Account> selectByUserId(int userID) {
        return null;
    }

    int insert(Account tWxcmsAccount) {
        return 0;
    }

    int update(Account tWxcmsAccount) {
        return 0;
    }

    /**
     * 该方法根据id从微信公众号表中获取公众号数据，具体实现在TWxcmsAccount.xml中；
     *
     * @param id       企业ID
     * @param callback 获取到数据后回调方法
     */
    void getById(int id, Handler<JsonObject> callback) {
        query("SELECT * FROM t_wxcms_account WHERE ID = ?",
                new JsonArray().add(id),
                result -> {
                    callback.handle(result.size() > 0 ? result.get(0) : null);
                });
    }

    Account getByAccount(String account) {
        return null;
    }

    Account getSingleAccount() {
        return null;
    }

    List<Account> getAllAccount() {
        return null;
    }

    List<Account> listForPage(Account searchEntity) {
        return null;
    }

    void add(Account entity) {
    }

    void delete(Account entity) {
    }

    Account getByAppId(String appId) {
        return null;
    }

    int updateWxPay(Account tWxcmsAccount) {
        return 0;
    }

    int updateZfbPay(Account tWxcmsAccount) {
        return 0;
    }

    public void login(Account account, Handler<JsonObject> callback) {
        query("SELECT * FROM t_wxcms_account WHERE email = ? and password = ?",
                new JsonArray().add(account.getName()).add(account.getPassword()),
                result -> {
                    callback.handle(result.size() > 0 ? result.get(0) : null);
                });
    }
}