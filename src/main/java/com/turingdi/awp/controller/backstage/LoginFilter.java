package com.turingdi.awp.controller.backstage;

import com.turingdi.awp.entity.Account;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.turingdi.awp.controller.backstage.BackstageBaseController.SESSION_ENTERPRISE_USER_KEY;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 13:03.
 */
@Order(1)
//重点
@WebFilter(filterName = "LoginFilter", urlPatterns = "/bs/*")
@Component
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String uri = req.getRequestURI().substring(req.getContextPath().length());
            if(uri.startsWith("/bs") && notContainStrings(uri, AUTH_EXCLUDED_URL)) {
                HttpSession session = req.getSession();
                Account user = (Account) session.getAttribute(SESSION_ENTERPRISE_USER_KEY);
                if(user == null) {
                    if(response instanceof HttpServletResponse) {
                        ((HttpServletResponse) response).sendRedirect(req.getContextPath() + "/bs/user/showLogin");
                        return;
                    }
                }
            }
        }

        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }

    /**
     * 访问时不需要公众号授权的uri
     * 包括:获取微信用户信息(公众号授权时的回调地址)
     */
    private static final String[] AUTH_EXCLUDED_URL = new String[]{"bs/user/", "/oauth/getOpenId"};

    /**
     * 判断target是否不包含rule数组中任意一字符串
     *
     * @param target 待判断的字符串
     * @param rule   判断的规则(是否这些字符串之一)
     * @return target是否不包含rule数组中任意一字符串
     */
    private boolean notContainStrings(String target, String[] rule) {
        boolean result = true;
        for (String s : rule) {
            if (target.contains(s)) {
                result = false;
                break;
            }
        }
        return result;
    }
}
