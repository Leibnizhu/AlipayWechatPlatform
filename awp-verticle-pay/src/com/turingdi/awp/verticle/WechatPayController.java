package com.turingdi.awp.verticle;

import com.turingdi.awp.entity.Account;
import com.turingdi.awp.entity.wechat.WechatJdk;
import com.turingdi.awp.service.AccountService;
import com.turingdi.awp.service.WechatPayService;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.DigestException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:35.
 */
@Controller
@RequestMapping("wxPay")
public class WechatPayController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String WECHAT_CALLBACK_SUCCESS_RETURN = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

    @Autowired
    private AccountService accountServ;
    @Autowired
    private WechatPayService payServ;
    /**
     * 微信支付的预处理，js的wx.config需要用
     *
     * @param orderId 本地订单ID
     * @param request HTTP请求对象
     * @return wx.config需要用的数据, 已支付或已取消订单的返回空{}
     *
     * @author Leibniz
     */
    @RequestMapping(value = "wxPre/{eid}/{orderId}/{openId}", method = GET)
    @ResponseBody
    public Map<String, String> wechatPayPreHandle(@PathVariable int eid, @PathVariable String orderId, @PathVariable String openId, HttpServletRequest request) throws DigestException {
        Account account = accountServ.getById(eid);
        //调用微信jdk类
        Map<String, String> jdkMap = new WechatJdk(request, account).getMap();
        jdkMap.put("appId", account.getAppid());
        jdkMap.put("openId", openId);
        log.info(jdkMap.toString());
        return jdkMap;
    }

    /**
     * 调用微信统一下单接口，获取prepay_id等信息
     *
     * @param orderId 本地订单ID
     * @param request HTTP请求对象
     * @return "已支付" "已取消订单" 或者统一下单接口返回的数据，包含prepay_id
     *
     * @author Leibniz
     */
    @RequestMapping(value = "wxOrder/{eid}/{orderId}/{openId}/{price}/{name}/{callback}", method = POST)
    @ResponseBody
    public String wechatOrder(@PathVariable int eid, @PathVariable String orderId, @PathVariable String openId, @PathVariable int price, @PathVariable String name, @PathVariable String callback, HttpServletRequest request) throws IOException {
        String result = payServ.wechatOrder(name, price, openId, orderId, eid, request, resultMap -> {
            //TODO 下单成功之后的处理
            log.debug(resultMap.toString());
        });
        log.info(result);
        return result;
    }

    /**
     * 微信支付回调接口
     * 更新订单状态（更新shop_order，已支付，商城订单号，支付类型，支付时间）
     *
     * @param param 回调请求体内容
     * @return 成功则返回微信接口规定的信息，失败则返回“下单失败”
     *
     * @throws JDOMException XML解析的异常
     * @throws IOException   请求接口的异常
     * @author Leibniz
     */
    @RequestMapping("wxNotify")
    @ResponseBody
    public String wechatNotify(@RequestBody String param) throws JDOMException, IOException {
        log.info("支付成功后返回的xml数据: " + param);
        //将解析后的数据传入map
        Map<String, String> payReturnParam = payServ.xmlAnalysis(param);
        //TODO 完善其他返回状态的处理
        if ("SUCCESS".equals(payReturnParam.get("return_code"))) {
            String localOrderId = payReturnParam.get("out_trade_no"); //本地订单ID
            String wechatOrderId = payReturnParam.get("transaction_id"); //微信订单ID
            //TODO 调用callback 更新shop_order，已支付，商城订单号，支付类型，支付时间
            return WECHAT_CALLBACK_SUCCESS_RETURN;
        } else {
            return "下单失败";
        }
    }
}
