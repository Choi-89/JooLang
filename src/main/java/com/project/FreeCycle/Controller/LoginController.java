package com.project.FreeCycle.Controller;


import com.project.FreeCycle.Api.NaverApi;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.UserService;
import com.project.FreeCycle.Util.PasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/home")
public class LoginController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public LoginController(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 페이지 반환 성공"),
            @ApiResponse(responseCode = "400", description = "로그인 오류 발생")
    })
    @Operation(summary = "로그인 페이지", description = "로그인 페이지를 반환합니다. 로그인 오류가 있을 경우 오류 메시지를 포함합니다.")
    @GetMapping("/login")
    public ResponseEntity<String> showLogin(@Parameter(description = "로그인 오류 메시지", required = false) @RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            log.error("로그인 오류 발생: {}", error);
            return ResponseEntity.badRequest().body("{\"message\": \"아이디 혹은 비밀번호가 다릅니다.\"}");
        }

        log.info("로그인 페이지 반환");
        return ResponseEntity.ok("{\"message\": \"로그인 페이지 요청 성공\"}");
    }


    @Operation(summary = "로그인 API", description = "사용자 ID와 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "로그인 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String userId, @RequestParam String password,
                                        HttpSession session) {
        try{
            User user = userRepository.findByUserId(userId);
            if (user != null) {
                if(bCryptPasswordEncoder.matches(password, user.getPassword())){
                    session.setAttribute("userId", user.getUserId());
                    return ResponseEntity.ok("{\"message\": \"로그인 성공\"}");
                }
                else {
                    log.error("비밀번호가 일치하지 않습니다: userId={}", userId);
                    return ResponseEntity.badRequest().body("{\"message\": \" 비밀번호가 다릅니다.\"}");
                }
            } else{
                log.error("사용자 ID를 찾을 수 없습니다: {}", userId);
                return ResponseEntity.badRequest().body("{\"message\": \"아이디 혹은 비밀번호가 다릅니다.\"}");
            }
        } catch (Exception e){
            log.error("로그인 중 오류가 발생했습니다.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"서버 오류가 발생했습니다.\"}");
        }
    }
}

