package com.turingdi.awp.router;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * 提供refuseNonLanAccess()方法，用于拒绝非局域网的访问
 * @author Leibniz.Hu
 * Created on 2017-10-24 16:16.
 */
public abstract class LanAccessSubRouter implements SubRouter {
    private String[] lanSegments = {"10.0.", "192.168.", "172.16."};

    protected boolean refuseNonLanAccess(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        String realIp = req.getHeader("X-Real-IP");
        String xforward = req.getHeader("X-Forwarded-For");
        //禁止外网访问
        if (realIp != null && !isLanIP(realIp)) {
            resp.setStatusCode(403).end();
            return true;
        }
        if (xforward != null && !isLanIP(xforward)) {
            resp.setStatusCode(403).end();
            return true;
        }
        return false;
    }

    private boolean isLanIP(String ip){
        boolean isLanIP = false;
        for(String segment : lanSegments){
            if(ip.startsWith(segment)){
                isLanIP = true;
                break;
            }
        }
        return isLanIP;
    }
}
