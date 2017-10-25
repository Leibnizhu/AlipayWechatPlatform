package com.turingdi.awp.verticle;

import com.turingdi.awp.router.EventBusNamespace;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;

import static com.turingdi.awp.router.EventBusNamespace.*;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-25 14:46.
 */
public abstract class AbstractDatabaseAccessVerticle extends AbstractVerticle {
    private String listenAddress;

    AbstractDatabaseAccessVerticle(EventBusNamespace address){
        this.listenAddress = address.get();
    }
    @Override
    public void start() throws Exception {
        super.start();

        vertx.eventBus().<JsonObject>consumer(listenAddress).handler(msg -> {
            JsonObject msgBody = msg.body();
            String methodStr = msgBody.getString(METHOD);
            EventBusNamespace method = null;
            try {
                method = EventBusNamespace.valueOf(methodStr);
            } catch (IllegalArgumentException e) {
                log().error("错误的请求方法名{}", methodStr);
            }
            log().debug("接收到消息({}),消息内容：{}", msg.address(), msgBody);
            processMethods(msg, msgBody.getJsonArray(PARAMS), method);
        });
    }

    protected abstract void processMethods(Message<JsonObject> msg, JsonArray params, EventBusNamespace method);

    protected abstract Logger log();
}
