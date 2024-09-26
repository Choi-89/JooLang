package com.project.FreeCycle.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UserController {

    @Operation(summary = "마이페이지", description = "로그인된 사용자의 마이페이지 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이페이지 반환 성공")
    })
    @GetMapping("/mypage")
    private ResponseEntity<String> Mypage(){
        log.info("마이페이지 요청 처리");
        return ResponseEntity.ok("mypage");
//        return "mypage";
    }

}
