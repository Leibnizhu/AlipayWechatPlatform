package com.turingdi.awp.db;


import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.turingdi.awp.entity.db.Account.JsonKey.*;

public class AccountDao extends BaseVertXDao {
    private Logger log = LoggerFactory.getLogger(getClass());

    public AccountDao() {
    }

    /**
     * 更新账户基本公众号配置
     */
    public void updateBase(JsonObject acc, Handler<Integer> callback) {
        Integer id = acc.getInteger(ID);
        if(id == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        boolean moreThanOne = false;
        String name = acc.getString(NAME);
        if (name != null && !name.equals("")) {
            sql.append("name=?");
            params.add(name);
            moreThanOne = true;
        }
        String appid = acc.getString(WXAPPID);
        if (appid != null && !appid.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("appid=?");
            params.add(appid);
            moreThanOne = true;
        }
        String appsecret = acc.getString(WXAPPSECRET);
        if (appsecret != null && !appsecret.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("appsecret=?");
            params.add(appsecret);
            moreThanOne = true;
        }
        String verify = acc.getString(VERIFY);
        if (verify != null && !verify.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("verify=?");
            params.add(verify);
            moreThanOne = true;
        }
        String email = acc.getString(EMAIL);
        if (email != null && !email.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("email=?");
            params.add(email);
            moreThanOne = true;
        }
        String password = acc.getString(PASSWORD);
        if (password != null && !password.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("password=?");
            params.add(password);
        }
        sql.append(" where id=?");
        params.add(id);
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
                result -> callback.handle(result.size() > 0 ? result.get(0) : null));
    }

    public void loginByEmail(String email, String password, Handler<JsonObject> callback) {
        query("SELECT * FROM awp_account WHERE email = ? and password = ?",
                new JsonArray().add(email).add(password),
                result -> callback.handle(result.size() > 0 ? result.get(0) : null));
    }

    public void loginById(long id, String password, Handler<JsonObject> callback) {
        query("SELECT * FROM awp_account WHERE id = ? and password = ?",
                new JsonArray().add(id).add(password),
                result -> callback.handle(result.size() > 0 ? result.get(0) : null));
    }

    public void getAccountList(Handler<List<JsonObject>> callback){
        query("SELECT id,name,email FROM awp_account ORDER BY ID ASC", callback);
    }

    public void updateWxPay(JsonObject acc, Handler<Integer> callback) {
        Integer id = acc.getInteger(ID);
        if(id == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        boolean moreThanOne = false;
        String mchid = acc.getString(MCHID);
        if (mchid != null && !mchid.equals("")) {
            sql.append("mchId=?");
            params.add(mchid);
            moreThanOne = true;
        }
        String mchkey = acc.getString(MCHKEY);
        if (mchkey != null && !mchkey.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("mchKey=?");
            params.add(mchkey);
            moreThanOne = true;
        }
        Integer wxpayon = acc.getInteger(WXPAYON);
        if (wxpayon != null) {
            if (moreThanOne) sql.append(",");
            sql.append("wxPayOn=?");
            params.add(wxpayon);
        }
        sql.append(" where id=?");
        params.add(id);
        update(sql.toString(), params, callback);
    }

    public void updateZfbPay(JsonObject acc, Handler<Integer> callback) {
        Integer id = acc.getInteger(ID);
        if(id == null){
            throw new IllegalArgumentException("Account ID cannot be null!!!");
        }
        StringBuilder sql = new StringBuilder("update awp_account set ");
        JsonArray params = new JsonArray();
        boolean moreThanOne = false;
        String zfbappid = acc.getString(ZFBAPPID);
        if (zfbappid != null && !zfbappid.equals("")) {
            sql.append("zfbAppId=?");
            params.add(zfbappid);
            moreThanOne = true;
        }
        String zfbprivkey = acc.getString(ZFBPRIVKEY);
        if (zfbprivkey != null && !zfbprivkey.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPrivKey=?");
            params.add(zfbprivkey);
            moreThanOne = true;
        }
        String zfbpubkey = acc.getString(ZFBPUBKEY);
        if (zfbpubkey != null && !zfbpubkey.equals("")) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPubKey=?");
            params.add(zfbpubkey);
            moreThanOne = true;
        }
        Integer zfbpayon = acc.getInteger(ZFBPAYON);
        if (zfbpayon != null) {
            if (moreThanOne) sql.append(",");
            sql.append("zfbPayOn=?");
            params.add(zfbpayon);
        }
        sql.append(" where id=?");
        params.add(id);
        update(sql.toString(), params, callback);
    }
}