package com.project.FreeCycle.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/mypage")
    private String Mypage(){

        return "mypage";
    }

}
