package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class PasswordController {
    private final VerifyService verifyService;

    @Autowired
    public PasswordController(VerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @Operation(summary = "비밀번호 찾기 페이지", description = "비밀번호 찾기 페이지를 반환")
    @GetMapping("/certifyUser")
    public ResponseEntity<String> ShowFindPassword(){
//        return "certifyUser";
        return ResponseEntity.ok("certifyUser");
    }

    @Operation(summary = "사용자 인증 확인 처리", description = "사용자 ID와 이메일을 통해 데이터 베이스에 있는 사용자인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 인증 성공"),
            @ApiResponse(responseCode = "400", description = "사용자 인증 실패")
    })
    @PostMapping("/certifyUserProc")
    public ResponseEntity<String> certifyUser(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId,
                              @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email
                              ){

        log.info("certifyUserProc 호출 - userId: {}", userId);
        /* 입력 받은 사용자 Id와 email이 데이터베이스에 있는지 확인하는
        * 로직 구현 */
        if(verifyService.existUser(userId, email)){
            log.info("유저 아이디 가져왔는지 확인 : userId={}", userId);

            log.info("모델에 추가된 데이터: userId={}, email={}",userId, email);
//            return "verifyCode";
            return ResponseEntity.ok("verifyCode");
        } else{
//            return "redirect:/certifyUser";
            log.error("사용자 인증 실패: userId={}, email={}", userId, email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당하는 아이디 혹은 이메일이 없습니다.");
        }
    }

    @Operation(summary = "인증 코드 입력 페이지", description = "인증 코드를 입력하는 페이지를 반환합니다.")
    @GetMapping("/verifyCode")
    public ResponseEntity<String> ShowVerifyCode(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId,
                                 @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email
                                 ){
        log.info("verifyCode 페이지 요청: userId={}, email={}", userId, email);
//        return "verifyCode";
        return ResponseEntity.ok("verifyCode");
    }

    @Operation(summary = "인증 코드 전송 처리", description = "인증 코드를 이메일로 전송합니다.")
    @PostMapping("/sendCodeProc")
    public ResponseEntity<String> VerifyCodeProc(@Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email,
                                 @Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId){
        log.info("인증 코드 전송 요청: userId={}, email={}", userId, email);

        /* 인증번호 발생 후 메일 제출 코드 */
        if(verifyService.sendEmail(email)){
            log.info("인증 코드 전송 성공: userId={}", userId);
//            return "verifyCode";
            return ResponseEntity.ok("verifyCode");
        } else{
            log.error("인증 코드 전송 실패: userId={}", userId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증번호 발송 중 오류 발생");
        }
    }

    @Operation(summary = "인증 코드 확인 처리", description = "입력된 인증 코드를 확인합니다.")
    @PostMapping("/verifyCodeProc")
    public ResponseEntity<String> SendCodeProc( @Parameter(description = "인증 코드", required = true) @RequestParam(name = "verifyCode") String code,
                                                @Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId){
        log.info("인증 코드 확인 요청: userId={}, code={}", userId, code);

        /* 인증 번호 비교 로직 */
        if(verifyService.verifyCode(code)){
            log.info("인증 코드 일치: userId={}", userId);
//            return "editPassword";
            return ResponseEntity.ok("editPassword");
        }
        log.error("인증 코드 불일치: userId={}, code={}", userId, code);
        return ResponseEntity.badRequest().body("인증번호가 일치하지 않습니다.");
//        return "verifyCode";
    }

    @Operation(summary = "비밀번호 수정 페이지", description = "비밀번호 수정 페이지를 반환합니다.")
    @GetMapping("/editPassword")
    public ResponseEntity<String> ShowEditPassword(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId){

//        return "editPassword";
        log.info("비밀번호 수정 페이지 요청: userId={}", userId);
        return ResponseEntity.ok("editPassword");
    }

    @Operation(summary = "비밀번호 수정 처리", description = "사용자의 비밀번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치 또는 업데이트 실패")
    })
    @PostMapping("/updatePasswordProc")
    public ResponseEntity<String> EditPasswordProc(@Parameter(description = "새 비밀번호", required = true) @RequestParam(name =  "newPassword") String password,
                                                   @Parameter(description = "비밀번호 확인", required = true) @RequestParam(name = "confirmPassword") String passwordConfirm,
                                                   @Parameter(description = "사용자 ID", required = true) @RequestParam (name = "userId") String userId) {


        /* 비밀번호 업데이트 로직 구현 */
        log.info("비밀번호 수정 요청: userId={}, newPassword={}, confirmPassword={}", userId, password, passwordConfirm);

        if (verifyService.checkPassword(password, passwordConfirm)) {
            log.info("비밀번호가 일치합니다.");
            if (verifyService.updatePassword(password, userId)) {
                log.info("비밀번호가 성공적으로 업데이트되었습니다: userId={}", userId);
//                return "redirect:/";
                return ResponseEntity.ok("비밀번호가 성공적으로 업데이트되었습니다.");
            } else{
                log.error("비밀번호 업데이트 중 오류 발생: userId={}", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("새 비밀번호가 현재 비밀번호와 동일합니다.");
            }
        } else{
            log.error("비밀번호가 일치하지 않습니다: newPassword={}, confirmPassword={}", password, passwordConfirm);
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
//
//        model.addAttribute("userId", userId);  // 다시 비밀번호 설정 위해 model에 userId 다시 추가
//        return "editPassword";
    }
}
