package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/{id}/mypage")
    public String myPage(Principal principal) {
        //User user = principal.getName();
        //String username = principal.getName();
//        model.addAttribute("user", user);
        //구글에서 본거 그냥 써보는 중.. 하하하하하하하하하하하하핳하하하하하하하하ㅏㅎ하하하하하하하하하하하하하ㅏㅎ하하ㅏ핳ㅎㅎㅎㅎㅎㅎ하하ㅏㅏ하하ㅏ하ㅏㅎ
        return "mypage";

    }
//    @PostMapping(value = "/{id}/mypage")

}
