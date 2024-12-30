package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Dto.ApiResponseDTO;
import com.project.FreeCycle.Dto.PasswordDTO;
import com.project.FreeCycle.Dto.UserDTO;
import com.project.FreeCycle.Dto.VerifyCodeDTO;
import com.project.FreeCycle.Service.UserService;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/home")
public class JoinController {

    private final VerifyService verifyService;
    private final UserService userService;

    public JoinController(VerifyService verifyService, UserService userService) {
        this.verifyService = verifyService;
        this.userService = userService;
    }

    @Operation(summary = "회원가입 페이지", description = "일반 회원가입 페이지로 이동합니다. 디자인 기준으로는 회원가입 버튼 누르면 회원가입" +
            "페이지로 이동하게 하면 됩니다.")
    @GetMapping("/join")
    public ResponseEntity<ApiResponseDTO<Void>> ShowJoin(){
        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success","회원가입 페이지로 이동", null);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 처리", description = "회원 정보를 받아 회원가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/joinProc")
    public ResponseEntity<ApiResponseDTO<Void>> JoinProc(@Valid @RequestBody UserDTO userDTO) {

        try{
            userService.saveUser(userDTO);

            return ResponseEntity.ok(new ApiResponseDTO<>("success", "회원가입 성공", null));

        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error","회원가입 도중 오류가 발생하였습니다.",null));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponseDTO<>("error","서버 오류",null)
            );
        }
    }

    // OAuth2 방식으로 처음 로그인 시도 할 시 비밀번호 업데이트 함.
    @Operation(summary = "OAuth2 비밀번호 설정 페이지", description = "소셜계정 로그인을 처음 시도했다면" +
            "비밀번호 설정 페이지로 이동합니다.")
    @GetMapping("/joinPassword")
    public ResponseEntity<ApiResponseDTO<Void>> joinPassword(){
        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success","oAuth2 소셜 로그인 비밀번호 설정 페이지 성공", null);

        return ResponseEntity.ok(response);
    }



    @Operation(summary = "OAuth2 비밀번호 설정 처리", description = "소셜 회원의 비밀번호를 설정합니다." +
            "등록할 비밀번호 json 형식으로 서버로 전달. 새 비밀번호 ( newPassword ) , 비밀번호 확인 ( confirmPassword )" +
            "소셜 사용자 비밀번호 재설정 할 때만 인증 방식을 세션으로 사용할거임." + "비밀번호 재설정 후, 다시 소셜 로그인을 진행해야함. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 설정 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/joinPasswordProc")
    public ResponseEntity<ApiResponseDTO<Void>> joinPasswordProc(@RequestBody PasswordDTO request
            ,HttpSession session){

        String password = request.getPassword();
        String passwordConfirm = request.getConfirmPassword();

        String userId = (String) session.getAttribute("userId");

        if(userId == null) {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error", "세션에 userId가 없습니다.", null));
        }

        if(verifyService.checkPassword(password,passwordConfirm)){
            if(verifyService.updatePassword(password, userId)) {
                session.removeAttribute("userId");
                return ResponseEntity.ok(new ApiResponseDTO<>("success", "비밀번호가 성공적으로 설정되었습니다.", null));

            } else{
                log.error("비밀번호 설정 중 오류가 발생하였습니다. userId = {}", userId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponseDTO<>("error", "비밀번호 설정 중 서버에 오류가 발생했습니다.", null));
            }
        } else{
            log.error("비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error", "비밀번호가 일치하지 않습니다.", null));
        }
    }


    @Operation(summary = "휴대폰 인증 페이지 이동 ", description = "휴대폰 번호를 입력받아 인증 절차를 진행할 수 있는 페이지로 이동합니다." +
            "phoneNumber 파라미터에 인증 받을 핸드폰 번호를 적고 서버에 전송")
    @GetMapping("/verifyPhone")
    public ResponseEntity<ApiResponseDTO<Void>> VerfiyPhone(){

        ApiResponseDTO<Void> response = new ApiResponseDTO<>("success", "휴대폰 인증을 위한 페이지로 이동 성공",null);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "휴대폰 인증 번호 전송", description = "입력된 휴대폰 번호로 인증 번호를 발송합니다. 성공 시, " +
            "인증 번호가 해당 번호로 전송됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 번호 전송 성공"),
            @ApiResponse(responseCode = "400", description = "인증 번호 전송 실패")
    })
    @PostMapping("/sendSmsProc")
    public ResponseEntity<ApiResponseDTO<Void>> sendSMSProc(HttpServletRequest request,
                                                            @Parameter(description = "인증할 휴대폰 번호", required = true)
            @RequestParam(name = "phoneNumber") @NotBlank String phoneNumber){

        if(verifyService.sendSMS(phoneNumber)){
            return ResponseEntity.ok(new ApiResponseDTO<>("success","인증번호가 성공적으로 전송 되었습니다.", null));
        }

        return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error","인증번호 전송에 오류가 발생했습니다.",null));
    }

    @Operation(summary = "인증 코드 확인", description = "사용자가 입력한 인증 코드를 확인하고" +
            "해당 번호로 가입 되어있는지, 회원가입이 가능한지 여부를 검사합니다." +
            "json 형식으로 인증코드 (code), 휴대폰 번호 (phoneNumber) 서버로 전달")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 번호 또는 이미 가입된 회원")
    })
    @PostMapping("/checkProc")
    public ResponseEntity<ApiResponseDTO<Void>> checkProc(HttpServletRequest servletRequest, @RequestBody VerifyCodeDTO request){

        String phoneNumber = request.getPhoneNumber();
        String code = request.getCode();
        UserDTO userDTO = verifyService.verifyPhoneNum(phoneNumber);
        if(userDTO != null){

            return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error","이미 가입 되어있는 회원입니다.", null));
        }

        if(verifyService.verifyCode(code)) {
            return ResponseEntity.ok(new ApiResponseDTO<>("success", "인증 성공하였습니다. 회원가입 페이지로 이동합니다.",null));
        }

        return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error","인증번호가 틀렸습니다.",null));
    }


    @Operation(summary = "아이디 중복 확인 ", description = "회원가입 할 때, 아이디 중복 체크 API" +
            "json 형식으로 중복 확인 할 유저 아이디(userId) 서버로 전달")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복되는 아이디가 없음."),
            @ApiResponse(responseCode = "400", description = "이미 가입된 회원")
    })
    @PostMapping("/checkId")
    public ResponseEntity<ApiResponseDTO<Void>> checkId(@RequestBody VerifyCodeDTO request){

        String userId = request.getUserId();

        if(verifyService.existUserId(userId)){
            return ResponseEntity.ok(new ApiResponseDTO<>("success", "사용가능한 ID 입니다.",null));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponseDTO<>("error","중복되는 아이디입니다.", null));
        }
    }
}