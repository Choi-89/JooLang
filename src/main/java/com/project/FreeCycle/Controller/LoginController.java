package com.project.FreeCycle.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {


    @GetMapping("/home/login")
    public String ShowLogin(@RequestParam(value = "error", required = false) String error, Model model){
        if (error != null) {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 다릅니다.");
        }
        return "login";
    }

}

