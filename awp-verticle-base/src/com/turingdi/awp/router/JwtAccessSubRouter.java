package com.turingdi.awp.router;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 给需要JWT授权的oSubRuter提供forbidAccess进行权限控制
 * 详见forbidAccess()方法的注释
 * 
 * @author Leibniz.Hu
 * Created on 2017-10-27 17:38.
 */
public abstract class JwtAccessSubRouter implements SubRouter{
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 判断当前请求i是否满足权限需求
     * 详见checkId参数
     * 
     * @param idKey 请求中的企业用户id参数名
     * @param checkId true=需要管理员权限，或者非管理员但ID相等才允许访问；false=只有管理员允许访问
     * @return true=禁止访问 false=允许访问
     */
    protected boolean forbidAccess(RoutingContext rc, String idKey, boolean checkId) {
        JsonObject jwtJson = rc.user().principal();
        int role = jwtJson.getInteger("role");
        if (checkId) {
            int queryId = Integer.parseInt(rc.request().getParam(idKey));
            int jwtId = jwtJson.getInteger("id");
            if (role != 0 && queryId != jwtId) {
                log.warn("用户(ID={})尝试对ID={}的用户进行操作，不满足权限要求，已被拦截", jwtId, queryId);
                rc.response().setStatusCode(403).end();
                return true;
            }
        } else {
            if ((role != 0)) {
                log.warn("用户(ID={})尝试进行受保护的操作，不满足权限要求，已被拦截", jwtJson.getInteger("id"));
                rc.response().setStatusCode(403).end();
                return true;
            }
        }
        return false;
    }
}
