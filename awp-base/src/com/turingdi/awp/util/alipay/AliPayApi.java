package com.turingdi.awp.util.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayOpenPublicMessageSingleSendResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.antgroup.zmxy.openplatform.api.DefaultZhimaClient;
import com.antgroup.zmxy.openplatform.api.ZhimaApiException;
import com.antgroup.zmxy.openplatform.api.request.ZhimaCreditScoreGetRequest;
import com.antgroup.zmxy.openplatform.api.response.ZhimaCreditScoreGetResponse;
import com.turingdi.awp.entity.db.Account;
import com.turingdi.awp.entity.alipay.*;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 该类是阿里支付宝的一个封装工具类，目前包含了验证签名、手机网页支付、发送模板消息、授权验证等功能；
 * Created by quandong on 17-6-21.
 */
public class AliPayApi {
    private static Logger LOG = LoggerFactory.getLogger(AliPayApi.class); // 日志对象，用于打日志

    /**
     * 该方法实现了支付宝手机网页的支付功能;
     * 方法将调用支付宝支付页面需要用到的数据拼接和设置好，然后，写到响应中，前台会自动弹出支付宝支付页面；
     * 其中，支付金额要求大于0.01，并且小于100000000，需要调用者自己检查该值再传进来，否则页面会报获取不到订单信息错误；
     *
     * @param aliAccountInfo 保存了支付宝账户的信息，包括appId、用户私钥、支付宝公钥和回调地址等，具体参考AliAccountInfo类的属性说明
     * @param payBizContent 保存了订单信息，包括本地订单号、金额等，具体参考BizContent类的属性说明
     * Create by quandong
     */
    public static void wapPay(AliAccountInfo aliAccountInfo, PayBizContent payBizContent, HttpServerResponse httpResponse) throws IOException {
        AlipayClient alipayClient = AliPayCliFactory.getAlipayClient(aliAccountInfo); // 从客户端工厂中获取AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest(); // 创建API对应的request
        String form = "";

        alipayRequest.setReturnUrl(aliAccountInfo.getReturnUrl()); // 设置回跳地址
        alipayRequest.setNotifyUrl(aliAccountInfo.getNotifyUrl()); // 设置通知地址
        alipayRequest.setBizContent(payBizContent.toString()); // 填充业务参数

        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        httpResponse.putHeader("Content-Type", "text/html;charset=" + AlipayConstants.CHARSET_UTF8); // 设置文本类型及编码
        httpResponse.end(form); // 直接将完整的表单html输出到页面
    }

    /**
     * 该方法对支付宝提供的验签方法进行了一层封装，验签失败时抛出异常，调用者需要在调用方法中捕获异常；
     *
     * @param params 支付宝回调通知中的请求参数
     * @param alipayPublicKey 支付宝公钥
     * Create by quandong
     */
    public static void verifySign(Map<String, String> params, String alipayPublicKey) throws AlipayApiException {
        // 调用支付宝的验签方法，并判断验签是否成功
        if (!AlipaySignature.rsaCheckV1(params, alipayPublicKey, AlipayConstants.CHARSET_UTF8, AlipayConstants.SIGN_TYPE_RSA2)) { // 验签失败
            throw new AlipayApiException("verify sign fail."); // 抛出异常，调用者可以根据该异常判断验签是否成功
        }
    }

    /**
     * 该方法用于从请求链接中获取请求的参数，然后存放在一个Map中；
     *
     * @param request http请求体
     * @return params 请求参数的Key-Value格式
     * Create by quandong
     */
    public static Map<String, String> getRequestParams(HttpServerRequest request) {
        Map<String, String> params = new HashMap<>(); // 创建一个Map，用于存放请求参数

        // 判断http请求体是否为空
        if(null != request) { // http请求体不为空
            Set<String> paramsKey = request.params().names(); // 获取请求参数的Key集合
            // 遍历请求参数的Key
            for(String key : paramsKey) {
                params.put(key, request.getParam(key)); // 将请求参数的Key和Value存放到params中
            }
        }
        return params; // 返回请求参数map
    }

    /**
     * 该方法对向支付宝发送请求唤醒授权页面进行了封装；
     * 方法先根据调用者传来的参数拼接好发送给支付宝服务器的Url；
     * 然后利用响应体的重定向方法发送请求；
     *
     * @param aliAccountInfo 封装了发送给支付宝数据的对象
     * @param response http响应体
     * Create by quandong
     */
    public static void auth(AliAccountInfo aliAccountInfo, HttpServerResponse response, boolean needDetail) throws IOException {
        String authReqUrl = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=" + aliAccountInfo.getAppId() + "&scope=" + (needDetail?"auth_user,auth_base":"auth_base") + "&redirect_uri=" + URLEncoder.encode(aliAccountInfo.getRedirectUrl(), AlipayConstants.CHARSET_UTF8); // 将参数拼接到发送给支付宝的的链接上
        response.setStatusCode(302)
                .putHeader("Location", authReqUrl) // 通过响应体的重定向方法，向支付宝发送请求，页面上会弹出支付宝授权页面
                .end();
    }

    /**
     * 该方法是用户确定授权后的获取用户信息的方法；
     * 方法先解析请求参数，获取auth_code；
     * 获取到auth_code后，用auth_code换取auth_token和openId；
     * 然后，判断用户是否需要获取用户详细信息，如果需要，则用auth_token获取用户详细信息，并返回AlipayUserInfoShareResponse响应对象;
     * 如果不需要，直接返回AlipaySystemOauthTokenResponse响应对象；
     * 其他的意外情况均返回null；
     *
     * @param aliAccountInfo 封装了获取用户详细信息需要用到的数据的对象
     * @param isNeedDetail 是否需要获取详细信息的标识
     * @param request http请求体
     * @return AlipayResponse类型，调用者根据是否需要获取详细信息的标识强转成不同类型的对象，false强转成AlipaySystemOauthTokenResponse，true强转成AlipayUserInfoShareResponse，意外情况返回空
     * Create by quandong
     */
    private static AlipayResponse getUserInfo(AliAccountInfo aliAccountInfo, boolean isNeedDetail, HttpServerRequest request) throws IOException {
        Map<String, String> params = AliPayApi.getRequestParams(request); // 解析请求参数
        String authCode = params.get("auth_code"); // 获得authCode

        try {
            // 判断是否能获取到authCode
            if(null != authCode && !authCode.equals("")) { // 能获取到authCode
                AlipayClient alipayClient = AliPayCliFactory.getAlipayClient(aliAccountInfo); // 获取支付宝连接
                // 利用authCode获得authToken
                AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest(); // 创建支付宝系统授权token请求对象
                oauthTokenRequest.setCode(authCode); // 设置auth_code
                oauthTokenRequest.setGrantType("authorization_code"); // 设置同意类型，值为authorization_code时，代表用code换取；值为refresh_token时，代表用refresh_token换取
                AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(oauthTokenRequest); // 向支付宝发送请求并获得响应

                // 判断是否换取到authToken
                if(null != oauthTokenResponse && oauthTokenResponse.isSuccess()) { // 成功获得authToken
                    // 判断是否需要获取用户的详细信息
                    if(isNeedDetail) { // 需要获取用户的详细信息
                        // 利用authToken获取用户信息
                        AlipayUserInfoShareRequest userinfoShareRequest = new AlipayUserInfoShareRequest(); // 创建用户信息共享请求对象
                        AlipayUserInfoShareResponse userinfoShareResponse = alipayClient.execute(userinfoShareRequest, oauthTokenResponse.getAccessToken()); // 向支付宝发送请求并获得响应
//                        AlipayUserUserinfoShareRequest userinfoShareRequest = new AlipayUserUserinfoShareRequest();
//                        AlipayUserUserinfoShareResponse userinfoShareResponse = alipayClient.execute(userinfoShareRequest, oauthTokenResponse.getAccessToken());
//                        LOG.debug("======================用户真实姓名={}======================", userinfoShareResponse.getRealName());

                        // 判断是否能获得用户信息
                        if(null != userinfoShareResponse && userinfoShareResponse.isSuccess()) { // 成功获得用户信息
                            LOG.info("获取用户信息成功：{}", userinfoShareResponse.getBody()); // 获取用户信息成功，打日志
                            return userinfoShareResponse; // 返回响应对象，调用者可以用该对象获取用户的详细信息
                        } else { // 获取用户信息失败
                            LOG.error("获取用户信息失败"); // 获取用户信息失败，打日志
                            return null; // 返回空
                        }
                    } else { // 不需要获取用户的详细信息
                        LOG.info("获取用户openId成功:{}", oauthTokenResponse.getUserId()); // 获取用户openId成功，打日志
                        return oauthTokenResponse; // 返回响应对象，调用者可以用该对象获取用户的openId
                    }
                } else { // 换取不到authToken
                    LOG.error("authCode换取authToken失败"); // authCode换取authToken失败，打日志
                    return null; // 返回空
                }
            } else { // 获取不到authCode
                LOG.error("authCode获取失败"); // authCode获取失败，打日志
                return null; // 返回空
            }
        } catch (AlipayApiException alipayApiException) {
            LOG.error("获取oauthToken或用户信息失败"); // 获取oauthToken或用户信息失败，打日志
            // 自行处理异常
            alipayApiException.printStackTrace();
        }
        return null; // 返回空，正常时不会执行此语句
    }

    /**
     * 用户确定授权后的获取用户详细信息；
     *
     * @param aliAccountInfo 封装了获取用户详细信息需要用到的数据的对象
     * @param request http请求体
     * @return AlipayUserInfoShareResponse实例，用于获取用户详细信息
     * @throws IOException
     */
    public static AlipayUserInfoShareResponse getUserDetailInfo(AliAccountInfo aliAccountInfo, HttpServerRequest request) throws IOException{
        return (AlipayUserInfoShareResponse) getUserInfo(aliAccountInfo, true, request); // 调用上面方法
    }

    /**
     * 用户确定授权后的获取用户简要信息，主要是OpenID；
     *
     * @param aliAccountInfo 封装了获取用户详细信息需要用到的数据的对象
     * @param request http请求体
     * @return AlipaySystemOauthTokenResponse实例，用于获取用于简要信息
     * @throws IOException
     */
    public static AlipaySystemOauthTokenResponse getUserId(AliAccountInfo aliAccountInfo, HttpServerRequest request) throws IOException{
        return (AlipaySystemOauthTokenResponse) getUserInfo(aliAccountInfo, false, request); // 调用上面方法
    }

    /**
     * 发送支付宝模板消息API；
     *
     * @param tplMsg 模板消息对象
     * @return 是否调用成功
     * @author Leibniz
     */
    public static boolean sendTemplateMessage(TemplateMessage tplMsg, Account wxAccount) {
        AliAccountInfo aliAccountInfo = new AliAccountInfo(wxAccount.getZfbappid(), wxAccount.getZfbprivkey(), wxAccount.getZfbpubkey(), null, null, null);
        AlipayClient alipayClient = AliPayCliFactory.getAlipayClient(aliAccountInfo); // 获取支付宝连接
        AlipayOpenPublicMessageSingleSendRequest request = new AlipayOpenPublicMessageSingleSendRequest(); // 创建发送模版消息请求
        request.setBizContent(tplMsg.toString()); // 设置请求的业务内容
        AlipayOpenPublicMessageSingleSendResponse response = null;

        try {
            response = alipayClient.execute(request); // 发送请求，并获得响应
        } catch (AlipayApiException e) { // 捕获异常
            LOG.error("调用支付宝模板消息接口时抛出AlipayApiException异常", e); // 打异常日志
        }

        // 判断响应是否为空
        if (response != null) { // 响应为空
            // 判断响应是否成功
            if(response.isSuccess()) { // 响应成功，即发送模版消息成功
                LOG.debug("调用成功code={},msg={}", new Object[]{response.getCode(), response.getMsg()}); // 打成功日志
                return true; // 返回发送成功标识
            } else { // 响应失败，即发送模版消息失败
                LOG.error("调用失败code={},subCode={},subMsg={}", new Object[]{response.getCode(), response.getSubCode(), response.getSubMsg()}); // 打失败日志
            }
        } else { // 响应为空
            LOG.error("调用支付宝模板消息接口时发生异常，返回响应对象为null！"); // 打异常日志
        }
        return false; // 返回发送失败标识
    }

    /**
     * 支付宝退款接口；
     *
     * @param bizContent 支付宝退款接口的业务内容请求JSON实例
     * @return 是否调用成功
     * @author Leibniz
     */
    public static boolean refund(RefundBizContent bizContent, Account wxAccount){
        String bizContentStr = bizContent.toString(); // 将参数bizContent转成字符串

        // 检查bizContent是否合法
        if(!bizContent.checkParameters()){
            throw new IllegalArgumentException("错误的支付宝退款接口请求业务JSON：" + bizContentStr); // 抛出异常
        }

        AliAccountInfo aliAccountInfo = new AliAccountInfo(wxAccount.getZfbappid(), wxAccount.getZfbprivkey(), wxAccount.getZfbpubkey(), null, null, null);
        AlipayClient alipayClient = AliPayCliFactory.getAlipayClient(aliAccountInfo); // 获取支付宝连接
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest(); // 创建退款请求
        request.setBizContent(bizContentStr); // 设置请求的bizContent
        AlipayTradeRefundResponse response = null;

        try {
            response = alipayClient.execute(request); // 发送退款请求并获得响应
        } catch (AlipayApiException e) { // 捕捉异常
            LOG.error("调用支付宝退款接口时抛出异常，请求的JSON：" + bizContentStr, e); // 打日志
        }

        // 判断是否有响应
        if (response != null) { // 响应成功
            // 判断退款是否成功
            if (response.isSuccess()) { // 退款成功
                LOG.info("调用成功"); // 打日志
                return true; // 退款成功
            } else { // 退款失败
                LOG.error("调用支付宝退款接口错误，code={}，msg={}，sub_code={}，sub_msg={}", new Object[]{response.getCode(), response.getMsg(), response.getSubCode(), response.getSubMsg()}); // 打日志
            }
        } else { // 响应失败
            LOG.error("调用支付宝退款接口时发生异常，返回响应对象为null！{}", bizContentStr); // 打日志
        }

        return false; // 退款失败
    }

    /**
     * 芝麻信用评分接口的订单号；
     */
    private static final AtomicLong counter = new AtomicLong();

    /**
     * 获取指定用户的芝麻信用评分；
     *
     * @param aliAccountInfo 保存了支付宝账户的信息，包括appId、用户私钥、支付宝公钥和回调地址等，具体参考AliAccountInfo类的属性说明
     * @param openId 要查询的用户的OpenID
     * @return 指定用户的芝麻信用评分
     */
    public static String getZhimaCreditScore(AliAccountInfo aliAccountInfo, String openId) {
        ZhimaCreditScoreGetRequest req = new ZhimaCreditScoreGetRequest();
        //固定参数
        req.setChannel("apppc");
        req.setPlatform("zmop");
        req.setProductCode("w1010100100000000001");
        req.setApiVersion("1.0");
        //变动参数
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        DecimalFormat df = new DecimalFormat("0000000000000");
        req.setTransactionId(sdf.format(new Date()) + df.format(counter.incrementAndGet()));// 必要参数
        req.setOpenId(openId);// 必要参数
        DefaultZhimaClient client = AliPayCliFactory.getZhimaClient(aliAccountInfo);
        try {
            ZhimaCreditScoreGetResponse response = client.execute(req);
            System.out.println(response.isSuccess());
            System.out.println(response.getErrorCode());
            System.out.println(response.getErrorMessage());
            return response.getZmScore();
        } catch(ZhimaApiException e) {
            LOG.error("调用芝麻信用评分接口时抛出ZhimaApiException异常", e);
        }
        return "";
    }
}