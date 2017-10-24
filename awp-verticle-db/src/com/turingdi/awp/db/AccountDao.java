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
    public void updateBase(Account acc, Handler<Integer> callback) {
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
        if (acc.getEmail() != null && !acc.getEmail().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("email=?");
            params.add(acc.getEmail());
            moreThanOne = true;
        }
        if (acc.getPassword() != null && !acc.getPassword().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("password=?");
            params.add(acc.getPassword());
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

    public void login(Account account, Handler<JsonObject> callback) {
        query("SELECT * FROM awp_account WHERE email = ? and password = ?",
                new JsonArray().add(account.getName()).add(account.getPassword()),
                result -> {
                    callback.handle(result.size() > 0 ? result.get(0) : null);
                });
    }

    public void loginById(Account account, Handler<JsonObject> callback) {
        query("SELECT * FROM awp_account WHERE id = ? and password = ?",
                new JsonArray().add(account.getId()).add(account.getPassword()),
                result -> {
                    callback.handle(result.size() > 0 ? result.get(0) : null);
                });
    }

    public void getAccountList(Handler<List<JsonObject>> callback){
        query("SELECT id,name,email FROM awp_account", callback);
    }

    public void updateWxPay(Account acc, Handler<Integer> callback) {
        if(acc.getId() == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        boolean moreThanOne = false;
        if (acc.getMchid() != null && !acc.getMchid().equals("")) {
            sql.append("mchId=?");
            params.add(acc.getMchid());
            moreThanOne = true;
        }
        if (acc.getMchkey() != null && !acc.getMchkey().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("mchKey=?");
            params.add(acc.getMchkey());
            moreThanOne = true;
        }
        if (acc.getWxpayon() != null) {
            if (moreThanOne) sql.append(",");
            sql.append("wxPayOn=?");
            params.add(acc.getWxpayon());
            moreThanOne = true;
        }
        sql.append(" where id=?");
        params.add(acc.getId());
        update(sql.toString(), params, callback);
    }

    public void updateZfbPay(Account acc, Handler<Integer> callback) {
        if(acc.getId() == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        boolean moreThanOne = false;
        if (acc.getZfbappid() != null && !acc.getZfbappid().equals("")) {
            sql.append("zfbAppId=?");
            params.add(acc.getZfbappid());
            moreThanOne = true;
        }
        if (acc.getZfbprivkey() != null && !acc.getZfbprivkey().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPrivKey=?");
            params.add(acc.getZfbprivkey());
            moreThanOne = true;
        }
        if (acc.getZfbpubkey() != null && !acc.getZfbpubkey().equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPubKey=?");
            params.add(acc.getZfbpubkey());
            moreThanOne = true;
        }
        if (acc.getZfbpayon() != null) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPayOn=?");
            params.add(acc.getZfbpayon());
            moreThanOne = true;
        }
        sql.append(" where id=?");
        params.add(acc.getId());
        update(sql.toString(), params, callback);
    }

    public List<Account> selectByUserId(int userID) {
        return null;
    }

    public List<Account> listForPage(Account searchEntity) {
        return null;
    }

}