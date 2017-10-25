package com.turingdi.awp.router.api;

import com.alipay.api.AlipayApiException;
import com.turingdi.awp.service.OrderService;
import com.turingdi.awp.entity.db.Order;
import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.service.AlipayPayService;
import com.turingdi.awp.util.alipay.AliPayApi;
import com.turingdi.awp.util.common.CommonUtils;
import com.turingdi.awp.util.common.NetworkUtils;
import com.turingdi.awp.util.common.TuringBase64Util;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.turingdi.awp.router.EventBusNamespace.ADDR_ACCOUNT_DB;
import static com.turingdi.awp.router.EventBusNamespace.COMMAND_GET_ACCOUNT_BY_ID;
import static com.turingdi.awp.router.EventBusNamespace.makeMessage;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:36.
 */
public class AlipayPaySubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AlipayPayService payServ;
    private OrderService orderServ;
    private Vertx vertx;


    public AlipayPaySubRouter(OrderService orderServ, AlipayPayService payServ) {
        this.payServ = payServ;
        this.orderServ = orderServ;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router payRouter = Router.router(vertx);
        payRouter.get("/order/:body").handler(this::alipayOrder);
        payRouter.post("/noti").handler(this::alipayNotify);
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
    private void alipayOrder(RoutingContext rc) {
        //请求json解码，获取订单参数
        JsonObject reqJson = new JsonObject(TuringBase64Util.decode(rc.request().getParam("body")));
        int eid = reqJson.getInteger("eid");
        String orderId = reqJson.getString("orderId");//orderId  本地订单ID
        int price = reqJson.getInteger("price");
        String name = reqJson.getString("name");
        String callback = reqJson.getString("callback");
        String successUrl = reqJson.getString("success");
        HttpServerResponse response = rc.response();
        //记录eid和orderId、callback关系
        Order order = new Order().setOrderId(orderId).setCallback(callback).setEid(eid).setType(1);
        orderServ.insert(order, rows -> {
        });
        vertx.eventBus().send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), ar -> {
            if(ar.succeeded()){
                JsonObject acc = (JsonObject) ar.result().body();
                payServ.alipayOrder(name, price, orderId, acc, successUrl, response);
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                response.setStatusCode(500).end("EventBus error!");
            }
        });
    }

    /**
     * 支付宝支付成功的回调方法
     * 结果直接写到响应里面
     *
     * @author Leibniz
     */
    private void alipayNotify(RoutingContext rc) {
        HttpServerRequest request=rc.request();
        HttpServerResponse response = rc.response();
        Map<String, String> params = AliPayApi.getRequestParams(request); // 解析请求参数
        String isSuccess = "success"; // 响应给支付宝的消息，默认是fail
        log.info("支付宝请求串:{}", params.toString()); // 打印本次请求日志，开发者自行决定是否需要

            String localOrderId = params.get("out_trade_no"); // 从解析后的数据获取本地订单号
        //通过本地订单号拿到eid以及callback，公钥
        orderServ.getByAlipayOrderId(localOrderId, res -> {
            try {
                AliPayApi.verifySign(params, res.getString("zfbPubKey")); // 调用支付宝提供的方法进行签名验证
                // 判断订单状态是否为成功，还有其他的状态，可以根据不同的业务需要进行判断
                if ("TRADE_SUCCESS".equals(params.get("trade_status"))) { // 支付成功
                    String alipayOrderId = params.get("trade_no");//从解析后的数据获取支付宝订单号
                    Order updateOrder = new Order().setPlatOrderId(alipayOrderId).setOrderId(localOrderId).setType(1);
                    orderServ.updateAfterPaid(updateOrder, rows -> {
                    });
                    // 调用下订单时候的callback
                    String callbackUrl = res.getString("callback");
                    String separator = callbackUrl.contains("?") ? "&" : "?";
                    String finalCallbackUrl = callbackUrl + separator + CommonUtils.mapToQueryString(params);
                    NetworkUtils.asyncGetString(finalCallbackUrl, str -> {
                        log.debug("调用支付宝回调地址{}返回结果：{}", finalCallbackUrl, str);
                    });
                }
            } catch (AlipayApiException e) { // 验签失败抛出异常
                log.error("异步通知验签失败", e); // 打印日志，便于调试错误
            } finally {
                response.putHeader("content-type", "text/html;charset=UTF-8").end(isSuccess);
            }
        });
    }
}
