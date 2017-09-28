package com.turingdi.awp.controller.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.turingdi.awp.entity.alipay.AliPayApi;
import com.turingdi.awp.service.AlipayPayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:36.
 */
@Controller
@RequestMapping("zfbPay")
public class AlipayPayController {
    private Logger log = LoggerFactory.getLogger(getClass());

    private final AlipayPayService payServ;

    @Autowired
    public AlipayPayController(AlipayPayService payServ) {
        this.payServ = payServ;
    }

    /**
     * 支付宝支付，直接向响应写入支付宝返回的内容
     *
     * @param orderId  本地订单ID
     * @param response HTTP响应对象
     * @author Leibniz
     */
    @RequestMapping(value = "order/{eid}/{orderId}/{price}/{name}/{callback}", method = GET)
    @ResponseBody
    public void alipayOrder(@PathVariable int eid, @PathVariable String orderId, @PathVariable int price, @PathVariable String name, @PathVariable String callback, HttpServletResponse response) {
        //TODO 记录eid和orderId、callback关系
        payServ.alipayOrder(name, price, orderId, eid, response);
    }

    /**
     * 支付宝支付成功的回调方法
     * 结果直接写到响应里面
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @author Leibniz
     */
    @RequestMapping("noti")
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = AliPayApi.getRequestParams(request); // 解析请求参数
        String isSuccess = "success"; // 响应给支付宝的消息，默认是fail
        log.info("支付宝请求串", params.toString()); // 打印本次请求日志，开发者自行决定是否需要

        try {
            String localOrderId = params.get("out_trade_no"); // 从解析后的数据获取本地订单号
            //TODO 通过本地订单号拿到eid以及callback，从而拿到公钥
            AliPayApi.verifySign(params, /*orderServ.getAlipayPublicKey(*/localOrderId); // 调用支付宝提供的方法进行签名验证

            // 判断订单状态是否为成功，还有其他的状态，可以根据不同的业务需要进行判断
            if ("TRADE_SUCCESS".equals(params.get("trade_status"))) { // 支付成功
                String alipayOrderId = params.get("trade_no");//从解析后的数据获取支付宝订单号
                // TODO 调用下订单时候的callback

            }
        } catch (AlipayApiException e) { // 验签失败抛出异常
            log.info("异步通知验签失败"); // 打印日志，便于调试错误
            e.printStackTrace(); // 输出异常
        } finally {
            response.setContentType("text/html;charset=" + AlipayConstants.CHARSET_UTF8); // 设置文本类型及编码
            response.getWriter().write(isSuccess); // 直接将响应消息输出到页面
            response.getWriter().flush(); // 刷新
            response.getWriter().close(); // 关闭
        }
    }
}
