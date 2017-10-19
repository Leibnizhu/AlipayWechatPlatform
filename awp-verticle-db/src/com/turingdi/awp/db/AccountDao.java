package com.turingdi.awp.db;


import com.turingdi.awp.entity.db.Account;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AccountDao extends BaseVertXDao {
    private Logger log = LoggerFactory.getLogger(getClass());

    public AccountDao() {
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
        boolean moreThanOne = false;
        if (acc.getName() != null && !acc.getName().equals("")) {
            sql.append("name=?");
            params.add(acc.getName());
            moreThanOne = true;
        }
        if (acc.getAppid() != null && !acc.getAppid().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("appid=?");
            params.add(acc.getAppid());
            moreThanOne = true;
        }
        if (acc.getAppsecret() != null && !acc.getAppsecret().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("appsecret=?");
            params.add(acc.getAppsecret());
            moreThanOne = true;
        }
        if (acc.getVerify() != null && !acc.getVerify().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("verify=?");
            params.add(acc.getVerify());
            moreThanOne = true;
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
    public void getById(int id, Handler<JsonObject> callback) {
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
        boolean moreThanOne = false;
        if (acc.getMchId() != null && !acc.getMchId().equals("")) {
            sql.append("mchId=?");
            params.add(acc.getMchId());
            moreThanOne = true;
        }
        if (acc.getMchKey() != null && !acc.getMchKey().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("mchKey=?");
            params.add(acc.getMchKey());
            moreThanOne = true;
        }
        if (acc.getWxPayOn() != null) {
            if (moreThanOne) sql.append(",");
            sql.append("wxPayOn=?");
            params.add(acc.getWxPayOn());
            moreThanOne = true;
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
        boolean moreThanOne = false;
        if (acc.getZfbAppId() != null && !acc.getZfbAppId().equals("")) {
            sql.append("zfbAppId=?");
            params.add(acc.getZfbAppId());
            moreThanOne = true;
        }
        if (acc.getZfbPrivKey() != null && !acc.getZfbPrivKey().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPrivKey=?");
            params.add(acc.getZfbPrivKey());
            moreThanOne = true;
        }
        if (acc.getZfbPubKey() != null && !acc.getZfbPubKey().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPubKey=?");
            params.add(acc.getZfbPubKey());
            moreThanOne = true;
        }
        if (acc.getZfbPayOn() != null) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPayOn=?");
            params.add(acc.getZfbPayOn());
            moreThanOne = true;
        }
        sql.append(" where id=?");
        params.add(acc.getId());
        update(sql.toString(), params, callback);
    }

    List<Account> selectByUserId(int userID) {
        return null;
    }

    List<Account> listForPage(Account searchEntity) {
        return null;
    }

}