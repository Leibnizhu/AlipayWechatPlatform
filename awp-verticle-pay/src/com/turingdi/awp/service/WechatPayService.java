package com.turingdi.awp.service;

import com.turingdi.awp.db.AccountDao;
import com.turingdi.awp.entity.wechat.WechatPay;
import com.turingdi.awp.util.common.CommonUtils;
import com.turingdi.awp.util.common.Constants;
import com.turingdi.awp.util.common.NetworkUtils;
import com.turingdi.awp.util.common.XmlUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

import static com.turingdi.awp.util.common.NetworkUtils.ContentType.XML;

/**
 * 支付服务类接口实现类
 *
 * @author Leibniz.Hu
 * Created on 2017-09-07 11:33.
 */
public class WechatPayService {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountDao accDao;
    private String wxPayNotifyUrl;

    public WechatPayService(AccountDao accDao) {
        this.accDao = accDao;
        this.wxPayNotifyUrl = Constants.PROJ_URL + "pay/wx/noti";
    }

    //统一下单接口
    private final static String WECHAT_UNIFY_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //退款接口
    private final static String WECHAT_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/wechatRefund";

    private static final JsonObject WECHAT_VERSION_ERROR = new JsonObject().put("status", "WECHAT_VERSION_LOW");
    private static final JsonObject ORDER_ERROR = new JsonObject().put("status", "ORDER_FAIL");

    /**
     * 调用微信统一下单接口
     *
     * @param product       产品名（会显示在微信支付推送的消息里面）
     * @param price         价格（单位：分）
     * @param openId        购买者的OpenID
     * @param orderId       本地订单ID
     * @param acc           商城对应企业用户
     * @param request       HTTP请求对象
     * @param forApiProcess 微信统一下单接口返回的数据的处理
     * @param forResponse   接口用于响应的Json，可能是 "WECHAT_VERSION_LOW"、"ORDER_FAIL"、成功的返回成功的JSON
     * @author Leibniz
     */
    public void wechatOrder(String product, int price, String openId, String orderId, JsonObject acc, HttpServerRequest request, Handler<JsonObject> forResponse, Handler<Map<String, String>> forApiProcess) {
        if (!testSupportPay(request)) {
            forResponse.handle(WECHAT_VERSION_ERROR);
            return;
        }

        //调用统一下单接口
        unifyPay(orderId, product, price, request.remoteAddress(), openId, null, wxPayNotifyUrl, acc, payParam -> {
            log.info("统一下单数据: " + payParam);
            //解析统一下单接口返回的xml数据
            Map<String, String> parsePayParam = XmlUtils.xmltoMap(payParam);

            //如果下单成功，则进行微信支付的js接口签名验证，并将签名的数据返回；否则返回下单失败
            if (parsePayParam == null) {
                forResponse.handle(ORDER_ERROR.put("errMsg", "XML_PARSE_ERROR"));
                return;
            }

            if ("SUCCESS".equals(parsePayParam.get("return_code"))) {
                //支付下单信息入库/其他处理
                forApiProcess.handle(parsePayParam);
                //微信支付js接口签名验证
                Map<String, Object> jsApiMap = new WechatPay(parsePayParam.get("prepay_id"), acc).getMap();
                JsonObject payJsonData = new JsonObject(jsApiMap).put("status", "SUCCESS");
                forResponse.handle(payJsonData);
            } else {
                log.error("统一订单失败：" + payParam);
                forResponse.handle(ORDER_ERROR.put("errMsg", parsePayParam.get("return_msg")));
            }
        });
    }


    private boolean testSupportPay(HttpServerRequest request) {
        String[] params = request.getHeader("user-agent").split(" ");
        for (String s : params) {
            if (s.contains("MicroMessenger")) {
                int version = Integer.valueOf(s.split("/")[1].substring(0, 1));
                return version >= 5;
            }
        }
        return true;
    }

    /**
     * 调用微信统一下单接口
     *
     * @param product 充值设备描述
     * @param price   充值设备价格
     * @param ip      充值端Ip
     * @param openId  充值的微信openId
     * @param acc
     * @return 微信统一下单接口返回的xml数据（String）
     *
     * @author Leibniz
     */
    private void unifyPay(String orderId, String product, int price, SocketAddress ip, String openId, String attach, String notUrl, JsonObject acc, Handler<String> callback) {
        Map<String, Object> map = new TreeMap<>();
        map.put("appid", acc.getString("appid"));
        map.put("mch_id", acc.getString("mchid"));
        map.put("nonce_str", CommonUtils.getRandomID());
        map.put("body", product);
        map.put("out_trade_no", orderId);
        map.put("total_fee", price);
        map.put("spbill_create_ip", ip.host());
        map.put("notify_url", notUrl);
        map.put("trade_type", "JSAPI");
        map.put("openid", openId);
        if (null != attach) {
            map.put("attach", attach);
        }
        map.put("sign", WechatPay.getWeixinPaySign(map, acc.getString("mchkey")));
        String xmlStr = null;
        String encode = "ISO8859-1";
        try {
            xmlStr = XmlUtils.simpleMapToXml(map, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.debug("下单请求数据：" + xmlStr);
        NetworkUtils.asyncPostStringWithData(WECHAT_UNIFY_PAY, xmlStr, XML, encode, callback);
    }

    /*

    *//**
     * 微信退款
     *
     * @param openId         退款订单对应用户的OpenID
     * @param refund         退款金额
     * @param totalFee       订单总金额
     * @param localOrderId   本地订单ID
     * @param enterpriseId   对应商城企业用户ID
     * @param refundCallback 退款成功之后的回调方法
     * @return "success" "fail" "订单不存在"
     *
     * @author Leibniz
     *//*
    public String wechatRefund(String openId, int refund, int totalFee, String localOrderId, int enterpriseId, Consumer<Map<String, String>> refundCallback) {
        Account wxAccount = wxDao.getById(enterpriseId);

        //设置退款需要的字段
        Map<String, Object> map = new TreeMap<>();
        map.put("appid", wxAccount.getAppid());
        map.put("mch_id", wxAccount.getMchid());
        map.put("nonce_str", CommonUtils.getRandomID());
        map.put("out_trade_no", localOrderId);//商户系统内部订单号
        map.put("out_refund_no", System.currentTimeMillis());//商户系统内部的退款单号
        map.put("total_fee", totalFee);
        map.put("refund_fee", refund);
//        map.put("op_user_id", wxAccount.getMchid());
        //map.put("refund_account", "REFUND_SOURCE_RECHARGE_FUNDS");//从账户可用余额退款，默认是REFUND_SOURCE_UNSETTLED_FUNDS 从未结算资金退款
        map.put("sign", WechatPay.getWeixinPaySign(map, wxAccount.getMchkey()));

        SSLConnectionSocketFactory sslsf = null;
        try {
            //读取退款需要用到的证书
            sslsf = readCert(wxAccount, enterpriseId);
        } catch (Exception e) {
            log.error("读取商户证书错误");
        }
        String result = null;
        try {
            //调用微信退款接口
            result = wechatRefund(sslsf, map);
            log.info("退款返回的xml数据：{} 本地订单号：{} 订单总金额：{} 退款金额：{}...", new Object[]{result, localOrderId, totalFee, refund});
        } catch (IOException e) {
            log.error("退款执行失败");
        }

        Map<String, String> refundReturnParam = null;
        try {
            refundReturnParam = xmltoMap(result);
        } catch (JDOMException e) {
            log.error("退款返回参数解析失败，查看退款返回参数是否正确");
        }
        if (refundReturnParam != null) { //退款成功，则更新用户订单
            if ("SUCCESS".equals(refundReturnParam.get("return_code")) && "SUCCESS".equals(refundReturnParam.get("result_code"))) {
                refundCallback.accept(refundReturnParam);
                return "success";
            } else if ("订单不存在".equals(refundReturnParam.get("err_code_des"))) {
                return "订单不存在";
            } else {
                log.debug(refundReturnParam.toString());
                return "fail";
            }
        } else {
            log.debug("refundReturnParam == null");
            return "fail";
        }
    }

    *//**
     * 支付成功后，微信会返回数据，解析微信返回的数据
     *
     * @param request http请求
     * @return 解析后的数据
     *
     * @author Leibniz
     *//*
    public String readPayParam(HttpServletRequest request) throws IOException {
        StringBuilder payResultXml = new StringBuilder();
        InputStream inStream = request.getInputStream();
        InputStreamReader isr = new InputStreamReader(inStream);
        BufferedReader br = new BufferedReader(isr);
        String s;
        while ((s = br.readLine()) != null) {
            payResultXml.append(s);
        }
        inStream.close();
        return payResultXml.toString();
    }

    */

    /**
     * 读取退款需要用到的证书
     *
     * @param wxAccount    微信账号信息
     * @param enterpriseId 企业用户ID
     * @return SSLConnectionSocketFactory
     *
     * @author Leibniz
     *//*
    private SSLConnectionSocketFactory readCert(Account wxAccount, int enterpriseId) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream instream = new FileInputStream(new File(certDir + enterpriseId + "_wxPay.p12"))) {
            keyStore.load(instream, wxAccount.getMchid().toCharArray());
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, wxAccount.getMchid().toCharArray()).build();
        return new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }

    *//**
     * 调用微信退款接口
     *
     * @param sslsf
     * @param map
     * @return
     *
     * @author Leibniz
     *//*
    private String wechatRefund(SSLConnectionSocketFactory sslsf, Map<String, Object> map) throws IOException {
        log.info("退款参数： " + XmlUtils.simpleMapToXml(map));
        StringBuilder result = new StringBuilder();
        try (CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build()) {
            HttpPost httpPost = new HttpPost(WECHAT_REFUND);
            StringEntity reqEntity = new StringEntity(XmlUtils.simpleMapToXml(map), "utf-8"); // 如果此处编码不对，可能导致客户端签名跟微信的签名不一致
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(entity.getContent(), "UTF-8"));
                    String text;
                    while ((text = bufferedReader.readLine()) != null) {
                        result.append(text);
                    }
                }
                EntityUtils.consume(entity);
            }
        } finally {
            return result.toString();
        }
    }*/
}
