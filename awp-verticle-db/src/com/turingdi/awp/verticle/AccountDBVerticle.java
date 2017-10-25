package com.turingdi.awp.verticle;

import com.turingdi.awp.db.AccountDao;
import com.turingdi.awp.router.EventBusNamespace;
import com.turingdi.awp.service.AccountService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-24 22:14.
 */
public class AccountDBVerticle extends AbstractVerticle {
    private Logger log = LoggerFactory.getLogger(getClass());
    private AccountDao accDao;
    private AccountService accountSrv;

    @Override
    public void start() throws Exception {
        super.start();

        if(accDao == null){
            accDao = new AccountDao();
        }
        accountSrv = new AccountService(accDao);
        vertx.eventBus().<JsonObject>consumer(ADDR_ACCOUNT_DB.get()).handler(msg -> {
            JsonObject msgBody = msg.body();
            String methodStr = msgBody.getString(METHOD);
            EventBusNamespace method = null;
            try{
                method = EventBusNamespace.valueOf(methodStr);
            } catch(IllegalArgumentException e){
                log.error("错误的请求方法名{}", methodStr);
            }
            log.debug("接收到消息({}),消息内容：{}", msg.address(), msgBody);
            processMethods(msg, msgBody.getJsonArray(PARAMS), method);
        });
    }

    private void processMethods(Message<JsonObject> msg, JsonArray params, EventBusNamespace method) {
        switch (method) {
            case COMMAND_GET_ACCOUNT_BY_ID:
                int eid = params.getInteger(0);
                accountSrv.getById(eid, acc -> {
                    msg.reply(acc);
                });
                break;
            default:
                log.error("未能处理的请求方法：{}", method);
        }
    }
}
