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

    AccountDao(Vertx vertx) {
        hikariCPM = HikariCPManager.getInstance(vertx);
    }

    /**
     * 更新账户基本公众号配置
     * @param acc
     * @param callback
     */
    void updateBase(Account acc, Handler<Integer> callback) {
        if(acc.getId() == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        int paramCnt = 0;
        if (acc.getName() != null && !acc.getName().equals("")) {
            sql.append("name=?");
            params.add(acc.getName());
            paramCnt++;
        }
        if (acc.getAppid() != null && !acc.getAppid().equals("")) {
            if (paramCnt > 0) sql.append(",");
            sql.append("appid=?");
            params.add(acc.getAppid());
            paramCnt++;
        }
        if (acc.getAppsecret() != null && !acc.getAppsecret().equals("")) {
            if (paramCnt > 0) sql.append(",");
            sql.append("appsecret=?");
            params.add(acc.getAppsecret());
            paramCnt++;
        }
        if (acc.getVerify() != null && !acc.getVerify().equals("")) {
            if (paramCnt > 0) sql.append(",");
            sql.append("verify=?");
            params.add(acc.getVerify());
            paramCnt++;
        }
        sql.append(" where id=?");
        params.add(acc.getId());
        update(sql.toString(), params, callback);
    }

    /**
     * 该方法根据id从微信公众号表中获取公众号数据，具体实现在TWxcmsAccount.xml中；
     *
     * @param id       企业ID
     * @param callback 获取到数据后回调方法
     */
    void getById(int id, Handler<JsonObject> callback) {
        query("SELECT * FROM awp_account WHERE ID = ?",
                new JsonArray().add(id),
                result -> {
                    callback.handle(result.size() > 0 ? result.get(0) : null);
                });
    }

    void login(Account account, Handler<JsonObject> callback) {
        query("SELECT * FROM awp_account WHERE email = ? and password = ?",
                new JsonArray().add(account.getName()).add(account.getPassword()),
                result -> {
                    callback.handle(result.size() > 0 ? result.get(0) : null);
                });
    }

    void getAccountList(Handler<List<JsonObject>> callback){
        query("SELECT id,name FROM awp_account", callback);
    }

    void updateWxPay(Account acc, Handler<Integer> callback) {
        if(acc.getId() == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        int paramCnt = 0;
        if (acc.getMchId() != null && !acc.getMchId().equals("")) {
            sql.append("mchId=?");
            params.add(acc.getMchId());
            paramCnt++;
        }
        if (acc.getMchKey() != null && !acc.getMchKey().equals("")) {
            if (paramCnt > 0) sql.append(",");
            sql.append("mchKey=?");
            params.add(acc.getMchKey());
            paramCnt++;
        }
        if (acc.getWxPayOn() != null) {
            if (paramCnt > 0) sql.append(",");
            sql.append("wxPayOn=?");
            params.add(acc.getWxPayOn());
            paramCnt++;
        }
        sql.append(" where id=?");
        params.add(acc.getId());
        update(sql.toString(), params, callback);
    }

    void updateZfbPay(Account acc, Handler<Integer> callback) {
        if(acc.getId() == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        int paramCnt = 0;
        if (acc.getZfbAppId() != null && !acc.getZfbAppId().equals("")) {
            sql.append("zfbAppId=?");
            params.add(acc.getZfbAppId());
            paramCnt++;
        }
        if (acc.getZfbPrivKey() != null && !acc.getZfbPrivKey().equals("")) {
            if (paramCnt > 0) sql.append(",");
            sql.append("zfbPrivKey=?");
            params.add(acc.getZfbPrivKey());
            paramCnt++;
        }
        if (acc.getZfbPubKey() != null && !acc.getZfbPubKey().equals("")) {
            if (paramCnt > 0) sql.append(",");
            sql.append("zfbPubKey=?");
            params.add(acc.getZfbPubKey());
            paramCnt++;
        }
        if (acc.getZfbPayOn() != null) {
            if (paramCnt > 0) sql.append(",");
            sql.append("zfbPayOn=?");
            params.add(acc.getZfbPayOn());
            paramCnt++;
        }
        sql.append(" where id=?");
        params.add(acc.getId());
        update(sql.toString(), params, callback);
    }

    int updateZfbPay(Account tWxcmsAccount) {
        return 0;
    }

    List<Account> selectByUserId(int userID) {
        return null;
    }

    int insert(Account tWxcmsAccount) {
        return 0;
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
}