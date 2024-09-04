package com.project.FreeCycle.Service;

import com.project.FreeCycle.Api.CoolSMSApi;
import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Random;

@Slf4j
@Service
public class VerifyService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Autowired
    private HttpSession session;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private CoolSMSApi coolSMSApi;

    // 가입 되어있는지 회원인지 확인
    public boolean existUser(String userId, String eamil){

        User user = userRepository.findByUserId(userId);
        if(user != null){
            if(user.getEmail().equals(eamil)){
                return true;
            }
        }
        return false;
    }
    
    // 메일 전송
    public boolean sendEmail(String email) {
        String code = generateCode();

        String subject = "인증번호 안내";
        String content = " 안녕하세요, 줄랑입니다. \n\n 인증번호는 " + code + "입니다. " +
                "\\n\\n이 인증번호를 사용하여 인증을 완료해주세요";

        try {
            sendEmailMessage(email, subject, content);
            session.setAttribute("authCode", code);
            session.setMaxInactiveInterval(300);  // 세션 만료 시간 5분
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 문자 전송
    public boolean sendSMS(String phone){
        String code = generateCode();
        String apikey = coolSMSApi.getApikey();
        String apiSecret = coolSMSApi.getApiSecret();
        String content = "주랑 {인증번호}" + code + "를 입력해주세요. ";


        if(sendPhoneMessage(phone,content, apikey, apiSecret)) {
            session.setAttribute("authCode", code);
            session.setMaxInactiveInterval(300);
            return true;
        }
        return false;
    }

    // 문자 보낼 메시지 전송
    public boolean sendPhoneMessage(String phoneNumber,String content,String apiKey, String apiSecret){
        // 메시지 전송 로직 구현
        Message coolsms = new Message(apiKey,apiSecret);

        HashMap<String, String> params = new HashMap<>();
        params.put("to", phoneNumber);
        params.put("from", "01056563642");
        params.put("type","SMS");
        params.put("text", content);

        try {
            JSONObject result = coolsms.send(params);
            System.out.println(result.toString());
            return true;
        } catch (CoolsmsException e) {
            System.out.println("문자 전송 실패" + e.getMessage());
            return false;
        }
    }

    // 인증번호 생성
    public String generateCode(){
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 이메일 보낼 메시지 전송
    public void sendEmailMessage(String email, String subject, String content) throws MessagingException {
        MimeMessage message =mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,false, "UTF-8");

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    // 코드 인증
    public boolean verifyCode(String code){
        String authCode = (String) session.getAttribute("authCode");
        if (authCode != null && authCode.equals(code)) {
            session.removeAttribute("authCode");
            return true;
        }
        return false;
    }

    // 비밀번호 업데이트
    @Transactional
    public boolean updatePassword(String NewPassword, String userId){
        log.info("비밀번호 업데이트 요청: userId={}, newPassword={}", userId, NewPassword);
        User user = userRepository.findByUserId(userId);

        if(user != null) {
            log.info("사용자 정보가 확인되었습니다: userId={}", userId);
            if (bCryptPasswordEncoder.matches(NewPassword, user.getPassword())) {
                log.warn("새 비밀번호가 현재 비밀번호와 동일합니다: userId={}", userId);
                System.out.println("새 비밀번호가 현재 비밀번호와 동일합니다. = userId=" + userId);
                return false;
            }
            // 비밀번호 업데이트 할 때 새로 업데이트 할 비밀번호가 현재 비밀번호와 같으면 실패 반환
            try {
                user.setPassword(bCryptPasswordEncoder.encode(NewPassword));  // 비밀번호 암호화
                userRepository.save(user);
                log.info("비밀번호가 성공적으로 업데이트되었습니다: userId={}", userId);
                return true;
            } catch (Exception e) {
                log.error("비밀번호 업데이트 중 예외 발생: userId={}, error={}", userId, e.getMessage());
                return false;
            }
        }
        return false;
    }

    // 비밀번호 비교
    public boolean checkPassword(String newPassword, String confirmPassword){

        if(newPassword.equals(confirmPassword)){
            return true;
        }
        return false;
    }

}
