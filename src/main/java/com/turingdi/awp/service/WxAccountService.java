package com.turingdi.awp.service;

import com.turingdi.awp.dao.WxAccountDao;
import com.turingdi.awp.entity.WxAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WxAccountService {
	@Autowired
	private WxAccountDao wxAccountDao;

	public List<WxAccount> selectByUserId(int userId) {
		return wxAccountDao.selectByUserId(userId);
	}

	public int insert(WxAccount WxAccount) {
		return wxAccountDao.insert(WxAccount);
	}

	public int update(WxAccount WxAccount) {
		return wxAccountDao.update(WxAccount);
	}

	/**
	 * 该方法用于从根据id获取微信公众号信息，通过调用dao层方法进行实现；
	 *
	 * @param id 公众号id
	 * @return 获取到的公众号数据
	 * Create by quandong
	 */
	public WxAccount getById(int id) {
		return wxAccountDao.getById(id); // 调用dao层方法
	}

	public List<WxAccount> listForPage(WxAccount searchEntity){
		return wxAccountDao.listForPage(searchEntity);
	}

	public void add(WxAccount entity){
		wxAccountDao.add(entity);
	}

	public void delete(WxAccount entity){
		wxAccountDao.delete(entity);
	}
}