package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Dto.UserDTO;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/home")
public class SMSController {

    @Autowired
    private VerifyService verifyService;

    @Operation(summary = "휴대폰 인증 페이지 이동 ", description = "휴대폰 번호 인즈을 위한 페이지로 이동")
    @GetMapping("/verifyPhone")
    public ResponseEntity<String> VerfiyPhone(){
        return ResponseEntity.ok("{\"message\": \"휴대폰 인증 페이지\"}");
    }

    @Operation(summary = "휴대폰 인증 번호 전송", description = "입력된 휴대폰 번호로 인증 번호 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 번호 전송 성공"),
            @ApiResponse(responseCode = "400", description = "인증 번호 전송 실패")
    })
    @PostMapping("/sendSmsProc")
    public ResponseEntity<String> sendSMSProc(
            @Parameter(description = "인증할 휴대폰 번호", required = true)
            @RequestParam(name = "phoneNumber") String phoneNumber){
        log.info("휴대폰 번호로 인증 번호 전송 시도: {}", phoneNumber);

        if(verifyService.sendSMS(phoneNumber)){
            log.info("인증 번호 전송 성공");
            return ResponseEntity.ok("인증 번호가 성공적으로 전송되었습니다.");
        }

        log.error("인증 번호 전송 실패");
        return ResponseEntity.badRequest().body("인증번호 전송에 오류가 발생했습니다.");
    }

    @Operation(summary = "인증 코드 확인", description = "입력된 인증 코드를 확인하여 회원가입 가능 여부를 체크합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 번호 또는 이미 가입된 회원")
    })
    @PostMapping("/checkProc")
    public ResponseEntity<String> checkProc(
                @Parameter(description = "입력된 인증 코드", required = true)
                @RequestParam String verifyCode,
                @Parameter(description = "휴대폰 번호", required = true)
                @RequestParam(name = "phoneNumber") String phoneNumber
                ){
        log.info("인증 코드 확인 시도: {}", verifyCode);

        UserDTO userDTO = verifyService.verifyPhoneNum(phoneNumber);
        if(userDTO != null){
            log.error("이미 중복 된 회원");
            return ResponseEntity.badRequest().body("이미 가입된 회원입니다.");
        }

        if(verifyService.verifyCode(verifyCode)) {
            log.info("인증 성공");
            return ResponseEntity.ok("인증 성공, 회원가입 페이지로 이동합니다.");
        }

        log.error("인증 실패: 잘못된 인증 번호");
        return ResponseEntity.badRequest().body("인증번호가 틀립니다.");

    }


}
