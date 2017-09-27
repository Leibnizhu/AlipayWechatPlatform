package com.turingdi.awp.controller.backstage;

import com.turingdi.awp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 入口Index
 *
 * Created by leibniz on 2017-05-16.
 */
@Controller
@RequestMapping("bs")
public class IndexController extends BackstageBaseController{
    @Autowired
    private AccountService wxAccServ;

    @RequestMapping("/")
    public String index() {
        return "wxaccount/edit";
    }
}
