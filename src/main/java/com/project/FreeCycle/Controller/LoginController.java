package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final LoginService loginService;
    private final UserRepository userRepository;

    public LoginController(LoginService loginService, UserRepository userRepository) {
        this.loginService = loginService;
        this.userRepository = userRepository;
    }

    @GetMapping("/home/login")
    public String ShowLogin(@RequestParam(value = "error", required = false) String error, Model model){

        if (error != null) {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 다릅니다.");
        }
        return "login";
    }


    @PostMapping("/loginProc")
    public String LoginProc(@RequestParam("userId") String userId, @RequestParam("password") String password,
                            HttpSession session){

        if(loginService.login(userId, password)){
            session.setAttribute("userId", userId);
            return "home_user";
        }

        return "redirect:/home/login?error=true";
    }

    @GetMapping("/logout")
    public String Logout(HttpSession session){
        session.removeAttribute("userId");

        return "login";
    }

}

