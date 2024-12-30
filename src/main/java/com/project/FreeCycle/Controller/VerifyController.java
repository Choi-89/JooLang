package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Dto.VerifyCodeDTO;
import com.project.FreeCycle.Dto.certifyUserDTO;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class VerifyController {

    private final UserRepository userRepository;
    private final VerifyService verifyService;

    public VerifyController(UserRepository userRepository, VerifyService verifyService) {
        this.userRepository = userRepository;
        this.verifyService = verifyService;
    }


    /**
     * 요청자가 이미 가입되어있는 회원인지 확인을 함.
     * */

    @Operation(summary = "사용자 인증 확인 처리", description = "사용자 ID와 이메일을 통해 데이터베이스에 있는 사용자인지 확인합니다." +
            "json 형식으로 유저 아이디 (userId), 사용자 이메일 (email) 서버로 전달" +
            "사용자 인증 완료 후, 이메일 인증 가능함." +
            "이메일을 통해 사용자가 가입된 회원인지 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 인증 성공"),
            @ApiResponse(responseCode = "400", description = "사용자 인증 실패")
    })
    @PostMapping("/certifyUserProc")
    public ResponseEntity<ApiResponseDTO<Void>> certifyUser(@RequestBody certifyUserDTO request)
    {
        String userId = request.getUserId();
        String email = request.getEmail();

        if (verifyService.existUser(userId,email)) {
            return ResponseEntity.ok(new ApiResponseDTO<>("success","사용자 인증 성공",null));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDTO<>("error", "해당하는 아이디 혹은 이메일이 없습니다.", null));
        }
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
            "json 형식으로 요청 타입 (userId 또는 password), 이메일 ( email ) 서버로 전달" +
            "type을 통해 아이디 찾기를 위한 인증코드인지, 비밀번호 찾기를 위한 인증코드인지 판단함.")
    @PostMapping("/sendCodeProc")
    public ResponseEntity<ApiResponseDTO<Void>> sendCodeProc(HttpServletRequest sessionRequest, @RequestBody VerifyCodeDTO request) {

        String email = request.getEmail();
        String type = request.getType();

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
            "json 형식으로 인증번호(code), 요청 타입 (userId 또는 password) type, 이메일 (email) 서버로 전달"+
            "type 파라미터로 전달받은 userId || password 인자를 통해 로직이 구현됨.")
    @PostMapping("/verifyCodeProc")
    public ResponseEntity<ApiResponseDTO<Object>> verifyCodeProc(HttpServletRequest servletRequest,@RequestBody VerifyCodeDTO request) {
        String email = request.getEmail();
        String type = request.getType();
        String code = request.getCode();

        System.out.println("클라이언트 요청 - 이메일: " + email + ", 타입: " + type + ", 코드: " + code);


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
}
