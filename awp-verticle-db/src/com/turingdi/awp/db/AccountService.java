package com.turingdi.awp.db;

import com.turingdi.awp.entity.db.Account;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class AccountService {
	private AccountDao accDao;

	public AccountService(Vertx vertx){
		this.accDao = new AccountDao(vertx);
	}

	public List<Account> selectByUserId(int userId) {
		return accDao.selectByUserId(userId);
	}

	public int insert(Account WxAccount) {
		return accDao.insert(WxAccount);
	}

	public void update(Account WxAccount, Handler<Integer> callback) {
		accDao.updateBase(WxAccount, callback);
	}

	/**
	 * 该方法用于从根据id获取微信公众号信息，通过调用dao层方法进行实现；
	 *
	 * @param id 公众号id
	 * @return 获取到的公众号数据
	 * Create by quandong
	 */
	public void getById(int id, Handler<JsonObject> callback) {
		accDao.getById(id, callback); // 调用dao层方法
	}

	public List<Account> listForPage(Account searchEntity){
		return accDao.listForPage(searchEntity);
	}

	public void add(Account entity){
		accDao.add(entity);
	}

	public void delete(Account entity){
		accDao.delete(entity);
	}

    public void login(String username, String password, Handler<JsonObject> callback) {
		Account account = new Account().setName(username).setPassword(password);
		accDao.login(account, callback);
    }

	public void getAccountList(Handler<List<JsonObject>> callback){
		accDao.getAccountList(callback);
	}

	public void updateWxPay(Account acc, Handler<Integer> callback){
		accDao.updateWxPay(acc, callback);
	}
}