package com.project.FreeCycle.Controller;


import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@Controller
@RequestMapping("/home")
public class LoginController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final VerifyService verifyService;

    public LoginController(BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository, VerifyService verifyService) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.verifyService = verifyService;
    }

    /**
     * 로그인
     * */
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "로그인 페이지 반환 성공"),
//            @ApiResponse(responseCode = "400", description = "로그인 오류 발생")
//    })
//    @Operation(summary = "로그인 페이지", description = "로그인 페이지를 반환합니다. 로그인 오류가 있을 경우 오류 메시지를 포함합니다.")
//    @GetMapping("/login")
//    public ResponseEntity<Map<String, String>> showLogin(@Parameter(description = "로그인 오류 메시지", required = false)
//                                                             @RequestParam(value = "error", required = false) String error) {
//        Map<String, String> response = new HashMap<>();
//
//        if (error != null) {
//            log.error("로그인 오류 발생: {}", error);
//            response.put("status","error");
//            response.put("message", "아이디 혹은 비밀번호가 다릅니다.");
//            return ResponseEntity.badRequest().body(response);
//        }
//        log.info("로그인 페이지 반환");
//        response.put("status","success");
//        response.put("message","로그인 페이지 요청 성공");
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/login")
    public String showLogin(){
        return "login";
    }

    /**
     * 비밀번호 찾기
     * */
    @Operation(summary = "비밀번호 찾기 페이지",description = "비밀번호 찾기 페이지로 이동합니다. 인증 코드를 입력받고, " +
            "type 파라미터에 'password'를 지정하여 서버로 요청합니다.")
    @GetMapping("/find/password")
    public ResponseEntity<Map<String, String>> ShowFindPassword() {
        Map<String, String> response = new HashMap<>();
        response.put("status","success");
        response.put("message", "비밀번호 찾기 페이지로 이동");
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "사용자 인증 확인 처리", description = "사용자 ID와 이메일을 통해 데이터베이스에 있는 사용자인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 인증 성공"),
            @ApiResponse(responseCode = "400", description = "사용자 인증 실패")
    })
    @PostMapping("/certifyUserProc")
    public  ResponseEntity<Map<String, String>> certifyUser(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") @NotBlank String userId,
                                              @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") @NotBlank String email
    ){
        Map<String, String> response = new HashMap<>();
        log.info("certifyUserProc 호출 - userId: {}", userId);
        /* 입력 받은 사용자 Id와 email이 데이터베이스에 있는지 확인하는
         * 로직 구현 */
        if(verifyService.existUser(userId, email)){
            log.info("유저 아이디 가져왔는지 확인 : userId={}", userId);
            response.put("status","success");
            response.put("message", "인증 성공");
            return ResponseEntity.ok(response);
        } else{
            log.error("사용자 인증 실패: userId={}, email={}", userId, email);
            response.put("status", "error");
            response.put("message", "해당하는 아이디 혹은 이메일이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "이메일 인증 코드 입력 페이지", description = "이메일 인증 코드를 입력하는 페이지를 반환합니다.")
    @GetMapping("/verifyCode")
    public ResponseEntity<Map<String, String>> ShowVerifyCode(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId,
                                                 @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email
    ){
        Map<String, String> response = new HashMap<>();
        log.info("verifyCode 페이지 요청: userId={}, email={}", userId, email);
        response.put("message", "인증 코드 입력 페이지로 이동");
        response.put("userId", userId);
        response.put("email", email);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이메일 인증 코드 전송 처리", description = "인증 코드를 이메일로 전송합니다.")
    @PostMapping("/sendCodeProc")
    public ResponseEntity<Map<String, String>> SendCodeProc(@Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email,
                                                 @Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId){
        Map<String, String> response = new HashMap<>();
        log.info("인증 코드 전송 요청: userId={}, email={}", userId, email);

        /* 인증번호 발생 후 메일 제출 코드 */
        if(verifyService.sendEmail(email)){
            log.info("인증 코드 전송 성공: userId={}", userId);
            response.put("status","success");
            response.put("message", "인증번호 이메일로 발송 성공");
            return ResponseEntity.ok(response);
        } else{
            log.error("인증 코드 전송 실패: userId={}", userId);
            response.put("status","error");
            response.put("message", "인증번호 발송 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "인증 코드 확인 처리", description = "입력된 인증 코드를 확인합니다. type으로 userId를 찾기 위한 로직을 실행할지" +
            "password를 찾기 위한 로직을 실행할지 서버에서 판단.")
    @PostMapping("/verifyCodeProc")
    public ResponseEntity<Map<String, String>> VerifyCodeProc( @Parameter(description = "인증 코드", required = true) @RequestParam(name = "verifyCode") @NotBlank String code,
                                                  @Parameter(description = "사용자 ID", required = false) @RequestParam(name = "userId", required = false) String userId,
                                                  @Parameter(description = "요청 타입 (password 또는 userId)", required = true) @RequestParam(name = "type") @NotBlank String type,
                                                  @Parameter(description = "사용자 이메일", required = false) @RequestParam(name = "email", required = false) String email){
        Map<String, String> response = new HashMap<>();
        log.info("인증 코드 확인 요청: userId={}, code={}", userId, code);

        /* 인증 번호 비교 로직 */
        if(verifyService.verifyCode(code)){
            log.info("인증 코드 일치: userId={}", userId);
            if("password".equals(type)){
                response.put("status","success");
                response.put("message", "비밀번호 수정 페이지로 이동");
                return ResponseEntity.ok(response);
            }

            if("userId".equals(type) && email != null){
                User user= userRepository.findByEmail(email);
                if(user != null) {
                    response.put("status","success");
                    response.put("message", "아이디 찾기 성공");
                    response.put("data", Map.of("userId", user.getUserId()).toString());
                    return ResponseEntity.ok(response);
                } else{
                    response.put("status","error");
                    response.put("message", "해당 이메일로 가입된 유저 정보가 없습니다.");
                    return ResponseEntity.badRequest().body(response);
                }
            }
        }
        log.error("인증 코드 불일치: type={}, code={}", type , code);
        response.put("message", "인증번호가 일치하지 않습니다.");
        return ResponseEntity.badRequest().body(response);
    }


    @Operation(summary = "비밀번호 수정 페이지", description = "비밀번호 수정 페이지를 반환합니다.")
    @GetMapping("/editPassword")
    public ResponseEntity<Map<String, String>> ShowEditPassword(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId){
        Map<String, String> response = new HashMap<>();

        log.info("비밀번호 수정 페이지 요청: userId={}", userId);
        response.put("status", "success");
        response.put("message", "비밀번호 수정 페이지로 이동");
        response.put("data", userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 수정 처리", description = "사용자의 비밀번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치 또는 업데이트 실패")
    })
    @PostMapping("/updatePasswordProc")
    public ResponseEntity<Map<String, String>> EditPasswordProc(@Parameter(description = "새 비밀번호", required = true) @RequestParam(name =  "newPassword") String password,
                                                   @Parameter(description = "비밀번호 확인", required = true) @RequestParam(name = "confirmPassword") String passwordConfirm,
                                                   @Parameter(description = "사용자 ID", required = true) @RequestParam (name = "userId") String userId) {

        Map<String, String> response = new HashMap<>();

        /* 비밀번호 업데이트 로직 구현 */
        log.info("비밀번호 수정 요청: userId={}, newPassword={}, confirmPassword={}", userId, password, passwordConfirm);

        if (verifyService.checkPassword(password, passwordConfirm)) {
            log.info("비밀번호가 일치합니다.");
            if (verifyService.updatePassword(password, userId)) {
                log.info("비밀번호가 성공적으로 업데이트되었습니다: userId={}", userId);
                response.put("status", "success");
                response.put("message", "비밀번호가 성공적으로 업데이트되었습니다.");
                response.put("data", Map.of("userId", userId).toString());
                return ResponseEntity.ok(response);
            } else{
                log.error("비밀번호 업데이트 중 오류 발생: userId={}", userId);
                response.put("status", "error");
                response.put("message", "새 비밀번호가 현재 비밀번호와 동일합니다.");
                return ResponseEntity.badRequest().body(response);
            }
        } else{
            log.error("비밀번호가 일치하지 않습니다: newPassword={}, confirmPassword={}", password, passwordConfirm);
            response.put("status", "error");
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }


    /**
     * 아이디 찾기
     * */

    @Operation(summary = "아이디 찾기 페이지", description = "아이디 찾기 페이지로 이동합니다. 인증 코드를 입력받고, " +
            "type 파라미터에 'userId'를 지정하여 서버로 요청합니다.")
    @GetMapping("/find/userId")
    public ResponseEntity<Map<String, String>> ShowFindId() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "아이디 찾기 페이지로 이동");
        return ResponseEntity.ok(response);
    }
    // 이메일 인증을 하고 인증 번호가 맞으면 그 이메일로 가입된 유저 정보가 있는지 확인
    // 있으면 그 유저의 userId를 화면에 반환

    @Operation(summary = "이메일로 인증코드 전송", description = "클라이언트에게 전달받은 email로 인증코드를 전송합니다." +
            "이후 /verifyCodeProc api 호출하여 인증 마무리")
    @PostMapping("/find/userId/sendCode")
    public ResponseEntity<Map<String, String>> sendUserIdCode(@RequestParam(name = "email") String email){
        Map<String, String> response = new HashMap<>();
        log.info("인증 코드 전송 요청: email={}", email);

        /* 인증번호 발생 후 메일 제출 코드 */
        if(verifyService.sendEmail(email)){
            response.put("message", "아이디 찾기 인증 코드가 이메일로 발송되었습니다.");
            return ResponseEntity.ok(response);
        } else{
            response.put("message", "인증번호 발송 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}

