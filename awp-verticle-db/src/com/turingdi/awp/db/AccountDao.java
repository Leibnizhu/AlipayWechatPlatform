package com.turingdi.awp.db;


import com.turingdi.awp.entity.Account;

import java.util.List;

public interface AccountDao {
	List<Account> selectByUserId(int userID);

	int insert(Account tWxcmsAccount);

	int update(Account tWxcmsAccount);

	/**
	 * 该方法根据id从微信公众号表中获取公众号数据，具体实现在TWxcmsAccount.xml中；
	 *
	 * @param id 企业ID
	 * @return 相关公众号信息
	 * Create by quandong
	 */
	Account getById(int id);

	Account getByAccount(String account);

	Account getSingleAccount();

	List<Account> getAllAccount();

	List<Account> listForPage(Account searchEntity);

	void add(Account entity);

	void delete(Account entity);

	Account getByAppId(String appId);

	int updateWxPay(Account tWxcmsAccount);

	int updateZfbPay(Account tWxcmsAccount);
}