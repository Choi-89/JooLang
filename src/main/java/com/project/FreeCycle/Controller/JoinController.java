package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.JoinRequestDTO;
import com.project.FreeCycle.Dto.LocationDTO;
import com.project.FreeCycle.Dto.UserDTO;
import com.project.FreeCycle.Service.LocationService;
import com.project.FreeCycle.Service.UserService;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
public class JoinController {

    private final LocationService locationService;
    private final VerifyService verifyService;
    private final UserService userService;

    public JoinController(LocationService locationService, VerifyService verifyService, UserService userService) {
        this.locationService = locationService;
        this.verifyService = verifyService;
        this.userService = userService;
    }

    @Operation(summary = "회원가입 페이지", description = "일반 회원가입 페이지로 이동합니다. 디자인 기준으로는 회원가입 버튼 누르면 회원가입" +
            "페이지로 이동하게 하면 됩니다.")
    @GetMapping("/join")
    public ResponseEntity<Map<String, String>> ShowJoin(){
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message","회원가입 페이지로 이동");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 처리", description = "회원 정보를 받아 회원가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/joinProc")
    public ResponseEntity<Map<String, String>> JoinProc(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {

        Map<String, String> response = new HashMap<>();

        try{

            UserDTO userDTO = joinRequestDTO.getUserDTO();
            LocationDTO locationDTO = joinRequestDTO.getLocationDTO();

            log.info("회원가입 요청: userId={}, email={}, name={}, phoneNum={}",
                    userDTO.getUserId(), userDTO.getEmail(), userDTO.getUsername(), userDTO.getPhoneNum());

            User savedUser = userService.saveUser(userDTO);
            log.info("User 저장 완료: {}", savedUser);

            locationService.LocationSave(locationDTO, savedUser);
            log.info("Location 정보 저장 완료");
            response.put("status", "success");
            response.put("message","회원가입 성공");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e){
            log.error("회원가입 오류: {}", e.getMessage());
            response.put("status", "error");
            response.put("message","회원가입 도중 오류가 발생했습니다.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e){
            log.error("회원가입 중 알 수 없는 오류 발생", e);
            response.put("status", "error");
            response.put("message","서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // OAuth2 방식으로 처음 로그인 시도 할 시 비밀번호 업데이트 함.
    @Operation(summary = "OAuth2 비밀번호 설정 페이지", description = "소셜계정 로그인을 처음 시도했다면" +
            "비밀번호 설정 페이지로 이동합니다.")
    @GetMapping("/joinPassword")
    public ResponseEntity<Map<String, String>> joinPassword(){
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message","oAuth2 비밀번호 설정 페이지");

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "OAuth2 비밀번호 설정 처리", description = "소셜 회원의 비밀번호를 설정합니다." +
            "등록할 비밀번호를 서버에게 전달.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 설정 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/joinPasswordProc")
    public ResponseEntity<Map<String, String>> joinPasswordProc(
            @Parameter(description = "새 비밀번호", required = true) @RequestParam(name = "newPassword") @NotBlank String password,
            @Parameter(description = "비밀번호 확인", required = true) @RequestParam(name = "confirmPassword") @NotBlank String passwordConfirm
            ,HttpSession session){

        String userId = (String) session.getAttribute("userId");
        Map<String, String> response = new HashMap<>();

        if(userId == null) {
            log.error("세션에 userId가 없습니다.");
            response.put("status", "error");
            response.put("message","세션에 userId가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        log.info("비밀번호 설정 요청 : userId = {}, newPassword = {}, confirmPassword = {}", userId, password, passwordConfirm);
        if(verifyService.checkPassword(password,passwordConfirm)){
            log.info("비밀번호가 일치합니다.");
            if(verifyService.updatePassword(password, userId)) {
                log.info("비밀번호가 성공적으로 설정되었습니다: userId={}", userId);
                session.removeAttribute("userId");
                response.put("status", "success");
                response.put("message","비밀번호가 성공적으로 설정 되었습니다.");
                return ResponseEntity.ok(response);
            } else{
                log.error("비밀번호 설정 중 오류가 발생하였습니다. userId = {}", userId);
                response.put("status", "error");
                response.put("message","비밀번호 설정 중 서버에 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response);
            }
        } else{
            log.error("비밀번호가 일치하지 않습니다.");
            response.put("status", "error");
            response.put("message","비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "휴대폰 인증 페이지 이동 ", description = "대폰 번호를 입력받아 인증 절차를 진행할 수 있는 페이지로 이동합니다." +
            "phoneNumber 파라미터에 인증 받을 핸드폰 번호를 적고 서버에 전송")
    @GetMapping("/verifyPhone")
    public ResponseEntity<Map<String, String>> VerfiyPhone(){
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message","휴대폰 인증을 위한 페이지로 이동");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "휴대폰 인증 번호 전송", description = "입력된 휴대폰 번호로 인증 번호를 발송합니다. 성공 시, " +
            "인증 번호가 해당 번호로 전송됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 번호 전송 성공"),
            @ApiResponse(responseCode = "400", description = "인증 번호 전송 실패")
    })
    @PostMapping("/sendSmsProc")
    public ResponseEntity<Map<String, String>> sendSMSProc(
            @Parameter(description = "인증할 휴대폰 번호", required = true)
            @RequestParam(name = "phoneNumber") @NotBlank String phoneNumber){
        Map<String, String> response = new HashMap<>();

        log.info("휴대폰 번호로 인증 번호 전송 시도: {}", phoneNumber);

        if(verifyService.sendSMS(phoneNumber)){
            log.info("인증 번호 전송 성공");
            response.put("status", "success");
            response.put("message","인증번호가 성공적으로 전송 되었습니다.");
            return ResponseEntity.ok(response);
        }

        log.error("인증 번호 전송 실패");
        response.put("status", "error");
        response.put("message","인증번호 전송에 오류가 발생했습니다.");
        return ResponseEntity.badRequest().body(response);
    }

    @Operation(summary = "인증 코드 확인", description = "사용자가 입력한 인증 코드를 확인하고, " +
            "해당 번호로 회원가입이 가능한지 여부를 검사합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 인증 번호 또는 이미 가입된 회원")
    })
    @PostMapping("/checkProc")
    public ResponseEntity<Map<String, String>> checkProc(
            @Parameter(description = "입력된 인증 코드", required = true)
            @RequestParam @NotBlank String verifyCode,
            @Parameter(description = "휴대폰 번호", required = true)
            @RequestParam(name = "phoneNumber") String phoneNumber
    ){
        Map<String, String> response = new HashMap<>();

        log.info("인증 코드 확인 시도: {}", verifyCode);

        UserDTO userDTO = verifyService.verifyPhoneNum(phoneNumber);
        if(userDTO != null){
            log.error("이미 중복 된 회원");
            response.put("status", "error");
            response.put("message","이미 가입 되어있는 회원입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        if(verifyService.verifyCode(verifyCode)) {
            log.info("인증 성공");
            response.put("status", "success");
            response.put("message","인증 성공하였습니다. 회원가입 페이지로 이동합니다.");
            return ResponseEntity.ok(response);
        }

        log.error("인증 실패: 잘못된 인증 번호");
        response.put("status", "error");
        response.put("message","인증번호가 틀렸습니다. 다시 입력하게 화면을 새로고침합니다.");
        return ResponseEntity.badRequest().body(response);

    }

}
