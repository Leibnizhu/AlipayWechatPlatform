package com.turingdi.awp.service;

import com.turingdi.awp.db.AccountDao;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * 账户信息相关的服务类，基本与DAO类一致，不再做注释
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-18 17:36.
 */
public class AccountService {
	private AccountDao accDao;

	public AccountService(AccountDao accDao){
		this.accDao = accDao;
	}

	public void update(JsonObject WxAccount, Handler<Integer> callback) {
		accDao.updateBase(WxAccount, callback);
	}

	public void getById(int id, Handler<JsonObject> callback) {
		accDao.getById(id, callback); // 调用dao层方法
	}

    public void loginByEmail(String email, String password, Handler<JsonObject> callback) {
		accDao.loginByEmail(email, password, callback);
    }

	public void loginById(long id, String password, Handler<JsonObject> callback) {
		accDao.loginById(id, password, callback);
	}

	public void getAccountList(Handler<JsonArray> callback){
		accDao.getAccountList(callback);
	}

	public void updateWxPay(JsonObject acc, Handler<Integer> callback){
		accDao.updateWxPay(acc, callback);
	}

	public void updateZfbPay(JsonObject acc, Handler<Integer> callback){
		accDao.updateZfbPay(acc, callback);
	}
}