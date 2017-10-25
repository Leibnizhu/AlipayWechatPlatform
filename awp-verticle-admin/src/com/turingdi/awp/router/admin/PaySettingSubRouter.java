package com.turingdi.awp.router.admin;

import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.common.CommonUtils;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-17 11:50.
 */
public class PaySettingSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private JWTAuth provider;
    private Vertx vertx;

    public PaySettingSubRouter(JWTAuth provider) {
        this.provider = provider;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router paySetRouter = Router.router(vertx);
        paySetRouter.route("/*").handler(JWTAuthHandler.create(provider));
        paySetRouter.get("/:eid").handler(this::getPaySetting);
        paySetRouter.post("/wx").handler(this::updateWechatPaySetting);
        paySetRouter.post("/zfb").handler(this::updateAlipayPaySetting);
        return paySetRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    private void getPaySetting(RoutingContext rc) {
        Integer eid = Integer.parseInt(rc.request().getParam("eid"));
        vertx.eventBus().<JsonObject>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_GET_ACCOUNT_BY_ID, eid), ar -> {
            if(ar.succeeded()){
                JsonObject acc = ar.result().body();
                JsonObject json = new JsonObject()
                        .put("wx", new JsonObject()
                                .put("appId", acc.getString("appid"))
                                .put("appSecret", acc.getString("appsecret"))
                                .put("mchId", acc.getString("mchid"))
                                .put("payKey", acc.getString("mchkey"))
                                .put("opened", acc.getInteger("wxpayon")))
                        .put("zfb", new JsonObject()
                                .put("appId", acc.getString("zfbappid"))
                                .put("appPrivKey", acc.getString("zfbprivkey"))
                                .put("zfbPubKey", acc.getString("zfbpubkey"))
                                .put("opened", acc.getInteger("zfbpayon")));
                rc.response().putHeader("content-type", "application/json; charset=utf-8").end(json.toString());
            } else {
                log.error("EventBus消息响应错误", ar.cause());
                rc.response().setStatusCode(500).end("EventBus error!");
            }
        });
    }

    private void updateWechatPaySetting(RoutingContext rc) {
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
        JsonObject acc = new JsonObject().put("id", uid).put("mchid", mchId).put("mchkey", payKey).put("wxpayon", paySwitch);
        updatePlatformOrderId(resp, acc);
    }

    private void updateAlipayPaySetting(RoutingContext rc) {
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
        JsonObject acc = new JsonObject().put("id", uid).put("zfbappid", appId).put("zfbprivkey", appPrivKey).put("zfbpubkey", zfbPubKey).put("zfbpayon", paySwitch);
        updatePlatformOrderId(resp, acc);
    }

    private void updatePlatformOrderId(HttpServerResponse resp, JsonObject acc) {
        vertx.eventBus().<Integer>send(ADDR_ACCOUNT_DB.get(), makeMessage(COMMAND_UPDATE_ALIPAY, acc), ar -> {
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
