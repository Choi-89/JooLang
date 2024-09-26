package com.project.FreeCycle.Controller;


import com.project.FreeCycle.Api.NaverApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/home")
public class LoginController {

    @Autowired
    NaverApi naverApi;


    @Operation(summary = "로그인 페이지", description = "로그인 페이지를 반환합니다. 로그인 오류가 있을 경우 오류 메시지를 포함합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 페이지 반환 성공"),
            @ApiResponse(responseCode = "400", description = "로그인 오류 발생")
    })
    @GetMapping("/home/login")
    public ResponseEntity<String> showLogin(@Parameter(description = "로그인 오류 메시지", required = false)@RequestParam(value = "error", required = false) String error) {

        if (error != null) {
            log.error("로그인 오류 발생: {}", error);
            return ResponseEntity.badRequest().body("아이디 혹은 비밀번호가 다릅니다.");
        }

        log.info("로그인 페이지 반환");
        return ResponseEntity.ok("login");
    }

}

