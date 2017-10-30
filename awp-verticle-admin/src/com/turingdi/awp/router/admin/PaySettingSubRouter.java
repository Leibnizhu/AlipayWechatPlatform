package com.turingdi.awp.router.admin;

import com.turingdi.awp.router.EventBusNamespace;
import com.turingdi.awp.router.JwtAccessSubRouter;
import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.common.CommonUtils;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.turingdi.awp.entity.db.Account.JsonKey.*;
import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * 支付配置Controller/SubRouter
 * 需要JWT授权
 * 功能：
 * 1. 获取当前支付配置
 * 2. 更新微信支付配置（允许上传支付证书）
 * 3. 更新支付宝支付配置
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-17 11:50.
 */
public class PaySettingSubRouter extends JwtAccessSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private Vertx vertx = Constants.vertx();
    private Router paySetRouter;

    public static Router create(Handler<RoutingContext> jwtHandler){
        return new PaySettingSubRouter(jwtHandler).subRouter();
    }

    private PaySettingSubRouter(Handler<RoutingContext> jwtHandler) {
        paySetRouter = Router.router(vertx);
        paySetRouter.route("/*").handler(jwtHandler);
        paySetRouter.get("/:eid").handler(this::getPaySetting);
        paySetRouter.post("/wx").handler(this::updateWechatPaySetting);
        paySetRouter.post("/zfb").handler(this::updateAlipayPaySetting);
    }

    @Override
    public Router subRouter() {
        return paySetRouter;
    }

    /**
     * 获取当前支付配置
     */
    private void getPaySetting(RoutingContext rc) {
        if (forbidAccess(rc, "eid", true)) {
            return;
        }
        Integer eid = Integer.parseInt(rc.request().getParam("eid"));
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), ar -> {
            if(ar.succeeded()){
                JsonObject acc = ar.result().body();
                JsonObject json = new JsonObject()
                        .put("wx", new JsonObject()
                                .put("appId", acc.getString(WXAPPID))
                                .put("appSecret", acc.getString(WXAPPSECRET))
                                .put("mchId", acc.getString(MCHID))
                                .put("payKey", acc.getString(MCHKEY))
                                .put("opened", acc.getInteger(WXPAYON)))
                        .put("zfb", new JsonObject()
                                .put("appId", acc.getString(ZFBAPPID))
                                .put("appPrivKey", acc.getString(ZFBPRIVKEY))
                                .put("zfbPubKey", acc.getString(ZFBPUBKEY))
                                .put("opened", acc.getInteger(ZFBPAYON)));
                rc.response().putHeader("content-type", "application/json; charset=utf-8").end(json.toString());
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                rc.response().setStatusCode(500).end("EventBus error!");
            }
        });
    }

    private void updateWechatPaySetting(RoutingContext rc) {
        if (forbidAccess(rc, "uid", true)) {
            return;
        }
        Set<FileUpload> uploads = rc.fileUploads();
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response().putHeader("content-type", "application/json; charset=utf-8");
        //解析参数
        Long uid = Long.parseLong(req.getParam("uid"));
        Integer paySwitch = Integer.parseInt(req.getParam("paySwitch"));
        String mchId = req.getParam("mchId");
        String payKey = req.getParam("payKey");

        //参数检查
        if (paySwitch == 1 && !CommonUtils.notEmptyString(mchId, payKey)) {
            resp.end(new JsonObject().put("status", "invalid").toString());
            return;
        }

        // 异步保存证书文件
        if (uploads != null && !uploads.isEmpty()) {
            for (FileUpload next : uploads) {
                if (paySwitch == 1 && "cert".equals(next.name()) && next.size() > 0) {
                    String filePath = Constants.CERT_DIR + uid + "_wxPay.p12";
                    FileSystem fs = this.vertx.fileSystem();
                    fs.exists(filePath, ex -> {
                        if(ex.succeeded()){
                            Future<Void> delFuture = Future.future();
                            Future<Void> copyFuture = Future.future();
                            fs.delete(filePath, delFuture.completer());
                            fs.copy(next.uploadedFileName(), filePath, copyFuture.completer());
                            if(ex.result()){
                                delFuture.compose(res -> {}, copyFuture);
                            }
                            copyFuture.setHandler(res -> {
                                if (res.succeeded()) {
                                    log.info("复制文件{}到{}成功！", next.uploadedFileName(), filePath);
                                } else {
                                    log.error("复制文件" + next.uploadedFileName() + "到" + filePath + "失败！", res.cause());
                                }
                            });
                        } else {
                            log.error("判断文件" + filePath + "是否存在时抛出异常！", ex.cause());
                        }
                    });
                    break;
                }
            }
        }

        //保存支付参数
        JsonObject acc = new JsonObject().put(ID, uid).put(MCHID, mchId).put(MCHKEY, payKey).put(WXPAYON, paySwitch);
        updatePaySetting(resp, acc, COMMAND_UPDATE_WECHATPAY);
    }

    private void updateAlipayPaySetting(RoutingContext rc) {
        if (forbidAccess(rc, "uid", true)) {
            return;
        }
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response().putHeader("content-type", "application/json; charset=utf-8");
        //解析参数
        Long uid = Long.parseLong(req.getParam("uid"));
        Integer paySwitch = Integer.parseInt(req.getParam("paySwitch"));
        String appId = req.getParam("appId");
        String appPrivKey = req.getParam("appPrivKey");
        String zfbPubKey = req.getParam("zfbPubKey");

        //参数检查
        if (paySwitch == 1 && !CommonUtils.notEmptyString(appId, appPrivKey, zfbPubKey)) {
            resp.end(new JsonObject().put("status", "invalid").toString());
            return;
        }

        //保存支付参数
        JsonObject acc = new JsonObject().put(ID, uid).put(ZFBAPPID, appId).put(ZFBPRIVKEY, appPrivKey).put(ZFBPUBKEY, zfbPubKey).put(ZFBPAYON, paySwitch);
        updatePaySetting(resp, acc, COMMAND_UPDATE_ALIPAY);
    }

    private void updatePaySetting(HttpServerResponse resp, JsonObject acc, EventBusNamespace command) {
        vertx.eventBus().<Integer>send(ADDR_ACCOUNT_DB.get(), makeMessage(command, acc), ar -> {
            if(ar.succeeded()){
                int rows = ar.result().body();
                resp.end(new JsonObject().put("status", rows > 0 ? "success" : "fail").toString());
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                resp.setStatusCode(500).end("EventBus error!");
            }
        });
    }
}
