package com.turingdi.awp.verticle;

import com.turingdi.awp.db.AccountDao;
import com.turingdi.awp.router.EventBusNamespace;
import com.turingdi.awp.service.AccountService;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.router.EventBusNamespace.ADDR_ACCOUNT_DB;

/**
 * AWP_ACCOUNT表相关的数据库操作Verticle服务
 *
 * @author Leibniz.Hu
 * Created on 2017-10-24 22:14.
 */
@SuppressWarnings("unchecked")
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

    /**
     * 分发请求并处理
     * 下面具体的方法如方法名所示，与DAO层方法命名一致，在此不表
     *
     * @author Leibniz.Hu
     */
    @Override
    protected <T> void processMethods(Handler<T> replyMsg, JsonArray params, EventBusNamespace method) {
        switch (method) {
            case COMMAND_GET_ACCOUNT_BY_ID:
                getAccountById(replyMsg, params);
                break;
            case COMMAND_EMAIL_LOGIN:
                loginByEmail(replyMsg, params);
                break;
            case COMMAND_ID_LOGIN:
                loginById(replyMsg, params);
                break;
            case COMMAND_GET_ALL_ACCOUNT:
                getAllAccount(replyMsg);
                break;
            case COMMAND_UPDATE_NORMAL:
                updateNormalInfo(replyMsg, params);
                break;
            case COMMAND_UPDATE_WECHATPAY:
                updateWechatPay(replyMsg, params);
                break;
            case COMMAND_UPDATE_ALIPAY:
                updateAlipayPay(replyMsg, params);
                break;
            default:
                log.error("未能处理的请求方法：{}", method);
        }
    }

    private void getAccountById(Handler replyMsg, JsonArray params) {
        int eid = params.getInteger(0);
        accountSrv.getById(eid, replyMsg);
    }

    private void loginByEmail(Handler replyMsg, JsonArray params) {
        String email = params.getString(0);
        String password = params.getString(1);
        accountSrv.loginByEmail(email, password, replyMsg);
    }

    private void loginById(Handler replyMsg, JsonArray params) {
        Long id = params.getLong(0);
        String password = params.getString(1);
        accountSrv.loginById(id, password, replyMsg);
    }

    private void getAllAccount(Handler replyMsg) {
        accountSrv.getAccountList(replyMsg);
    }

    private void updateNormalInfo(Handler replyMsg, JsonArray params) {
        JsonObject acc = params.getJsonObject(0);
        accountSrv.update(acc, replyMsg);
    }

    private void updateWechatPay(Handler replyMsg, JsonArray params) {
        JsonObject acc = params.getJsonObject(0);
        accountSrv.updateWxPay(acc, replyMsg);
    }

    private void updateAlipayPay(Handler replyMsg, JsonArray params) {
        JsonObject acc = params.getJsonObject(0);
        accountSrv.updateZfbPay(acc, replyMsg);
    }
}
