package com.turingdi.awp.db;

import com.turingdi.awp.entity.db.Account;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class AccountService {
	private AccountDao wxAccountDao;

	public AccountService(Vertx vertx){
		this.wxAccountDao = new AccountDao(vertx);
	}

	public List<Account> selectByUserId(int userId) {
		return wxAccountDao.selectByUserId(userId);
	}

	public int insert(Account WxAccount) {
		return wxAccountDao.insert(WxAccount);
	}

	public int update(Account WxAccount) {
		return wxAccountDao.update(WxAccount);
	}

	/**
	 * 该方法用于从根据id获取微信公众号信息，通过调用dao层方法进行实现；
	 *
	 * @param id 公众号id
	 * @return 获取到的公众号数据
	 * Create by quandong
	 */
	public void getById(int id, Handler<JsonObject> callback) {
		wxAccountDao.getById(id, callback); // 调用dao层方法
	}

	public List<Account> listForPage(Account searchEntity){
		return wxAccountDao.listForPage(searchEntity);
	}

	public void add(Account entity){
		wxAccountDao.add(entity);
	}

	public void delete(Account entity){
		wxAccountDao.delete(entity);
	}
}