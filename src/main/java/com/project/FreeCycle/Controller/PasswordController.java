package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Service.VerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
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

        log.info("certifyUserProc 호출 - userId: {}", userId);
        /* 입력 받은 사용자 Id와 email이 데이터베이스에 있는지 확인하는
        * 로직 구현 */
        if(verifyService.existUser(userId, email)){
            log.info("유저 아이디 가져왔는지 확인 : userId={}", userId);
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            log.info("모델에 추가된 데이터: userId={}, email={}", model.getAttribute("userId"), model.getAttribute("email"));
            return "verifyCode";
        } else{
            model.addAttribute("errorMsg", "해당하는 아이디 혹은 이메일이 없습니다.");
            return "redirect:/certifyUser";
        }

    }

    @GetMapping("/verifyCode")
    public String ShowVerifyCode(@ModelAttribute(name = "userId") String userId,
                                 @ModelAttribute(name = "email") String email,
                                 Model model){
        model.addAttribute("userId", userId);
        model.addAttribute("email", email);
        return "verifyCode";
    }


    @PostMapping("/sendCodeProc")
    public String VerifyCodeProc(@ModelAttribute(name = "email") String email,
                                 @ModelAttribute(name = "userId") String userId, Model model){
        log.info("전송된 userId: {}", userId);

        /* 인증번호 발생 후 메일 제출 코드 */
        if(verifyService.sendEmail(email)){
            model.addAttribute("userId", userId);
            log.info("유저 아이디 가져왔는지 확인 : userId={}", userId);
            log.info("모델에 추가된 데이터: userId={}", model.getAttribute("userId"));

            return "verifyCode";
        } else{
            throw new IllegalArgumentException("인증번호 발생 혹은 메일 발송 중 에러 발생했습니다.");
        }

    }

    @PostMapping("/verifyCodeProc")
    public String SendCodeProc(@RequestParam(name = "verifyCode") String code,
                               @RequestParam(name = "userId") String userId, Model model){

        /* 인증 번호 비교 로직 */
        if(verifyService.verifyCode(code)){
            log.info("유저 아이디 가져왔는지 확인 : userId={}", userId);

            model.addAttribute("userId", userId);
            return "editPassword";
        }
        model.addAttribute("errorMsg","인증번호가 일치하지 않습니다.");
        model.addAttribute("userId", userId);
        return "verifyCode";
    }

    @GetMapping("/editPassword")
    public String ShowEditPassword(@ModelAttribute(name = "userId") String userId,
                                   Model model){
        model.addAttribute("userId", userId);
        log.info("모델에 추가된 데이터: userId={}", model.getAttribute("userId"));

        return "editPassword";
    }

    @PostMapping("/updatePasswordProc")
    public String EditPasswordProc(@RequestParam(name =  "newPassword") String password,
                                   @RequestParam(name = "confirmPassword") String passwordConfirm,
                                   @RequestParam (name = "userId") String userId, Model model) {

        log.info("모델에 추가된 데이터: userId={}", model.getAttribute("userId"));

        /* 비밀번호 업데이트 로직 구현 */
        log.info("비밀번호 수정 요청: userId={}, newPassword={}, confirmPassword={}", userId, password, passwordConfirm);

        if (verifyService.checkPassword(password, passwordConfirm)) {
            log.info("비밀번호가 일치합니다.");
            if (verifyService.updatePassword(password, userId)) {
                log.info("비밀번호가 성공적으로 업데이트되었습니다: userId={}", userId);
                return "redirect:/";
            } else{
                log.error("비밀번호 업데이트 중 오류 발생: userId={}", userId);
                model.addAttribute("errorMsg","새 비밀번호가 현재 비밀번호와 동일합니다");
            }
        } else{
            log.error("비밀번호가 일치하지 않습니다: newPassword={}, confirmPassword={}", password, passwordConfirm);
            model.addAttribute("errorMsg","비밀번호가 일치하지 않습니다.");
        }

        model.addAttribute("userId", userId);  // 다시 비밀번호 설정 위해 model에 userId 다시 추가
        return "editPassword";
    }
}
