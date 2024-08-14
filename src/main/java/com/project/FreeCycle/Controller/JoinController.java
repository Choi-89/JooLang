package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import com.project.FreeCycle.Service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class JoinController {

//    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/home/join")
    public String ShowJoin(){
        return "join";
    }

    @PostMapping("/joinProc")
    public String JoinProc(@RequestParam("userName") String name, @RequestParam("nickname") String nickname,
                                           @RequestParam("password") String password, @RequestParam("user_id") String userId,
                                           @RequestParam("postcode") String postcode, @RequestParam("address") String address,
                                           @RequestParam("detail_address") String detailAddress, Model model
                                           ){

        User user = new User();
        user.setName(name);
        user.setNickname(nickname);
        user.setPassword(bCryptPasswordEncoder.encode(password));  // 비밀번호 인코딩
        user.setUserId(userId);
        user.setRole("ROLE_USER"); // user 역할로 저장
        user.setAddress(address);
        user.setDetailAddress(detailAddress);
        user.setPostcode(postcode);

        try{
            userService.save(user);
            model.addAttribute("userName", name);
            model.addAttribute("nickname", nickname);
            model.addAttribute("password", password);
            model.addAttribute("user_id", userId);
            model.addAttribute("postcode", postcode);
            model.addAttribute("address", address);
            model.addAttribute("detail_address", detailAddress);

//            return ResponseEntity.ok().body("User registered successfully");
            return "redirect:/";
        } catch (IllegalArgumentException e){
            model.addAttribute("errorMessage", e.getMessage());
//            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
            return "join";
        } catch (Exception e){
            model.addAttribute("errorMessage", "회원가입 중 오류가 발생하였습니다.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred during registration");
            return "join";
        }
    }


}
