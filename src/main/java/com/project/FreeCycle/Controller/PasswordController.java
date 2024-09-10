package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {
    private final VerifyService verifyService;

    @Autowired
    public PasswordController(VerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/certifyUser")
    public String ShowFindPassword(){
        return "certifyUser";
    }

    @PostMapping("/certifyUserProc")
    public String certifyUser(@RequestParam(name = "userId") String userId,
                              @RequestParam(name = "email") String email,
                              Model model){
        
        /* 입력 받은 사용자 Id와 email이 데이터베이스에 있는지 확인하는
        * 로직 구현 */

        if(verifyService.existUser(userId, email)){
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            return "verifyCode";
        } else{
            model.addAttribute("errorMsg", "해당하는 아이디 혹은 이메일이 없습니다.");
            return "redirect:/certifyUser";
        }

    }

    @GetMapping("/verifyCode")
    public String ShowVerifyCode(){
        return "verifyCode";
    }


    @PostMapping("/sendCodeProc")
    public String VerifyCodeProc(@RequestParam(name = "email") String email){
        /* 인증번호 발생 후 메일 제출 코드 */

        if(verifyService.sendEmail(email)){
            return "verifyCode";
        } else{
            throw new IllegalArgumentException("인증번호 발생 혹은 메일 발송 중 에러 발생했습니다.");
        }

    }

    @PostMapping("/verifyCodeProc")
    public String SendCodeProc(@RequestParam(name = "verifyCode") String code, Model model){

        /* 인증 번호 비교 로직 */
        if(verifyService.verifyCode(code)){
            return "editPassword";
        }
        model.addAttribute("errorMsg","인증번호가 일치하지 않습니다.");
        return "verifyCode";
    }

    @GetMapping("/editPassword")
    public String ShowEditPassword(){
        return "editPassword";
    }

    @PostMapping("/updatePasswordProc")
    public String EditPasswordProc(@RequestParam(name = "newPassword") String password,
                                   @RequestParam(name = "confirmPassword") String passwordConfirm,
                                   @RequestParam(name = "userId") String userId) {

        /* 비밀번호 업데이트 로직 구현 */

        if (verifyService.checkPassword(password, passwordConfirm)) {
            if (verifyService.updatePassword(password, userId)) {
                return "login";
            }
        }
        return "editPassword";
    }
}
