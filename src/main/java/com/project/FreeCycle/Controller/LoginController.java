package com.project.FreeCycle.Controller;


import com.project.FreeCycle.Api.KakaoApi;
import com.project.FreeCycle.Api.NaverApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
public class LoginController {

    @Autowired
    NaverApi naverApi;

    @Autowired
    KakaoApi kakaoApi;

    private static final String FIXED_STATE = "3s0asdt0";

    @GetMapping("/home/login")
    public String ShowLogin(@RequestParam(value = "error", required = false) String error, Model model){


        String state = FIXED_STATE; // 상태 문자열은 보안 목적으로 사용됨

        model.addAttribute("naverClientId", naverApi.getNaverClientId());
        model.addAttribute("naverRedirectUri", naverApi.getNaverRedirectUri());
        model.addAttribute("KakaoClientId", kakaoApi.getKakaoClientId());
        model.addAttribute("KakaoRedirectUri", kakaoApi.getKakaoRedirectUri());
        model.addAttribute("state", state);

        if (error != null) {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 다릅니다.");
        }

        return "login";
    }

}

