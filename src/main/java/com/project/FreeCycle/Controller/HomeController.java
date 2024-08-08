package com.project.FreeCycle.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 로그인 전 홈 화면
    @GetMapping("/")
    public String Home(){
        return "home";
    }

    // 로그인 후 홈 화면
    @GetMapping("/home_user")
    public String HomeUser(){
        return "home_user";
    }


}
