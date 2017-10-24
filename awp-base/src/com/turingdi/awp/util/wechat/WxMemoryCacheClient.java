package com.turingdi.awp.util.wechat;

import com.turingdi.awp.entity.db.Account;
import com.turingdi.awp.util.common.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存工具类；
 * 目前使用 服务器内存的方式；
 * 
 * 1、开发者可以根据自己的需求使用不同的缓存方式,比如memcached
 * 2、系统默认使用单个公众账号的缓存处理，如果有多个账号，请开发者自行处理
 * 
 */
public class WxMemoryCacheClient {

	//服务器内存的方式缓存account、accessToken、jsTicket
	private static Map<Long,Account> mpAccountMap = new HashMap<>();
	private static Map<Long,AccessToken> accountAccessTokenMap = new HashMap<>();
	private static Map<Long,JSTicket> accountJSTicketMap = new HashMap<>();
	
	//微信OAuth认证的时候，服务器内存的方式缓存openid; key=sessionid ，value=openid
	private static Map<String,String> sessionOpenIdMap = new HashMap<>();
	private static Map<Long,OAuthAccessToken> accountOAuthTokenMap = new HashMap<>();
	
	
	public static void addMpAccount(Account account){
		if(account != null /*&& !mpAccountMap.containsKey(account.getId())*/){
			mpAccountMap.put(account.getId(), account);
		}
	}

	/**
	 * 根据id获取公众号对象
	 * @param id
	 * @return
	 */
	public static Account getMpAccount(Long id){
		return mpAccountMap.get(id);
	}

	/**
	 * 根据公众号id获取公众号对象
	 * @param publicId 公众号ID
	 * @return Account
	 */
	public static Account getMpAccountByPublicid(String publicId){
		for(Map.Entry<Long, Account> entry : mpAccountMap.entrySet()){
			if(entry.getValue().getAccount().equals(publicId))
				return entry.getValue();
		}
		return null;
	}
	
	//获取唯一的公众号,如果需要多账号，请自行处理
/*	public static MpAccount getSingleMpAccount(){
		MpAccount sigleAccount = null;
		for(String key : mpAccountMap.keySet()){
			sigleAccount = mpAccountMap.get(key);
			break;
		}
		return sigleAccount;
	}*/
	
	public static AccessToken addAccessToken(Long id ,AccessToken token){
		if(token != null){
			accountAccessTokenMap.put(id, token);
		}
		return token;
	}
	
	/**
	 * accessToken的获取，绝对不要从缓存中直接获取，请从WxApiClient中获取；
	 * @param id
	 * @return
	 */
	public static AccessToken getAccessToken(Long id){
		return accountAccessTokenMap.get(id);
	}
	
	/**
	 * 获取唯一的公众号的accessToken,如果需要多账号，请自行处理
	 * accessToken的获取，绝对不要从缓存中直接获取，请从WxApiClient中获取；
	 * @return
	 */
/*	public static AccessToken getSingleAccessToken(){
		AccessToken accessToken = null;
		for(String key : accountAccessTokenMap.keySet()){
			accessToken = accountAccessTokenMap.get(key);
			break;
		}
		return accessToken;
	}*/
	
	/**
	 * 添加JSTicket到缓存
	 * @param id userid
	 * @param jsTicket
	 * @return
	 */
	public static JSTicket addJSTicket(Long id , JSTicket jsTicket){
		if(jsTicket != null){
			accountJSTicketMap.put(id, jsTicket);
		}
		return jsTicket;
	}
	
	/**
	 * JSTicket的获取，绝对不要从缓存中直接获取，请从JSTicket中获取；
	 * @param id
	 * @return
	 */
	public static JSTicket getJSTicket(Long id){
		return accountJSTicketMap.get(id);
	}
	
	/**
	 * 获取唯一的公众号的JSTicket,如果需要多账号，请自行处理
	 * JSTicket的获取，绝对不要从缓存中直接获取，请从WxApiClient中获取；
	 * @return
	 */
/*	public static JSTicket getSingleJSTicket(){
		JSTicket jsTicket = null;
		for(String key : accountJSTicketMap.keySet()){
			jsTicket = accountJSTicketMap.get(key);
			break;
		}
		return jsTicket;
	}*/
	
	
	//处理openid缓存
	public static String getOpenid(String sessionid){
		if(!StringUtils.isBlank(sessionid)){
			return sessionOpenIdMap.get(sessionid);
		}
		return null;
	}
	
	public static String setOpenid(String sessionid, String openid){
		if(!StringUtils.isBlank(sessionid) && !StringUtils.isBlank(openid)){
			sessionOpenIdMap.put(sessionid, openid);
		}
		return openid;
	}
	
	//处理OAuth的Token
	public static AccessToken addOAuthAccessToken(Long id ,OAuthAccessToken token){
		if(token != null){
			accountOAuthTokenMap.put(id, token);
		}
		return token;
	}
	
	/**
	 * OAuthAccessToken的获取，绝对不要从缓存中直接获取，请从WxApiClient中获取；
	 * @param id
	 * @return
	 */
	public static OAuthAccessToken getOAuthAccessToken(Long id){
		return accountOAuthTokenMap.get(id);
	}

	public static Account getMpAccount(int userId) {
		return getMpAccount((long)userId);
	}

	/**
	 * 获取唯一的公众号的accessToken,如果需要多账号，请自行处理
	 * OAuthAccessToken的获取，绝对不要从缓存中直接获取，请从WxApiClient中获取；
	 * @return
	 */
/*	public static OAuthAccessToken getSingleOAuthAccessToken(){
		OAuthAccessToken token = null;
		for(String key : accountOAuthTokenMap.keySet()){
			token = accountOAuthTokenMap.get(key);
			break;
		}
		return token;
	}*/
	
	
}


