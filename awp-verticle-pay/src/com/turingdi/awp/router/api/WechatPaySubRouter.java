package com.turingdi.awp.router.api;

import com.turingdi.awp.entity.db.Order;
import com.turingdi.awp.entity.wechat.WechatJdk;
import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.service.OrderService;
import com.turingdi.awp.service.WechatPayService;
import com.turingdi.awp.util.common.XmlUtils;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 14:35.
 */
public class WechatPaySubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private WechatPayService payServ = new WechatPayService();
    private OrderService orderServ;
    private Vertx vertx;


    public WechatPaySubRouter(OrderService orderServ) {
        this.orderServ = orderServ;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router payRouter = Router.router(vertx);
        payRouter.get("/pre/:eid/:url").handler(this::wechatPreHandle);
        payRouter.post("/order").handler(this::wechatOrder);
        payRouter.post("/noti").handler(this::wechatNotify);
        return payRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    private static final String WECHAT_CALLBACK_SUCCESS_RETURN = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

    /**
     * 微信支付的预处理，js的wx.config需要用
     *
     * @return wx.config需要用的数据
     *
     * @author Leibniz
     */
    private void wechatPreHandle(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse response = rc.response();
        int eid = Integer.parseInt(req.getParam("eid"));
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), ar -> {
            if (ar.succeeded()) {
                JsonObject acc = ar.result().body();
                String curUrl = null;
                try {
                    curUrl = URLDecoder.decode(req.getParam("url"), "UTF-8");
                } catch (UnsupportedEncodingException ignore) {
                }
                //调用微信jdk类
                Map<String, String> jdkMap = new WechatJdk(req, acc, curUrl).getMap();
                jdkMap.put("appId", acc.getString("appid"));
                String jsonStr = JsonObject.mapFrom(jdkMap).toString();
                log.debug(jsonStr);
                response.putHeader("content-type", "application/json;charset=UTF-8").end(jsonStr);
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                response.setStatusCode(500).end("EventBus error!");
            }
        });
    }

    /**
     * 调用微信统一下单接口，获取prepay_id等信息
     *
     * @return "已支付" "已取消订单" 或者统一下单接口返回的数据，包含prepay_id
     *
     * @author Leibniz
     */
    private void wechatOrder(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        JsonObject body = rc.getBodyAsJson();
        int eid = body.getInteger("eid");
        String orderId = body.getString("orderId");
        String openId = body.getString("openId");
        int price = body.getInteger("price");
        String name = body.getString("name");
        String callback = body.getString("callback");
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), ar -> {
            if (ar.succeeded()) {
                JsonObject acc = ar.result().body();
                payServ.wechatOrder(name, price, openId, orderId, acc, req,
                        forResponse -> {
                            String jsonStr = forResponse.toString();
                            log.debug(jsonStr);
                            rc.response().putHeader("content-type", "application/json;charset=UTF-8").end(jsonStr);
                        },
                        orderSuccessMap -> {
                            // 下单成功之后的处理
                            log.debug(orderSuccessMap.toString());
                            Order wechatOrder = new Order().setOrderId(orderId).setEid(eid).setType(0).setCallback(callback);
                            orderServ.insert(wechatOrder, rows -> {
                                log.info("微信下单后更新数据库，影响行数={}", rows);
                            });
                        });
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                rc.response().setStatusCode(500).end("EventBus error!");
            }
        });
    }

    /**
     * 微信支付回调接口
     * 更新订单状态（更新shop_order，已支付，商城订单号，支付类型，支付时间）
     *
     * @return 成功则返回微信接口规定的信息，失败则返回“下单失败”
     *
     * @author Leibniz
     */
    private void wechatNotify(RoutingContext rc) {
        String param = rc.getBody().toString();
        log.info("支付成功后返回的xml数据: " + param);
        //将解析后的数据传入map
        Map<String, String> payReturnParam = XmlUtils.xmltoMap(param);
        //TODO 完善其他返回状态的处理
        if ("SUCCESS".equals(payReturnParam.get("return_code"))) {
            String localOrderId = payReturnParam.get("out_trade_no"); //本地订单ID
            String wechatOrderId = payReturnParam.get("transaction_id"); //微信订单ID
            //调用callback 更新订单数据，已支付，商城订单号，支付类型，支付时间
            Order updateOrder = new Order().setPlatOrderId(wechatOrderId).setOrderId(localOrderId).setType(0);
            orderServ.updateAfterPaid(updateOrder, rows -> {
                log.info("微信支付回调更新数据库，影响行数={}", rows);
            });
            rc.response().putHeader("content-type", "application/xml; charset=utf-8").end(WECHAT_CALLBACK_SUCCESS_RETURN);
        } else {
            rc.response().end("下单失败");
        }
    }
}