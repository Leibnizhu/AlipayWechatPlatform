package com.tuiringdi.awp.test;

import com.turingdi.awp.util.common.NetworkUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-12 21:09.
 */
public class NetworkUtilTest {
    @Test
    public void asyncPostTest() throws IOException {
        Vertx vertx = Vertx.vertx();
        System.out.println("===================Test start===================");
        NetworkUtils.asyncPostString(vertx, "http://breo.turing.asia/gzwx/smartBox/poll", System.out::println);
    }

    @Test
    public void HttpClientTest() {
        Vertx vertx = Vertx.vertx();
        System.out.println("===================Test start===================");
        HttpClient client = vertx.createHttpClient();
        client.get(8080, "localhost", "/api/prod/dfsad", response -> {
            System.out.println("Received response with status code " + response.statusCode());
        }).end();
    }


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        System.out.println("===================Test start===================");
        vertx.deployVerticle(new AbstractVerticle() {
            @Override
            public void start() throws Exception {
                NetworkUtils.asyncPostString(vertx, "http://breo.turing.asia/gzwx/smartBox/poll", System.out::println);
            }
        });

    }

}
