package com.ctrip.framework.apollo.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @RequestMapping("/")
    public Object index(){
        System.out.println("走controller");
        return new ModelAndView("static/index.html");
    }
}
