package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JoinController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, BCryptPasswordEncoder bCryptPasswordEncoder1) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder1;
    }

    @GetMapping("/home/join")
    public String ShowJoin(){
        return "join";
    }

    @PostMapping("/joinProc")
    public String JoinProc(@RequestParam("userName") String name, @RequestParam("nickname") String nickname,
                           @RequestParam("password") String password, @RequestParam("user_id") String userId,
                           Model model){

        User user = new User();
        user.setName(name);
        user.setNickname(nickname);
        user.setPassword(bCryptPasswordEncoder.encode(password));  // 비밀번호 인코딩
        user.setUserId(userId);
        user.setRole("ROLE_USER"); // user 역할로 저장

        try{
            userRepository.save(user);
            model.addAttribute("userName", name);
            model.addAttribute("nickname", nickname);
            model.addAttribute("password", password);
            model.addAttribute("user_id", userId);

            return "redirect:/";

        } catch (IllegalArgumentException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "join";
        } catch (Exception e){
            model.addAttribute("errorMessage", "회원가입 중 오류가 발생하였습니다.");
            return "join";
        }
    }


}
