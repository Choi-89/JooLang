package com.project.FreeCycle.Controller;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.JoinRequestDTO;
import com.project.FreeCycle.Dto.LocationDTO;
import com.project.FreeCycle.Dto.UserDTO;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import com.project.FreeCycle.Service.LocationService;
import com.project.FreeCycle.Service.UserService;
import com.project.FreeCycle.Service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


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

    @Operation(summary = "회원가입 유형", description = "회원가입을 어떤 방식으로 할지 고르는 페이지로 이동합니다.")
    @GetMapping("/joinList")
    public ResponseEntity<String> joinList(){
//        return "joinList";
        return ResponseEntity.ok("{\"message\": \"회원가입 유형 선택 페이지로 이동\"}");
    }

    @Operation(summary = "회원가입 페이지", description = "회원가입 페이지로 이동합니다.")
    @GetMapping("/join")
    public ResponseEntity<String> ShowJoin(){
//        return "join";
        return ResponseEntity.ok("{\"message\": \"회원가입 페이지로 이동\"}");
    }


    @Operation(summary = "회원가입 처리", description = "회원 정보를 받아 회원가입을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/joinProc")
    public ResponseEntity<String> JoinProc(@RequestBody JoinRequestDTO joinRequestDTO) {

        try{

            UserDTO userDTO = joinRequestDTO.getUserDTO();
            LocationDTO locationDTO = joinRequestDTO.getLocationDTO();

            log.info("회원가입 요청: userId={}, email={}, name={}, phoneNum={}",
                    userDTO.getUserId(), userDTO.getEmail(), userDTO.getUsername(), userDTO.getPhoneNum());

            User savedUser = userService.saveUser(userDTO);
            log.info("User 저장 완료: {}", savedUser);

            locationService.LocationSave(locationDTO, savedUser);
            log.info("Location 정보 저장 완료");

            return ResponseEntity.ok("User registered successfully");

        } catch (IllegalArgumentException e){
            log.error("회원가입 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid data: " + e.getMessage());
        } catch (Exception e){
            log.error("회원가입 중 알 수 없는 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during registration");
        }
    }

    // OAuth2 방식으로 처음 로그인 시도 할 시 비밀번호 업데이트 함.
    @Operation(summary = "OAuth2 비밀번호 설정 페이지", description = "OAuth2 로그인 시 비밀번호 설정 페이지로 이동합니다.")
    @GetMapping("/joinPassword")
    public ResponseEntity<String> joinPassword(){
        return ResponseEntity.ok("{\"message\": \"OAuth2 비밀번호 설정 페이지\"}");
    }


    @Operation(summary = "OAuth2 비밀번호 설정 처리", description = "OAuth2 회원의 비밀번호를 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 설정 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 불일치"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/joinPasswordProc")
    public ResponseEntity<String> joinPasswordProc(
            @Parameter(description = "새 비밀번호", required = true) @RequestParam(name = "newPassword") String password,
            @Parameter(description = "비밀번호 확인", required = true) @RequestParam(name = "confirmPassword") String passwordConfirm
            ,HttpSession session){
        String userId = (String) session.getAttribute("userId");
        if(userId == null) {
            log.error("세션에 userId가 없습니다.");
//            return "joinPassword";
            return ResponseEntity.badRequest().body("Session does not contain userId");

        }

        log.info("비밀번호 설정 요청 : userId = {}, newPassword = {}, confirmPassword = {}", userId, password, passwordConfirm);
        if(verifyService.checkPassword(password,passwordConfirm)){
            log.info("비밀번호가 일치합니다.");
            if(verifyService.updatePassword(password, userId)) {
                log.info("비밀번호가 성공적으로 설정되었습니다: userId={}", userId);
                session.removeAttribute("userId");
//                return "redirect:/";
                return ResponseEntity.ok("Password updated successfully");
            } else{
                log.error("비밀번호 설정 중 오류가 발생하였습니다. userId = {}", userId);
//                model.addAttribute("errorMsg","비밀번호가 업데이트 중 오류가 발생했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An error occurred while updating password");
            }
        } else{
            log.error("비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
//        return "joinPassword";
    }

}
