package com.project.FreeCycle.Controller;


import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
@RestController
@RequestMapping("/home")
public class LoginController {

    private final UserRepository userRepository;
    private final VerifyService verifyService;

    public LoginController(UserRepository userRepository, VerifyService verifyService) {
        this.userRepository = userRepository;
        this.verifyService = verifyService;
    }

    /**
     * 로그인 페이지 반환
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 페이지 반환 성공"),
            @ApiResponse(responseCode = "400", description = "로그인 페이지 오류 발생")
    })
    @Operation(summary = "로그인 페이지", description = "로그인 페이지를 반환합니다. 로그인 오류가 있을 경우 오류 메시지를 포함합니다.")
    @GetMapping("/login")
    public ResponseEntity<ApiResponseDTO<Void>> showLogin(
            @Parameter(description = "로그인 오류 메시지", required = false)
            @RequestParam(value = "error", required = false) String error) {
        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success","로그인 페이지 요청 성공",null);

        return ResponseEntity.ok(response);
    }

    /**
     * 실제 로그인 로직은 LoginFilter를 통해 구현됨. 이 코드는 프론트앤드 개발자를 위한 명세서를 위해 적은 코드
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 시 JWT 토큰 반환"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @Operation(summary = "로그인 로직 (Swagger 명세 전용)", description = "userId와 password를 포함하여 POST /login으로 요청을 보내면 기본 로그인 로직이 실행됩니다. " +
            "이 엔드포인트는 실제 요청을 처리하지 않고 문서화를 위해 제공됩니다"+"프론트엔드 팀은 JWT 토큰을 localStorage나 sessionStorage에 저장한 뒤," +
            " 인증이 필요한 API 요청 시 Authorization: Bearer <JWT 토큰> 형식으로 요청 헤더에 포함해 보내야 합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<Void>> loginProc(
            @Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") @NotBlank String userId,
            @Parameter(description = "사용자 비밀번호", required = true) @RequestParam(name = "password") @NotBlank String password) {
        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success","로그인 성공",null);

        return ResponseEntity.ok(response);
    }

    /**
     * oAuth2 소셜 로그인 설명을 위한 메서드, 실제로 작동 안됨.
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "소셜 로그인 페이지로 리디렉션"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @Operation(summary = "OAuth2 소셜 로그인 (Swagger 명세 전용)",
            description = "사용자가 /oauth2/authorization/{provider}로 리디렉션하면 해당 소셜 로그인 페이지로 이동합니다. 로그인 성공 시 서버는 JWT 토큰을 쿠키에 저장하고, " +
                    "클라이언트는 이후 요청에서 해당 토큰을 활용하여 인증할 수 있습니다. 실제 요청 처리가 아닌 명세용 설명입니다." +
                    "클라이언트가 /oauth2/authorization/naver로 접근하면 Spring Security의 OAuth2 설정에 따라 자동으로 네이버 로그인 로직이 실행됩니다. " +
                    "그리고 로그인에 성공하면 커스텀 성공 핸들러(CustomOAuth2SuccessHandler)가 작동하여 JWT 토큰을 발급하고, 이를 클라이언트에게 쿠키로 전달" +
                    "쿠키의 만료 시간은 1시간"
                    )
    @GetMapping("/oauth2/authorization/{provider}")
    public ResponseEntity<ApiResponseDTO<Void>> oauth2Login(
            @Parameter(description = "소셜 로그인 제공자 (예: google, facebook, github)", required = true)
            @PathVariable String provider) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(new ApiResponseDTO<>("redirect", "소셜 로그인 페이지로 리디렉션", null));
    }

    /**
     * 로그아웃
     * */

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @Operation(summary = "로그아웃 로직 (Swagger 명세 전용)",
            description = "POST /logout 요청을 보내면 클라이언트 측에서 JWT 토큰을 삭제해 로그아웃을 처리합니다. 이 엔드포인트는 실제 서버에서 처리하지 않고, 문서화를 위해 제공됩니다."+
                    "로그아웃 요청 시 클라이언트 측에서 저장된 JWT 토큰을 삭제해야 합니다. " +
                    "서버는 세션을 사용하지 않으므로 JWT 기반에서는 토큰 자체를 삭제하는 것이 곧 로그아웃을 의미")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logoutProc(){
        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success", "비밀번호 찾기 페이지로 이동", null);

        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 찾기
     * */
    @Operation(summary = "비밀번호 찾기 페이지",description = "비밀번호 찾기 페이지로 이동합니다. 인증 코드를 입력받고, " +
            "type 파라미터에 'password'를 지정하여 서버로 요청합니다.")
    @GetMapping("/find/password")
    public ResponseEntity<ApiResponseDTO<Void>> showFindPassword() {

        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success","비밀번호 찾기 페이지로 이동 성공",null);

        return ResponseEntity.ok(response);
    }


    /**
     * 요청자가 이미 가입되어있는 회원인지 확인을 함.
     * */

    @Operation(summary = "사용자 인증 확인 처리", description = "사용자 ID와 이메일을 통해 데이터베이스에 있는 사용자인지 확인합니다." +
            "사용자 인증 완료 후, 이메일 인증 가능함.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 인증 성공"),
            @ApiResponse(responseCode = "400", description = "사용자 인증 실패")
    })
    @PostMapping("/certifyUserProc")
    public ResponseEntity<ApiResponseDTO<Void>> certifyUser(
            @Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") @NotBlank String userId,
            @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") @NotBlank String email) {

        if (verifyService.existUser(userId, email)) {
            return ResponseEntity.ok(new ApiResponseDTO<>("success","사용자 인증 성공",null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDTO<>("error", "해당하는 아이디 혹은 이메일이 없습니다.", null));
        }
    }

    /**
     * 이메일 인증 코드 입력 페이지 이동
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 찾기 인증 코드 입력 페이지로 이동 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @Operation(summary = "비밀번호 찾기 인증 코드 입력 페이지", description = "비밀번호 찾기 용도로 이메일 인증 코드를 입력하는 페이지를 반환함." +
            "\"type 파라미터에 'userId'를 지정하여 서버로 요청합니다.\"")
    @GetMapping("/verifyCode/password")
    public ResponseEntity<ApiResponseDTO<Map<String,String>>> showVerifyCodePW(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId,
                                                 @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email,
                                                 @Parameter(description = "요청 타입 (userId: 아이디 찾기용, password: 비밀번호 찾기용)", required = true) @RequestParam(name = "type") String type
    ){

        Map<String, String> data = Map.of("userId", userId, "email", email, "type", type);
        return ResponseEntity.ok(new ApiResponseDTO<>("success", "인증 코드 입력 페이지로 이동", data));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 찾기 인증 코드 입력 페이지로 이동 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @Operation(summary = "아이디 찾기 인증 코드 입력 페이지", description = "아이디 찾기 용도로 이메일 인증 코드를 입력하는 페이지를 반환함." +
            "type 파라미터에 'userId'를 지정하여 서버로 요청합니다." +
            "이메일 인증을 하고 인증 번호가 맞으면 그 이메일로 가입된 유저 정보가 있는지 확인" +
            "있으면 그 유저의 userId를 화면에 반환 ( 원하면 이메일로 발송되게 수정 가능 )")
    @GetMapping("/verifyCode/userId")
    public ResponseEntity<ApiResponseDTO<Map<String,String>>> showVerifyCodeID(@Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email,
                                                                @Parameter(description = "요청 타입 (userId: 아이디 찾기용, password: 비밀번호 찾기용)", required = true) @RequestParam(name = "type") String type
    ){
        Map<String, String> data = Map.of("email", email, "type", type);
        return ResponseEntity.ok(new ApiResponseDTO<>("success", "인증 코드 입력 페이지로 이동", data));
    }


    /**
     * 이메일로 인증 코드 발송하는 api
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증번호 이메일 발송 성공"),
            @ApiResponse(responseCode = "500", description = "인증번호 발송 중 서버 오류 발생")
    })
    @Operation(summary = "이메일 인증 코드 전송 처리", description = "인증 코드를 이메일로 전송합니다." +
            "이메일 인증 번호 발송 버튼을 누르면 작동" +
            "type을 통해 아이디 찾기를 위한 인증코드인지, 비밀번호 찾기를 위한 인증코드인지 판단함.")
    @PostMapping("/sendCodeProc")
    public ResponseEntity<ApiResponseDTO<Void>> sendCodeProc(
            @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email,
            @Parameter(description = "요청 타입 (userId: 아이디 찾기용, password: 비밀번호 찾기용)", required = true) @RequestParam(name = "type") String type) {

        if (verifyService.sendEmail(email)) {

            String message = type.equals("userId") ? "아이디 찾기 인증 코드가 이메일로 발송되었습니다." : "비밀번호 찾기 인증 코드가 이메일로 발송되었습니다.";

            return ResponseEntity.ok(new ApiResponseDTO<>("success",message, null));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDTO<>("error", "인증번호 발송 중 오류 발생", null));
        }
    }


    /**
     * 이메일 인증 코드 인증 로직
     * 비밀번호 찾을 때도 이 로직 사용하고, 아이디 찾을 때도 이 API 사용하면 됨.
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 수정 페이지로 이동 (type이 password일 때)"),
            @ApiResponse(responseCode = "200", description = "아이디 찾기 성공 (type이 userId일 때)"),
            @ApiResponse(responseCode = "400", description = "인증번호 불일치 또는 유효하지 않은 요청"),
            @ApiResponse(responseCode = "404", description = "해당 이메일로 가입된 유저 정보가 없음")
    })
    @Operation(summary = "인증 코드 확인 처리", description = "입력된 인증 코드를 확인합니다." +
            "비밀번호 찾을 때, 아이디 찾을 때 모두 이 API 사용하면 됨" +
            "type 파라미터로 전달받은 userId || password 인자를 통해 로직이 구현됨.")
    @PostMapping("/verifyCodeProc")
    public ResponseEntity<ApiResponseDTO<Object>> verifyCodeProc(
            @Parameter(description = "인증 코드", required = true) @RequestParam(name = "verifyCode") @NotBlank String code,
            @Parameter(description = "요청 타입 (userId 또는 password)", required = true) @RequestParam(name = "type") @NotBlank String type,
            @Parameter(description = "사용자 이메일", required = false) @RequestParam(name = "email", required = false) String email) {

        if (verifyService.verifyCode(code)) {
            if ("userId".equals(type) && email != null) {
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    Map<String,String> data = Map.of("userId",user.getUserId());
                    return ResponseEntity.ok(new ApiResponseDTO<>("success","아이디 찾기 성공",data));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponseDTO<>("error", "해당 이메일로 가입된 유저 정보가 없습니다.", null));
                }
            } else if ("password".equals(type)) {
                return ResponseEntity.ok(new ApiResponseDTO<>("success", "비밀번호 수정 페이지로 이동", null));
            }
        }
        return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error", "인증번호가 일치하지 않습니다.", null));
    }


    /**
     * 비밀번호 수정하는 페이지로 이동함
     * */
    @Operation(summary = "비밀번호 수정 페이지", description = "비밀번호 수정 페이지를 반환합니다.")
    @GetMapping("/editPassword")
    public ResponseEntity<ApiResponseDTO<String>> showEditPassword(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId){

        ApiResponseDTO<String> response = new ApiResponseDTO<>("suceess", "비밀번호 수정 페이지로 이동",userId);

        return ResponseEntity.ok(response);
    }


    /**
     * 비밀번호 수정 로직
     * */
    @Operation(summary = "비밀번호 수정 처리", description = "사용자의 비밀번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
            @ApiResponse(responseCode = "409", description = "새 비밀번호가 현재 비밀번호와 동일함.")
    })
    @PostMapping("/updatePasswordProc")
    public ResponseEntity<ApiResponseDTO<Object>> editPasswordProc(
            @Parameter(description = "새 비밀번호", required = true) @RequestParam(name = "newPassword") String password,
            @Parameter(description = "비밀번호 확인", required = true) @RequestParam(name = "confirmPassword") String passwordConfirm,
            @Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId) {

        Map<String, Object> response = new HashMap<>();
        if (verifyService.checkPassword(password, passwordConfirm)) {
            if (verifyService.updatePassword(password, userId)) {
                Map<String, String> data = Map.of("userId", userId);

                return ResponseEntity.ok(new ApiResponseDTO<>("success","비밀번호가 성공적으로 변경되었습니다.",data));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponseDTO<>("error", "새 비밀번호가 현재 비밀번호와 동일합니다.", null));
            }
        } else {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDTO<>("error", "비밀번호가 일치하지 않습니다.", null));
        }
    }
}

