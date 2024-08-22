package com.project.FreeCycle.Controller;


import com.project.FreeCycle.Api.NaverApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;


@Slf4j
//@RestController
//@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class LoginController {

    @Autowired
    NaverApi naverApi;

    @GetMapping("/home/login")
    public String showLogin(@RequestParam(value = "error", required = false) String error,
                            Model model) {

//        Map<String, String> response = new HashMap<>();
        if (error != null) {
            model.addAttribute("errorMsg", "아이디 혹은 비밀번호가 다릅니다.");
//            response.put("errorMsg", "아이디 또는 비밀번호가 다릅니다.");
//            return ResponseEntity.badRequest().body(response.toString());  // 400 Bad Request와 함께 오류 메시지 반환
        }

//        response.put("message", "Login page accessed successfully");
//        return ResponseEntity.ok(response.toString());  // 200 OK와 함께 기본 메시지 반환
        return "login";
    }


}

