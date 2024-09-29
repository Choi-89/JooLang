package com.project.FreeCycle.Controller;

//import org.springframework.http.ResponseEntity;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.UserDTO;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.LocationService;
import com.project.FreeCycle.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
//import org.springframework.web.bind.annotation.RestController;

//import java.util.HashMap;
//import java.util.Map;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

    private final UserService userService;
    private final LocationService locationService;


    // 로그인 전 홈 화면
    @Operation(summary = "로그인 전 홈 화면", description = "로그인되지 않은 상태에서의 홈 화면")
    @GetMapping
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("{\"message\": \"로그인 전 홈 화면\"}");
//        return "home";
    }

    // 로그인 후 홈 화면 정보 반환
    @Operation(summary = "로그인 후 홈 화면 정보", description = "로그인한 사용자를 위한 홈 화면 정보를 반환합니다.")
    @GetMapping("/home_user")
    public ResponseEntity<String> homeUser() {
        return ResponseEntity.ok("{\"message\": \"로그인 후 홈 화면 정보\"}");
//        return "home_user";
    }

    // 계정 삭제
    @Operation(summary = "계정 삭제", description = "로그인된 사용자의 계정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 오류 또는 삭제 실패")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@Parameter(description = "비밀번호", required = true) @RequestParam("password") String password
            ,Principal principal){
        String userId = principal.getName();// 현재 로그인 되어있는 이름 가져옴
        UserDTO userDTO = userService.getUser(userId);
        long user_id = userDTO.getId();

        if(userService.checkPassword(userId, password)){
            if(locationService.deleteLocation(user_id) && userService.deleteUser(userId)){
//                return "redirect:/";
                return ResponseEntity.ok("계정이 성공적으로 삭제되었습니다.");
            } else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("계정 삭제에 실패했습니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 틀렸습니다.");

        }
    }

}
