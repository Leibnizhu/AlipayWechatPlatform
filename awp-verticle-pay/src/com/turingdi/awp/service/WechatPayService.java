package com.turingdi.awp.service;

import com.turingdi.awp.dao.AccountDao;
import com.turingdi.awp.entity.db.Account;
import com.turingdi.awp.entity.wechat.WechatPay;
import com.turingdi.awp.util.CommonUtils;
import com.turingdi.awp.util.Constants;
import com.turingdi.awp.util.NetworkUtils;
import com.turingdi.awp.util.XmlUtils;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * 支付服务类接口实现类
 *
 * @author Leibniz.Hu
 * Created on 2017-09-07 11:33.
 */
@Service
public class WechatPayService {
    private Logger log = LoggerFactory.getLogger(getClass());

    private String certDir;
    private String projectUrl;
    private String wxPayNotifyUrl;

    //统一下单接口
    private final static String WECHAT_UNIFY_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //退款接口
    private final static String WECHAT_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/wechatRefund";


    private final AccountDao wxDao;

    @Autowired
    public WechatPayService(Constants constants, AccountDao wxDao) {
        this.wxDao = wxDao;
        this.certDir = constants.CERT_DIR;
        this.projectUrl = constants.PROJ_URL;
        this.wxPayNotifyUrl = projectUrl + "mb/pay/wxNotify";
    }

    /**
     * 调用微信统一下单接口
     *
     * @param product      产品名（会显示在微信支付推送的消息里面）
     * @param price        价格（单位：分）
     * @param openId       购买者的OpenID
     * @param orderId      本地订单ID
     * @param enterpriseId 商城对应企业用户ID
     * @param request      HTTP请求对象
     * @return "当前微信版本号过低"、"下单失败"、成功的返回成功的JSON
     *
     * @author Leibniz
     */
    public String wechatOrder(String product, int price, String openId, String orderId, int enterpriseId, HttpServletRequest request, Consumer<Map<String, String>> payCallback) throws IOException {
        if (!testSupportPay(request)) {
            return "当前微信版本号过低";
        }

        //调用统一下单接口
        String payParam = null;
        Account wxAccount = wxDao.getById(enterpriseId);
        try {
            payParam = unifyPay(orderId, product, price, request.getRemoteAddr(), openId, null, wxPayNotifyUrl, wxAccount);
            log.info("统一下单数据: " + payParam);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(orderId + "下单失败，请查看微信支持开发配置是否正确");
        }

        //解析统一下单接口返回的xml数据
        Map<String, String> parsePayParam = null;
        try {
            parsePayParam = xmlAnalysis(payParam);
        } catch (JDOMException e) {
            log.error("统一订单返回的数据解析失败,查看统一下单返回的数据是否错误" + payParam);
        }

        //如果下单成功，则进行微信支付的js接口签名验证，并将签名的数据返回；否则返回下单失败
        if (parsePayParam == null) {
            return "下单失败";
        }

        if ("SUCCESS".equals(parsePayParam.get("return_code"))) {
            //支付下单信息入库/其他处理
            payCallback.accept(parsePayParam);

            //微信支付js接口签名验证
            Map<String, Object> jsApiMap = new WechatPay(parsePayParam.get("prepay_id"), wxAccount).getMap();
            JSONObject payJsonData = JSONObject.fromObject(jsApiMap);
            return payJsonData.toString();
        } else {
            log.error("统一订单失败：" + payParam);
            return "下单失败";
        }
    }

    private boolean testSupportPay(HttpServletRequest request) {
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
     */
    public String wechatRefund(String openId, int refund, int totalFee, String localOrderId, int enterpriseId, Consumer<Map<String, String>> refundCallback) {
        Account wxAccount = wxDao.getById(enterpriseId);

        //设置退款需要的字段
        Map<String, Object> map = new TreeMap<>();
        map.put("appid", wxAccount.getAppid());
        map.put("mch_id", wxAccount.getMchId());
        map.put("nonce_str", CommonUtils.getRandomID());
        map.put("out_trade_no", localOrderId);//商户系统内部订单号
        map.put("out_refund_no", System.currentTimeMillis());//商户系统内部的退款单号
        map.put("total_fee", totalFee);
        map.put("refund_fee", refund);
//        map.put("op_user_id", wxAccount.getMchId());
        //map.put("refund_account", "REFUND_SOURCE_RECHARGE_FUNDS");//从账户可用余额退款，默认是REFUND_SOURCE_UNSETTLED_FUNDS 从未结算资金退款
        map.put("sign", WechatPay.getWeixinPaySign(map, wxAccount.getMchKey()));

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
            refundReturnParam = xmlAnalysis(result);
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

    /**
     * 支付成功后，微信会返回数据，解析微信返回的数据
     *
     * @param request http请求
     * @return 解析后的数据
     *
     * @author Leibniz
     */
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

    /**
     * 解析xml数据，并将解析后的数据传入map里面
     *
     * @param xmlData xml数据
     * @return 解析后的数据
     *
     * @author Leibniz
     */
    public Map<String, String> xmlAnalysis(String xmlData) throws JDOMException {
        StringReader read = new StringReader(xmlData);
        // 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        SAXBuilder sbx = new SAXBuilder();
        Document doc = null;
        try {
            doc = sbx.build(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert doc != null;
        Element root = doc.getRootElement();
        List es = root.getChildren();
        Map<String, String> retMap = new HashMap<>();
        if (es != null && es.size() != 0) {
            for (Object obj : es) {
                if (obj instanceof Element) {
                    Element element = (Element) obj;
                    retMap.put(element.getName(), element.getText());
                }
            }
        }
        return retMap;
    }

    /**
     * 调用微信统一下单接口
     *
     * @param product 充值设备描述
     * @param price   充值设备价格
     * @param ip      充值端Ip
     * @param openId  充值的微信openId
     * @return 微信统一下单接口返回的xml数据（String）
     *
     * @author Leibniz
     */
    private String unifyPay(String orderId, String product, int price, String ip, String openId, String attach, String notUrl, Account wxAccount) throws IOException {
        Map<String, Object> map = new TreeMap<>();
        map.put("appid", wxAccount.getAppid());
        map.put("mch_id", wxAccount.getMchId());
        map.put("nonce_str", CommonUtils.getRandomID());
        map.put("body", new String(product.getBytes("UTF-8")));
        map.put("out_trade_no", orderId);
        map.put("total_fee", price);
        map.put("spbill_create_ip", ip);
        map.put("notify_url", notUrl);
        map.put("trade_type", "JSAPI");
        map.put("openid", openId);
        if (null != attach) {
            map.put("attach", attach);
        }
        map.put("sign", WechatPay.getWeixinPaySign(map, wxAccount.getMchKey()));
        System.out.println("下单请求数据：" + XmlUtils.simpleMapToXml(map, "ISO8859-1"));
        return NetworkUtils.postRequestWithData(WECHAT_UNIFY_PAY, XmlUtils.simpleMapToXml(map, "ISO8859-1"), "xml");
    }

    /**
     * 读取退款需要用到的证书
     *
     * @param wxAccount    微信账号信息
     * @param enterpriseId 企业用户ID
     * @return SSLConnectionSocketFactory
     *
     * @author Leibniz
     */
    private SSLConnectionSocketFactory readCert(Account wxAccount, int enterpriseId) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream instream = new FileInputStream(new File(certDir + enterpriseId + "_wxPay.p12"))) {
            keyStore.load(instream, wxAccount.getMchId().toCharArray());
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, wxAccount.getMchId().toCharArray()).build();
        return new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
    }

    /**
     * 调用微信退款接口
     *
     * @param sslsf
     * @param map
     * @return
     *
     * @author Leibniz
     */
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
    }
}
