package com.project.FreeCycle.Controller;

//import org.springframework.http.ResponseEntity;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.LocationService;
import com.project.FreeCycle.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
//import org.springframework.web.bind.annotation.RestController;

//import java.util.HashMap;
//import java.util.Map;

//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class HomeController {



    private final UserService userService;
    private final LocationService locationService;
    private final UserRepository userRepository;

    public HomeController(UserService userService, LocationService locationService, UserRepository userRepository) {
        this.userService = userService;
        this.locationService = locationService;
        this.userRepository = userRepository;
    }

    // 로그인 전 홈 화면
    @GetMapping("/")
    public String home() {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Welcome to the Home page!");
//        return ResponseEntity.ok(response); // 200 OK와 함께 메시지 반환
        return "home";
    }

    // 로그인 후 홈 화면 정보 반환
    @GetMapping("/home_user")
    public String homeUser() {
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Welcome to the User Home page!");
//        return ResponseEntity.ok(response); // 200 OK와 함께 메시지 반환
        return "home_user";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("password") String password, Model model, Principal principal){
        String userId = principal.getName();// 현재 로그인 되어있는 이름 가져옴
//        System.out.println("까꿍~~~~: "+userId);
        User user = userRepository.findByUserId(userId);
        long user_id = user.getId();

        if(userService.checkPassword(userId, password)){
            if(locationService.deleteLocation(user_id) && userService.deleteUser(userId)){
                return "redirect:/logout";
            } else{
                model.addAttribute("errormsg", "계정 삭제에 실패했습니다.");
                return "redirect:/home_user";
            }
        } else {
            model.addAttribute("errormsg","비밀번호가 틀렸습니다.");
            return "redirect:/home_user";
        }
    }

}
