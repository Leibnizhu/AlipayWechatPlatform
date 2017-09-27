package com.turingdi.awp.service;

import com.turingdi.awp.dao.AccountDao;
import com.turingdi.awp.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {
	@Autowired
	private AccountDao wxAccountDao;

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
	public Account getById(int id) {
		return wxAccountDao.getById(id); // 调用dao层方法
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