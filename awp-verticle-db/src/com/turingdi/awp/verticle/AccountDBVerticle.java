package com.turingdi.awp.verticle;

import com.turingdi.awp.db.AccountDao;
import com.turingdi.awp.router.EventBusNamespace;
import com.turingdi.awp.service.AccountService;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.router.EventBusNamespace.ADDR_ACCOUNT_DB;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-24 22:14.
 */
public class AccountDBVerticle extends AbstractDatabaseAccessVerticle {
    protected Logger log = LoggerFactory.getLogger(getClass());
    private AccountService accountSrv;

    public AccountDBVerticle() {
        super(ADDR_ACCOUNT_DB);
        AccountDao accDao = new AccountDao();
        accountSrv = new AccountService(accDao);
    }

    @Override
    protected Logger log() {
        return log;
    }

    @Override
    protected void processMethods(Message<JsonObject> msg, JsonArray params, EventBusNamespace method) {
        switch (method) {
            case COMMAND_GET_ACCOUNT_BY_ID:
                getAccountById(msg, params);
                break;
            case COMMAND_EMAIL_LOGIN:
                loginByEmail(msg, params);
                break;
            case COMMAND_ID_LOGIN:
                loginById(msg, params);
                break;
            case COMMAND_GET_ALL_ACCOUNT:
                getAllAccount(msg);
                break;
            case COMMAND_UPDATE_NORMAL:
                updateNormalInfo(msg, params);
                break;
            case COMMAND_UPDATE_WECHATPAY:
                updateWechatPay(msg, params);
                break;
            case COMMAND_UPDATE_ALIPAY:
                updateAlipayPay(msg, params);
                break;
            default:
                log.error("未能处理的请求方法：{}", method);
        }
    }

    private void getAccountById(Message<JsonObject> msg, JsonArray params) {
        int eid = params.getInteger(0);
        accountSrv.getById(eid, msg::reply);
    }

    private void loginByEmail(Message<JsonObject> msg, JsonArray params) {
        String email = params.getString(0);
        String password = params.getString(1);
        accountSrv.loginByEmail(email, password, msg::reply);
    }

    private void loginById(Message<JsonObject> msg, JsonArray params) {
        Long id = params.getLong(0);
        String password = params.getString(1);
        accountSrv.loginById(id, password, msg::reply);
    }

    private void getAllAccount(Message<JsonObject> msg) {
        accountSrv.getAccountList(accList -> msg.reply(new JsonArray(accList)));
    }

    private void updateNormalInfo(Message<JsonObject> msg, JsonArray params) {
        JsonObject acc = params.getJsonObject(0);
        accountSrv.update(acc, msg::reply);
    }

    private void updateWechatPay(Message<JsonObject> msg, JsonArray params) {
        JsonObject acc = params.getJsonObject(0);
        accountSrv.updateWxPay(acc, msg::reply);
    }

    private void updateAlipayPay(Message<JsonObject> msg, JsonArray params) {
        JsonObject acc = params.getJsonObject(0);
        accountSrv.updateZfbPay(acc, msg::reply);
    }
}
