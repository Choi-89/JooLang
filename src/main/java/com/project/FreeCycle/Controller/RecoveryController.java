package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Dto.PasswordDTO;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class RecoveryController {

    private final VerifyService verifyService;

    public RecoveryController(VerifyService verifyService) {
        this.verifyService = verifyService;
    }


    /**
     * 비밀번호 찾기 홈페이지로 이동
     * 비밀번호 찾기 로직 중 2번에 해당하는 페이지
     * */
    @Operation(summary = "비밀번호 찾기 페이지",description = "비밀번호 찾기 페이지로 이동합니다. " +
            "type 파라미터에 'password'를 지정하여 서버로 요청합니다." +
            "비밀번호 찾기 로직 중 2번에 해당하는 페이지" +
            "userId와 email을 데이터 전달 받아서 \n" +
            "가입된 회원인지 확인")
    @GetMapping("/find/password")
    public ResponseEntity<ApiResponseDTO<Void>> showFindPassword() {

        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success","비밀번호 찾기 페이지로 이동 성공",null);

        return ResponseEntity.ok(response);
    }



    /**
     * 이메일 인증 코드 입력 페이지 이동
     * ( 비밀번호 용)
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 찾기 인증 코드 입력 페이지로 이동 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @Operation(summary = "비밀번호 찾기 인증 코드 입력 페이지", description = "비밀번호 찾기 용도로 이메일 인증 코드를 입력하는 페이지를 반환함." +
            "쿼리 데이터 형식으로 요청 타입 (userId 또는 password), 이메일 ( email ), 유저 아이디 (userId) 서버로 전달" +
            "비밀번호 로직 내 2번에 해당하는 페이지")
    @GetMapping("/verifyCode/password")
    public ResponseEntity<ApiResponseDTO<Map<String,String>>> showVerifyCodePW(
            @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email,
            @Parameter(description = "사용자 아이디", required = true) @RequestParam(name = "userId") String userId,
            @Parameter(description = " 요청 타입 (userId 또는 password)", required = true) @RequestParam(name = "type") String type){


        Map<String, String> data = Map.of("userId", userId, "email", email, "type", type);
        return ResponseEntity.ok(new ApiResponseDTO<>("success", "인증 코드 입력 페이지로 이동", data));
    }


    /**
     * 이메일 인증 코드 입력 페이지 이동
     * ( 아이디 찾기 용 )
     * */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아이디 찾기 인증 코드 입력 페이지로 이동 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    @Operation(summary = "아이디 찾기 인증 코드 입력 페이지", description = "아이디 찾기 용도로 이메일 인증 코드를 입력하는 페이지를 반환함." +
            "쿼리 데이터 형식으로 요청 타입 (userId 또는 password), 이메일 ( email ) 서버로 전달" +
            "이메일 인증을 하고 인증 번호가 맞으면 그 이메일로 가입된 유저 정보가 있는지 확인" +
            "있으면 그 유저의 userId를 화면에 반환 ( 원하면 이메일로 발송되게 수정 가능 )")
    @GetMapping("/verifyCode/userId")
    public ResponseEntity<ApiResponseDTO<Map<String,String>>> showVerifyCodeID(
            @Parameter(description = "사용자 이메일", required = true) @RequestParam(name = "email") String email,
            @Parameter(description = " 요청 타입 (userId 또는 password)", required = true) @RequestParam(name = "type") String type){

        Map<String, String> data = Map.of("email", email, "type", type);
        return ResponseEntity.ok(new ApiResponseDTO<>("success", "인증 코드 입력 페이지로 이동", data));
    }


    /**
     * 비밀번호 수정하는 페이지로 이동함
     * */
    @Operation(summary = "비밀번호 수정 페이지", description = "가입된 유저라면 비밀번호를 재설정하는 페이지로 이동시킴." +
            "비밀번호 로직 내 3번에 해당하는 페이지")
    @GetMapping("/editPassword")
    public ResponseEntity<ApiResponseDTO<String>> showEditPassword(@Parameter(description = "사용자 ID", required = true) @RequestParam(name = "userId") String userId){

        ApiResponseDTO<String> response = new ApiResponseDTO<>("suceess", "비밀번호 수정 페이지로 이동",userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 수정 로직
     * */
    @Operation(summary = "비밀번호 수정 처리", description = "사용자의 비밀번호를 수정합니다." +
            "json 형식으로 새 비밀번호(newPassword), 비밀번호 확인(confirmPassword), 사용자ID (userId) 서버로 전달")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 수정 성공"),
            @ApiResponse(responseCode = "409", description = "새 비밀번호가 현재 비밀번호와 동일함.")
    })
    @PostMapping("/updatePasswordProc")
    public ResponseEntity<ApiResponseDTO<Object>> editPasswordProc(@RequestBody PasswordDTO request) {
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();
        String userId = request.getUserId();

//        Map<String, Object> response = new HashMap<>();


        if (verifyService.checkPassword(password, confirmPassword)) {
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
