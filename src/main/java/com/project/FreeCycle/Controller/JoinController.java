package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.Location;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import com.project.FreeCycle.Service.JoinService;
import com.project.FreeCycle.Service.LocationService;
import com.project.FreeCycle.Service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class JoinController {

    private final LocationService locationService;

    private final JoinService joinService;

    public JoinController(LocationService locationService, JoinService joinService) {
        this.locationService = locationService;
        this.joinService = joinService;
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


}
