package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import com.project.FreeCycle.Service.JoinService;
import com.project.FreeCycle.Service.LocationService;
import com.project.FreeCycle.Service.UserService;
import com.project.FreeCycle.Service.VerifyService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@Controller
public class JoinController {

    private final LocationService locationService;
    private final JoinService joinService;
    private final VerifyService verifyService;

    public JoinController(LocationService locationService, JoinService joinService, VerifyService verifyService) {
        this.locationService = locationService;
        this.joinService = joinService;
        this.verifyService = verifyService;
    }

    @GetMapping("/home/join")
    public String ShowJoin(){
        return "join";
    }


    @PostMapping("/joinProc")
    public String JoinProc(@RequestParam("userName") String name, @RequestParam("nickname") String nickname,
                           @RequestParam("password") String password, @RequestParam("userId") String userId,
                           @RequestParam("postcode") String postcode, @RequestParam("address") String address,
                           @RequestParam(name = "email") String email,@RequestParam("detail_address") String detailAddress,
                           Model model){

        try{
            User user = new User();
            user.setName(name);
            user.setNickname(nickname);
            user.setUserId(userId);
            user.setPassword(password);
            user.setEmail(email);

            User savedUser = joinService.UserSave(user);

            Location location = new Location();
            location.setAddress(address);
            location.setDetailAddress(detailAddress);
            location.setPostcode(postcode);
            location.setUser(savedUser);
            locationService.LocationSave(location);


//            return ResponseEntity.ok().body("User registered successfully");
            return "redirect:/";
        } catch (IllegalArgumentException e){
            model.addAttribute("errorMsg", e.getMessage());
//            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
            return "join";
        } catch (Exception e){
            model.addAttribute("errorMsg", "회원가입 중 오류가 발생하였습니다.");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred during registration");
            return "join";
        }
    }

    // OAuth2 방식으로 처음 로그인 시도 할 시 비밀번호 업데이트 함.
    @GetMapping("/joinPassword")
    public String joinPassword(){
        return "joinPassword";
    }

    @PostMapping("/joinPasswordProc")
    public String joinPasswordProc(@RequestParam(name = "newPassword") String password,
                                    @RequestParam(name = "confirmPassword") String passwordConfirm
                                    ,HttpSession session, Model model){
        String userId = (String) session.getAttribute("userId");
        if(userId == null) {
            log.error("세션에 userId가 없습니다.");
            return "joinPassword";
        }

        log.info("비밀번호 설정 요청 : userId = {}, newPassword = {}, confirmPassword = {}", userId, password, passwordConfirm);
        if(verifyService.checkPassword(password,passwordConfirm)){
            log.info("비밀번호가 일치합니다.");
            if(verifyService.updatePassword(password, userId)) {
                log.info("비밀번호가 성공적으로 설정되었습니다: userId={}", userId);
                session.removeAttribute("userId");
                return "redirect:/";
            } else{
                log.error("비밀번호 설정 중 오류가 발생하였습니다. userId = {}", userId);
                model.addAttribute("errorMsg","비밀번호가 업데이트 중 오류가 발생했습니다.");
            }
        } else{
            log.error("비밀번호가 일치하지 않습니다.");
            model.addAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
        }
        return "joinPassword";
    }

}
