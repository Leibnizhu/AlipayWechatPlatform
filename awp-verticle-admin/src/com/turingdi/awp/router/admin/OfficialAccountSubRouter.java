package com.turingdi.awp.router.admin;

import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.service.AccountService;
import com.turingdi.awp.entity.db.Account;
import com.turingdi.awp.util.common.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.JWTAuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-13 12:44.
 */
public class OfficialAccountSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService wxAccServ;
    private JWTAuth provider;
    private Vertx vertx;

    public OfficialAccountSubRouter(AccountService wxAccServ, JWTAuth provider) {
        this.wxAccServ = wxAccServ;
        this.provider = provider;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router offAccRouter = Router.router(vertx);
        offAccRouter.route("/*").handler(JWTAuthHandler.create(provider));
        offAccRouter.get("/").handler(this::getOfficialAccount);
        offAccRouter.get("/all").handler(this::getAccountList);
        offAccRouter.get("/:id").handler(this::getAccountById);
        offAccRouter.put("/").handler(this::updateOfficialAccount);
        offAccRouter.put("/pswd").handler(this::updateEmailPassword);
        return offAccRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    private void getOfficialAccount(RoutingContext rc) {
        JsonObject jwtJson = rc.user().principal();
        int userId = jwtJson.getInteger("id");
        wxAccServ.getById(userId, offAcc -> {
            responseOneAccount(rc, offAcc);
        });
    }

    /**
     * @param rc
     * @param checkId true=需要管理员权限，或者非管理员但ID相等才允许访问；false=只有管理员允许访问
     * @return true=禁止访问 false=允许访问
     */
    private boolean forbidAccess(RoutingContext rc, boolean checkId) {
        JsonObject jwtJson = rc.user().principal();
        int role = jwtJson.getInteger("role");
        if (checkId) {
            int queryId = Integer.parseInt(rc.request().getParam("id"));
            int jwtId = jwtJson.getInteger("id");
            if (role != 0 && queryId != jwtId) {
                rc.response().setStatusCode(403).end();
                return true;
            }
        } else {
            if ((role != 0)) {
                rc.response().setStatusCode(403).end();
                return true;
            }
        }
        return false;
    }

    private void getAccountById(RoutingContext rc) {
        if (forbidAccess(rc, true)) {
            return;
        }
        int queryId = Integer.parseInt(rc.request().getParam("id"));
        wxAccServ.getById(queryId, offAcc -> {
            responseOneAccount(rc, offAcc);
        });
    }

    private void responseOneAccount(RoutingContext rc, JsonObject offAcc) {
        JsonObject result = new JsonObject()
                .put("id", offAcc.getInteger("id"))
                .put("name", offAcc.getString("name"))
                .put("appid", offAcc.getString("appid"))
                .put("appsecret", offAcc.getString("appsecret"))
                .put("verify", offAcc.getString("verify"))
                .put("role", offAcc.getInteger("role"))
                .put("projUrl", Constants.PROJ_URL);
        rc.response().putHeader("content-type", "application/json; charset=utf-8").end(result.toString());
    }

    private void getAccountList(RoutingContext rc) {
        if (forbidAccess(rc, false)) {
            return;
        }
        wxAccServ.getAccountList(rows -> {
            rc.response().putHeader("content-type", "application/json; charset=utf-8").end(rows.toString());
        });
    }

    private void updateOfficialAccount(RoutingContext rc) {
        if (forbidAccess(rc, true)) {
            return;
        }
        HttpServerRequest req = rc.request();
        Long id = Long.valueOf(req.getParam("id"));
        String name = req.getParam("name");
        String appid = req.getParam("appid");
        String appsecret = req.getParam("appsecret");
        String verify = req.getParam("verify");
        Account acc = new Account().setId(id).setName(name).setAppid(appid).setAppsecret(appsecret).setVerify(verify);
        wxAccServ.update(acc, rows -> {
            rc.response().putHeader("content-type", "application/json; charset=utf-8").end(rows > 0 ? "success" : "fail");
        });
    }

    private void updateEmailPassword(RoutingContext rc) {
        if (forbidAccess(rc, true)) {
            return;
        }
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response().putHeader("content-type", "text/plain; charset=utf-8");
        Long id = Long.valueOf(req.getParam("id"));
        String oldPassword = req.getParam("oldPassword");
        wxAccServ.loginById(id, oldPassword, acc -> {
            if (acc == null) {
                rc.response().end("errPswd");
                return;
            }
            String email = req.getParam("email");
            String newPassword = req.getParam("newPassword");
            boolean needUpdatePassword = false;
            if (newPassword != null && newPassword.trim().length() > 0) {
                String rePassword = req.getParam("rePassword");
                if (rePassword == null || rePassword.trim().length() == 0) {
                    resp.setStatusCode(500).end("EMPTY_REPEAT_PSWD");
                    return;
                }
                if (!newPassword.trim().equals(rePassword.trim())) {
                    resp.setStatusCode(500).end("PSWD_NOT_EQUAL");
                    return;
                }
                needUpdatePassword = true;
            }
            Account updateAcc = new Account().setId(id).setEmail(email);
            if (needUpdatePassword) {
                updateAcc.setPassword(newPassword);
            }
            wxAccServ.update(updateAcc, rows -> {
                rc.response().end(rows > 0 ? "success" : "fail");
            });
        });
    }
}
