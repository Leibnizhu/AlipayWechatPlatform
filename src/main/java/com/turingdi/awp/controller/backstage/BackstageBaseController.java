package com.turingdi.awp.controller.backstage;

import com.turingdi.awp.entity.Account;

import javax.servlet.http.HttpSession;

/**
 * @author Leibniz.Hu
 * Created on 2017-09-27 13:05.
 */
public class BackstageBaseController {
    public static final String SESSION_ENTERPRISE_USER_KEY = "EnterpriseUser";
    protected Account getUser(HttpSession session){
        return (Account) session.getAttribute(SESSION_ENTERPRISE_USER_KEY);
    }
}
