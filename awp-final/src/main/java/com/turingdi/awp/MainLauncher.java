package com.turingdi.awp;

import io.netty.channel.DefaultChannelId;
import io.vertx.core.Launcher;

/**
 * 主入口，继承Launcher，配置日志
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-11 20:18.
 */
public class MainLauncher extends Launcher{
    public static void main(String[] args) {
        //Force to use slf4j
        DefaultChannelId.newInstance();
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");
        String[] finalArgs = new String[3 + args.length];
        System.arraycopy(new String[]{"run", "com.turingdi.awp.verticle.MainVerticle", "-conf"}, 0, finalArgs, 0, 3);
        System.arraycopy(args, 0, finalArgs, 3, args.length);
        new Launcher().dispatch(finalArgs);
    }
}
