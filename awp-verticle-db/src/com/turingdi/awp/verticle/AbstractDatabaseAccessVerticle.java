package com.turingdi.awp.verticle;

import com.turingdi.awp.router.EventBusNamespace;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;

import static com.turingdi.awp.router.EventBusNamespace.METHOD;
import static com.turingdi.awp.router.EventBusNamespace.PARAMS;

/**
 * 数据库访问服务Verticle的抽象类
 * 继承AbstractVerticle， 覆盖start()方法，统一了从监听EventBus并分发请求任务的逻辑
 *
 * @author Leibniz.Hu
 * Created on 2017-10-25 14:46.
 */
public abstract class AbstractDatabaseAccessVerticle extends AbstractVerticle {
    private String listenAddress;

    /**
     * 子类构造器需要调用，指定监听的EventBus地址
     *
     * @param address 监听的地址
     * @author Leibniz.Hu
     */
    AbstractDatabaseAccessVerticle(EventBusNamespace address) {
        this.listenAddress = address.get();
    }

    /**
     * 根据EventBusNamespace统一规定的规则，解析EventBus的消息
     * 然后子类的processMethods()方法去分发请求到具体方法
     *
     * @author Leibniz.Hu
     */
    @Override
    public void start() throws Exception {
        super.start();
        vertx.eventBus().<JsonObject>consumer(listenAddress).handler(msg -> {
            JsonObject msgBody = msg.body();

            //尝试解析请求的方法名
            String methodStr = msgBody.getString(METHOD);
            EventBusNamespace method = null;
            try {
                method = EventBusNamespace.valueOf(methodStr);
            } catch (IllegalArgumentException e) {
                log().error("错误的请求方法名{}", methodStr);
                msg.fail(404, e.getMessage() + ".错误的请求方法名" + methodStr);
            }
            log().debug("接收到消息({}),消息内容：{}", msg.address(), msgBody);

            //调用子类具体方法去分发处理请求
            try {
                processMethods(msg::reply, msgBody.getJsonArray(PARAMS), method);
            } catch (Exception e) {
                msg.fail(500, e.getMessage());
            }
        });
    }

    /**
     * 子类需要实现的分发处理请求的方法
     *
     * @param replyMsg 回复EventBus的Handler
     * @param params   请求的参数
     * @param method   请求的方法
     * @param <T>      EventBus消息类型
     * @author Leibniz.Hu
     */
    protected abstract <T> void processMethods(Handler<T> replyMsg, JsonArray params, EventBusNamespace method);

    /**
     * 子类需要实现的，获取子类Logger的方法
     * @return 子类的Logger
     */
    protected abstract Logger log();
}
