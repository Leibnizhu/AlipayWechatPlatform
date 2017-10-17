package com.turingdi.awp.admin;

import com.turingdi.awp.base.SubRouter;
import com.turingdi.awp.db.AccountService;
import com.turingdi.awp.entity.db.Account;
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

/**
 * @author Leibniz.Hu
 * Created on 2017-10-17 11:50.
 */
public class PaySettingSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService accServ;
    private JWTAuth provider;
    private Vertx vertx;

    public PaySettingSubRouter(AccountService wxAccServ, JWTAuth provider) {
        this.accServ = wxAccServ;
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
        accServ.getById(eid, acc -> {
            JsonObject json = new JsonObject()
                    .put("wx", new JsonObject()
                            .put("appId", acc.getString("appid"))
                            .put("appSecret", acc.getString("appsecret"))
                            .put("mchId", acc.getString("mchId"))
                            .put("payKey", acc.getString("mchKey"))
                            .put("opened", acc.getInteger("wxPayOn")))
                    .put("zfb", new JsonObject()
                            .put("appId", acc.getString("zfbAppId"))
                            .put("appPrivKey", acc.getString("zfbPrivKey"))
                            .put("zfbPubKey", acc.getString("zfbPubKey"))
                            .put("opened", acc.getInteger("zfbPayOn")));
            rc.response().putHeader("content-type", "application/json; charset=utf-8").end(json.toString());
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
                if ("cert".equals(next.name())) {
                    String filePath = Constants.CERT_DIR + uid + "_wxPay.p12";
                    FileSystem fs = this.vertx.fileSystem();
                    Future<Void> delFuture = Future.future();
                    Future<Void> copyFuture = Future.future();
                    fs.delete(filePath, delFuture.completer());
                    fs.copy(next.uploadedFileName(), filePath, copyFuture.completer());
                    delFuture.compose(res -> {}, copyFuture);
                    copyFuture.setHandler(res -> {
                        if (res.succeeded()) {
                            log.info("复制文件{}到{}成功！", next.uploadedFileName(), filePath);
                        } else {
                            log.error("复制文件" + next.uploadedFileName() + "到" + filePath + "失败！", res.cause());
                        }
                    });
                    break;
                }
            }
        }

        //保存支付参数
        Account acc = new Account().setId(uid).setMchId(mchId).setMchKey(payKey).setWxPayOn(paySwitch);
        accServ.updateWxPay(acc, rows -> {
            rc.response().end(new JsonObject().put("status", rows > 0 ? "success" : "fail").toString());
        });
    }

    private void updateAlipayPaySetting(RoutingContext rc) {
        /*Map<String, String> json = new HashMap<>();
        if (paySwitch == 1 && !CommonUtils.notEmptyString(appId, appPrivKey, zfbPubKey)) {
            json.put("status", "invalid");
            return json;
        }
        //保存支付参数
        TWxcmsAccount wxAccount = new TWxcmsAccount().setId((long) uid).setZfbAppId(appId).setZfbPrivKey(appPrivKey).setZfbPubKey(zfbPubKey).setZfbPayOn(paySwitch);
        int affRows = payService.updateZfbPay(wxAccount);
        json.put("status", affRows == 1 ? "success" : "failure");
        return json;*/
    }
}
