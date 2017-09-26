package com.turingdi.awp.dao;


import com.turingdi.awp.entity.WxAccount;

import java.util.List;

public interface WxAccountDao {
	List<WxAccount> selectByUserId(int userID);

	int insert(WxAccount tWxcmsAccount);

	int update(WxAccount tWxcmsAccount);

	/**
	 * 该方法根据id从微信公众号表中获取公众号数据，具体实现在TWxcmsAccount.xml中；
	 *
	 * @param id 企业ID
	 * @return 相关公众号信息
	 * Create by quandong
	 */
	WxAccount getById(int id);

	WxAccount getByAccount(String account);

	WxAccount getSingleAccount();

	List<WxAccount> getAllAccount();

	List<WxAccount> listForPage(WxAccount searchEntity);

	void add(WxAccount entity);

	void delete(WxAccount entity);

	WxAccount getByAppId(String appId);

	int updateWxPay(WxAccount tWxcmsAccount);

	int updateZfbPay(WxAccount tWxcmsAccount);
}