package com.turingdi.awp.util.wechat;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 微信 API、微信基本接口
 * 
 */

public class WxApi {

	//token 接口
	private static final String TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
	
	//创建菜单
	private static final String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s";
	
	//创建个性化菜单
	private static final String MENU_ADDCONDITIONAL = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=%s";
	
	//删除菜单
	private static final String MENU_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s";
	
	//获取账号粉丝信息
	private static final String GET_FANS_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
	
	//获取账号粉丝列表
	private static final String GET_FANS_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s";
	
	//获取批量素材
	private static final String GET_BATCH_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=%s";
	
	//上传多媒体资料接口
	private static final String UPLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s";
	
	//上传永久素材：图文
	private static final String UPLOAD_NEWS = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=%s";
	
	//群发接口
	private static final String MASS_SEND = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=%s";
	
	//网页授权OAuth2.0获取code
	private static final String GET_OAUTH_CODE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s#wechat_redirect";
	
	//网页授权OAuth2.0获取token
	private static final String GET_OAUTH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
		
	//网页授权OAuth2.0获取用户信息
	private static final String GET_OAUTH_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";
	
	//生成二维码
	private static final String CREATE_QRCODE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
	
	//根据ticket获取二维码图片
	private static final String SHOW_QRCODE = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s";
	
	//js ticket
	private static final String GET_JSAPI_TICKET="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";
	
	//发送客服消息
	private static final String SEND_CUSTOM_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=%s";
	
	//模板消息接口
	private static final String SEND_TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

	//获取模板列表
	private static final  String GET_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=%s";
	
	
	//获取token接口
	public static String getTokenUrl(String appId,String appSecret){
		return String.format(TOKEN, appId, appSecret);
	}
	
	//获取上传Media接口
	public static String getUploadMediaUrl(String token,String type){
		return String.format(UPLOAD_MEDIA, token, type);
	}
	
	//获取菜单创建接口
	public static String getMenuCreateUrl(String token){
		return String.format(MENU_CREATE, token);
	}
	
	//获取个性化菜单创建接口
	public static String getMenuAddconditionalUrl(String token){
		return String.format(MENU_ADDCONDITIONAL, token);
	}
	
	//获取菜单删除接口
	public static String getMenuDeleteUrl(String token){
		return String.format(MENU_DELETE, token);
	}
	
	//获取粉丝信息接口
	public static String getFansInfoUrl(String token,String openid){
		return String.format(GET_FANS_INFO, token, openid);
	}
	
	//获取粉丝列表接口
	public static String getFansListUrl(String token,String nextOpenId){
		if(nextOpenId == null){
			return String.format(GET_FANS_LIST, token);
		}else{
			return String.format(GET_FANS_LIST + "&next_openid=%s", token, nextOpenId);
		}
	}
	
	//获取素材列表接口
	public static String getBatchMaterialUrl(String token){
		return String.format(GET_BATCH_MATERIAL, token);
	}
	
	//获取上传图文消息接口
	public static String getUploadNewsUrl(String token){
		return String.format(UPLOAD_NEWS, token);
	}
	
	//群发接口
	public static String getMassSendUrl(String token){
		return String.format(MASS_SEND, token);
	}
	
	//网页授权OAuth2.0获取code
	public static String getOAuthCodeUrl(String appId ,String redirectUrl ,String scope ,String state){
		return String.format(GET_OAUTH_CODE, appId, urlEnodeUTF8(redirectUrl), "code", scope, state);
	}
	
	//网页授权OAuth2.0获取token
	public static String getOAuthTokenUrl(String appId ,String appSecret ,String code ){
		return String.format(GET_OAUTH_TOKEN, appId, appSecret, code);
	}
	
	//网页授权OAuth2.0获取用户信息
	public static String getOAuthUserinfoUrl(String token ,String openid){
		return String.format(GET_OAUTH_USERINFO, token, openid);
	}
	
	//获取创建二维码接口url
	public static String getCreateQrcodeUrl(String token){
		return String.format(CREATE_QRCODE, token);
	}
	
	//获取显示二维码接口
	public static String getShowQrcodeUrl(String ticket){
		return String.format(SHOW_QRCODE, ticket);
	}
	
	//获取js ticket url
	public static String getJsApiTicketUrl(String token){
		return String.format(GET_JSAPI_TICKET, token);
	}
	
	//获取发送客服消息 url
	public static String getSendCustomMessageUrl(String token){
		return String.format(SEND_CUSTOM_MESSAGE, token);
	}
	
	//获取发送模板消息 url
	public static String getSendTemplateMessageUrl(String token){
		return String.format(SEND_TEMPLATE_MESSAGE, token);
	}

	//获取模板列表接口
	public static String getTemplateUrl(String token) { return  String.format(GET_TEMPLATE,token);}
	
	/**
	 * 获取创建临时二维码post data
	 * @param expireSecodes 该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为30秒。
	 * @param scene 临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000)
	 * @return
	 */
	public static String getQrcodeJson(Integer expireSecodes, Integer scene){
		String postStr = "{\"expire_seconds\":%d,\"action_name\":\"QR_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":%d}}";
		return String.format(postStr, expireSecodes, scene);
	}
	/**
	 * 获取创建临时二维码post data
	 * @param scene 临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000)
	 * @return
	 */
	public static String getQrcodeLimitJson(Integer scene){
		String postStr = "{\"action_name\":\"QR_LIMIT_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":%d}}";
		return String.format(postStr, scene);
	}
	//获取永久二维码
	public static String getQrcodeLimitJson(String sceneStr){
		String postStr = "{\"action_name\":\"QR_LIMIT_STR_SCENE\",\"action_info\":{\"scene\":{\"scene_str\":%s}}";
		return String.format(postStr, sceneStr);
	}
	
	//获取接口访问凭证
	public static AccessToken getAccessToken(String appId, String appSecret) {
		AccessToken token = null;
		String tockenUrl = WxApi.getTokenUrl(appId, appSecret);
		JSONObject jsonObject = httpsRequest(tockenUrl, HttpMethod.GET, null);
		if (null != jsonObject && !jsonObject.containsKey("errcode")) {
			try {
				token = new AccessToken();
				token.setAccessToken(jsonObject.getString("access_token"));
				token.setExpiresIn(jsonObject.getInt("expires_in"));
			} catch (JSONException e) {
				token = null;//获取token失败
			}
		}else if(null != jsonObject){
			System.out.println("获取AccessToken失败，原因=" + jsonObject.getString("errmsg"));
			token = new AccessToken();
			token.setErrcode(jsonObject.getInt("errcode"));
		}
		return token;
	}
	
	//获取js ticket
	public static JSTicket getJSTicket(String token){
		JSTicket jsTicket = null;
		String jsTicketUrl = WxApi.getJsApiTicketUrl(token);
		JSONObject jsonObject = httpsRequest(jsTicketUrl, HttpMethod.GET, null);
		if (null != jsonObject && jsonObject.containsKey("errcode") && jsonObject.getInt("errcode") == 0) {
			try {
				jsTicket = new JSTicket();
				jsTicket.setTicket(jsonObject.getString("ticket"));
				jsTicket.setExpiresIn(jsonObject.getInt("expires_in"));
			} catch (JSONException e) {
				jsTicket = null;//获取token失败
			}
		}else if(null != jsonObject){
			jsTicket = new JSTicket();
			jsTicket.setErrcode(jsonObject.getInt("errcode"));
		}
		return jsTicket;
	}
	
	//获取OAuth2.0 Token
	public static OAuthAccessToken getOAuthAccessToken(String appId, String appSecret,String code) {
		OAuthAccessToken token = null;
		String tockenUrl = getOAuthTokenUrl(appId, appSecret, code);
		JSONObject jsonObject = httpsRequest(tockenUrl, HttpMethod.GET, null);
		if (null != jsonObject && !jsonObject.containsKey("errcode")) {
			try {
				token = new OAuthAccessToken();
				token.setAccessToken(jsonObject.getString("access_token"));
				token.setExpiresIn(jsonObject.getInt("expires_in"));
				token.setOpenid(jsonObject.getString("openid"));
				token.setScope(jsonObject.getString("scope"));
			} catch (JSONException e) {
				token = null;//获取token失败
			}
		}else if(null != jsonObject){
			token = new OAuthAccessToken();
			token.setErrcode(jsonObject.getInt("errcode"));
		}
		return token;
	}
	
	/**
	 * 上传多媒体文件
	 * 返回media_id
	 */
	public static String uploadMedia(String accessToken, String mediaType, String mediaUri) {
		String uploadMediaUrl = String.format(UPLOAD_MEDIA, accessToken, mediaType);
		String boundary = "----------" + System.currentTimeMillis();// 设置边界  
		try {
			URL uploadUrl = new URL(uploadMediaUrl);
			HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
			uploadConn.setDoOutput(true);
			uploadConn.setDoInput(true);
			uploadConn.setRequestMethod("POST");
			// 设置请求头Content-Type
			uploadConn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
			// 获取媒体文件上传的输出流（往微信服务器写数据）
			OutputStream outputStream = uploadConn.getOutputStream();

			URL mediaUrl = new URL(mediaUri);
			HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
			meidaConn.setDoOutput(true);
			meidaConn.setRequestMethod("GET");

			// 从请求头中获取内容类型
			String contentType = meidaConn.getHeaderField("Content-Type");
			// 根据内容类型判断文件扩展名
			String fileExt = ".jpg";
			// 请求体开始
			outputStream.write(("--" + boundary + "\r\n").getBytes());
			outputStream.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"file1%s\"\r\n",fileExt).getBytes());
			outputStream.write(String.format("Content-Type: %s\r\n\r\n",contentType).getBytes());
			
			// 获取媒体文件的输入流（读取文件）
			BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1) {
				outputStream.write(buf, 0, size);
			}
			// 请求体结束
			outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
			outputStream.close();
			bis.close();
			meidaConn.disconnect();

			// 获取媒体文件上传的输入流（从微信服务器读数据）
			InputStream inputStream = uploadConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			uploadConn.disconnect();
			// 使用JSON-lib解析返回结果
			JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
			if(jsonObject.containsKey("media_id"))
				return jsonObject.getString("media_id");
			return null;
		} catch (Exception e) {
			String error = String.format("上传媒体文件失败：%s", e);
			System.out.println(error);
		}
		return null;
	}

	//发送请求
	public static JSONObject httpsRequest(String requestUrl, String requestMethod) {
		return httpsRequest(requestUrl,requestMethod,null);
	}
	
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			TrustManager[] tm = { new JEEWeiXinX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public static byte[] httpsRequestByte(String requestUrl, String requestMethod) {
		return httpsRequestByte(requestUrl,requestMethod,null);
	}
	
	public static byte[] httpsRequestByte(String requestUrl, String requestMethod, String outputStr) {
		try {
			TrustManager[] tm = { new JEEWeiXinX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			InputStream inputStream = conn.getInputStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
		    byte[] buffer = new byte[4096];
		    int n = 0;
		    while (-1 != (n = inputStream.read(buffer))) {
		        output.write(buffer, 0, n);
		    }
		    return output.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String urlEnodeUTF8(String str){
        String result = str;
        try {
            result = URLEncoder.encode(str,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
}

class JEEWeiXinX509TrustManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}