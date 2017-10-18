package com.turingdi.awp.verticle;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.db.AccountService;
import com.turingdi.awp.service.AlipayPayService;
import com.turingdi.awp.util.alipay.AliPayApi;
import com.turingdi.awp.util.common.TuringBase64Util;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:36.
 */
public class AlipayPaySubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AlipayPayService payServ;
    private AccountService accServ;
    private Vertx vertx;


    public AlipayPaySubRouter(AccountService wxAccServ, AlipayPayService payServ) {
        this.accServ = wxAccServ;
        this.payServ = payServ;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router payRouter = Router.router(vertx);
        payRouter.get("/order/:body").handler(this::alipayOrder);
        payRouter.get("/noti").handler(this::alipayNotify);
        return payRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    /**
     * 支付宝支付，直接向响应写入支付宝返回的内容
     *
     * @author Leibniz
     */
    public void alipayOrder(RoutingContext rc) {
        //请求json解码，获取订单参数
        JsonObject reqJson = new JsonObject(TuringBase64Util.decode(rc.request().getParam("body")));
        int eid = reqJson.getInteger("eid");
        String orderId = reqJson.getString("orderId");//orderId  本地订单ID
        int price = reqJson.getInteger("price");
        String name = reqJson.getString("name");
        String callback = reqJson.getString("callback");
        HttpServerResponse response = rc.response();
        //TODO 记录eid和orderId、callback关系
        payServ.alipayOrder(name, price, orderId, eid, response);
    }

    /**
     * 支付宝支付成功的回调方法
     * 结果直接写到响应里面
     *
     * @author Leibniz
     */
    public void alipayNotify(RoutingContext rc) {
        HttpServerRequest request=rc.request();
        HttpServletResponse response=null;
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
            try {
                response.getWriter().write(isSuccess); // 直接将响应消息输出到页面
                response.getWriter().flush(); // 刷新
                response.getWriter().close(); // 关闭
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
