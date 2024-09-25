package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.LocationDTO;
import com.project.FreeCycle.Dto.UserDTO;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import com.project.FreeCycle.Service.LocationService;
import com.project.FreeCycle.Service.UserService;
import com.project.FreeCycle.Service.VerifyService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@Controller
public class JoinController {

    private final LocationService locationService;
    private final VerifyService verifyService;
    private final UserService userService;

    public JoinController(LocationService locationService, VerifyService verifyService, UserService userService) {
        this.locationService = locationService;
        this.verifyService = verifyService;
        this.userService = userService;
    }

    @GetMapping("/home/joinList")
    public String joinList(){
        return "joinList";
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
                           @RequestParam("phoneNum") String phoneNum,
                           Model model){

        try{
            log.info("회원가입 요청: userId={}, email={}, name={}, phoneNum={}", userId, email, name, phoneNum);

            UserDTO userDTO = new UserDTO(userId,name,nickname,email, null,null,null,phoneNum,0);
            log.info("UserDTO 생성 완료: {}", userDTO);

            userDTO.setPassword(password);
            log.info("비밀번호 설정 완료: {}", password);

            User savedUser = userService.saveUser(userDTO);
            log.info("User 저장 완료: {}", savedUser);

            LocationDTO locationDTO = new LocationDTO(address,postcode,detailAddress);
            locationService.LocationSave(locationDTO, savedUser);
            log.info("Location 정보 저장 완료");

//            return ResponseEntity.ok().body("User registered successfully");
            return "redirect:/";
        } catch (IllegalArgumentException e){
            log.error("회원가입 오류: {}", e.getMessage());
            model.addAttribute("errorMsg", e.getMessage());
//            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
            return "join";
        } catch (Exception e){
            log.error("회원가입 중 알 수 없는 오류 발생", e);

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
