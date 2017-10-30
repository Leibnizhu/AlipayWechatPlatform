package com.turingdi.awp.router;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供refuseNonLanAccess()方法，用于拒绝非局域网的访问
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-24 16:16.
 */
public abstract class LanAccessSubRouter implements SubRouter {
    private Logger log = LoggerFactory.getLogger(getClass());
    /**
     * 来源IP网段白名单
     * 目前放的是内网地址
     */
    private String[] lanSegments = { "10.0.", "192.168.", "172.16." };

    /**
     * 判断当前请求是否允许，如果不允许，则将状态码设为403并结束响应
     * 
     * @return true:禁止访问 false=允许访问
     * @author Leibniz.Hu
     */
    protected boolean refuseNonLanAccess(RoutingContext rc) {
        HttpServerRequest req = rc.request();
        HttpServerResponse resp = rc.response();
        String realIp = req.getHeader("X-Real-IP");
        String xforward = req.getHeader("X-Forwarded-For");
        //禁止外网访问
        if (realIp != null && !isLanIP(realIp)) {
            log.warn("检测到非法访问，来自X-Real-IP={}", realIp);
            resp.setStatusCode(403).end();
            return true;
        }
        if (xforward != null && !isLanIP(xforward)) {
            log.warn("检测到非法访问，来自X-Forwarded-For={}", xforward);
            resp.setStatusCode(403).end();
            return true;
        }
        return false;
    }

    /**
     * 判断ip参数是否满足lanSegments字段的要求
     * @author Leibniz.Hu
     */
    private boolean isLanIP(String ip) {
        boolean isLanIP = false;
        for (String segment : lanSegments) {
            if (ip.startsWith(segment)) {
                isLanIP = true;
                break;
            }
        }
        return isLanIP;
    }
}
