package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Service.VerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class SMSController {

    @Autowired
    private VerifyService verifyService;

    @GetMapping("/home/verifyPhone")
    public String VerfiyPhone(){
        return "verifyPhone";
    }

    @PostMapping("/sendSmsProc")
    public String sendSMSProc(@RequestParam String phoneNumber, Model model){
        log.info("휴대폰 번호로 인증 번호 전송 시도: {}", phoneNumber);

        if(verifyService.sendSMS(phoneNumber)){
            log.info("인증 번호 전송 성공");
            return "verifyPhone";
        }

        log.error("인증 번호 전송 실패");
        model.addAttribute("errorMsg", "인증번호 전송에 오류가 발생했습니다.");
        return "verifyPhone";
    }

    @PostMapping("/checkProc")
    public String checkProc(@RequestParam String verifyCode, Model model){
        log.info("인증 코드 확인 시도: {}", verifyCode);
        if(verifyService.verifyCode(verifyCode)){
            log.info("인증 성공");
            return "join";
        }

        log.error("인증 실패: 잘못된 인증 번호");
        model.addAttribute("errorMsg", "인증번호가 틀립니다.");
        return "verifyPhone";
    }

}
