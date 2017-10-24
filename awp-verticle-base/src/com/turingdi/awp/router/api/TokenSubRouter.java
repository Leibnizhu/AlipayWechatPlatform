package com.turingdi.awp.router.api;

import com.turingdi.awp.service.AccountService;
import com.turingdi.awp.entity.db.Account;
import com.turingdi.awp.router.SubRouter;
import com.turingdi.awp.util.wechat.WxApiClient;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-19 22:11.
 */
public class TokenSubRouter extends LanAccessSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountService wxAccServ;
    private Vertx vertx;

    public TokenSubRouter(AccountService wxAccServ) {
        this.wxAccServ = wxAccServ;
    }

    @Override
    public Router getSubRouter() {
        if (vertx == null) {
            throw new IllegalStateException("Please set Vertx before you call getSubRouter()!!!");
        }
        Router tokenRouter = Router.router(vertx);
        tokenRouter.post("/jst/:eid").handler(this::getJsTicket);
        tokenRouter.post("/act/:eid").handler(this::getAccessToken);
        return tokenRouter;
    }

    @Override
    public SubRouter setVertx(Vertx vertx) {
        this.vertx = vertx;
        return this;
    }

    private void getAccessToken(RoutingContext rc) {
        responseWithToken(rc, "jsTicket", WxApiClient::getAccessToken);
    }

    private void getJsTicket(RoutingContext rc) {
        responseWithToken(rc, "jsTicket", WxApiClient::getJSTicket);
    }

    private void responseWithToken(RoutingContext rc, String key, Function<Account, String> tokenGetter) {
        if (refuseNonLanAccess(rc))
            return;
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        Integer eid = -1;
        try {
            eid = Integer.parseInt(req.getParam("eid"));
        } catch (NullPointerException e) {
            resp.setStatusCode(500).end("Need request parameter: eid!   ");
        } catch (NumberFormatException e) {
            resp.setStatusCode(500).end("Request parameter eid must be a number!");
        }
        wxAccServ.getById(eid, acc -> {
            String token = tokenGetter.apply(acc.mapTo(Account.class));
            resp.end(new JsonObject().put(key, token).toString());
        });
    }

}
