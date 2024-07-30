package com.project.FreeCycle.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 메인 홈 컨트롤러
@Controller
public class HomeController {

    @GetMapping("/")
    private String homeView(){
        return "home";
    }

}
