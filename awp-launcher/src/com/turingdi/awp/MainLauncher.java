package com.turingdi.awp;

import io.vertx.core.Launcher;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-11 20:18.
 */
public class MainLauncher extends Launcher{
    public static void main(String[] args) {
        //Force to use slf4j
        System.setProperty("vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");
        new Launcher().dispatch(args);
    }
}
