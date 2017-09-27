package com.turingdi.awp.controller.backstage;

import com.turingdi.awp.entity.Account;
import com.turingdi.awp.service.AccountService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 该类是用户管理的控制类，负责用户管理的业务逻辑
 *
 * Create by quandong
 */
@Controller
@RequestMapping("bs/user")
public class UserManagerController extends BackstageBaseController {
    /**
     * 存储角色名和（数据库中存储的）角色int数值的对应关系
     */
    private final static String[] ROLE_NAME = {"超级用户", "主帐号", "子帐号"};
    /**
     * Log4j日志对象
     */
    private final static Logger LOG = Logger.getLogger(UserManagerController.class);

    @Autowired
    private AccountService wxAccServ;

    /**
     * @author lws
     * @date 2016-12-05
     * @introduce 显示登录页面
     */
    @RequestMapping("showLogin")
    public String showLogin(HttpSession session) throws IOException {
        Account curUser = getUser(session);
        ModelAndView view = new ModelAndView();
        //登录成功后再次请求该路径则跳转到
        if (curUser == null) {
            return "login";
        } else {
            return "redirect:../camAnalysis/show";
        }

    }


    /**
     * @author lws
     * @date 2016-12-05
     * @introduce 登录验证
     */
    @RequestMapping("login")
    @ResponseBody
    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        return "";
    }

    /**
     * @return "退出登录成功"
     *
     * @author lws
     * @date 2016-12-05
     * @introduce 退出登录
     */
    @RequestMapping("logout")
    public String logout() {
        return "";
    }

}