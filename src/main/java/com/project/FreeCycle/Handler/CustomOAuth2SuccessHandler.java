package com.project.FreeCycle.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.FreeCycle.Dto.CustomUserDetail;
import com.project.FreeCycle.Util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final Long expiredMs = 60 * 60 * 1000L;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();

        String userId = customUserDetail.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String token = jwtUtil.createJwt(userId,role, expiredMs);
        log.info("JWT 토큰 생성: {}", token);

        response.addCookie(createCookie("Authorization", token));

        // 세션에서 신규 사용자 여부 확인
        HttpSession session = request.getSession(false);
        if (session != null && Boolean.TRUE.equals(session.getAttribute("isNewUser"))){
            session.removeAttribute("userId");
            // 새로운 사용자는 비밀번호 설정을 마친 후 다시 로그인 후 홈 화면으로 리다이렉트
            response.sendRedirect("http://localhost:8080/home_user"); // 프론트쪽 특정 URI
        } else{
            // 기존 사용자는 바로 홈 화면으로 리다이렉트
            response.sendRedirect("http://localhost:8080/home_user"); // 프론트쪽 특정 URI
        }
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60); // 쿠키가 살아있을 시간
        //cookie.setSecure(true);  //https 일 경우 주석 삭제
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
/**
Swagger UI 테스트 시: Swagger는 OAuth2 인증 후 리다이렉트 처리를 기대합니다. 따라서 리다이렉트 URI가 존재하는 경우, Swagger로 리다이렉트됩니다.
클라이언트 개발자가 API 요청 시 JSON 응답을 기대하는 경우, 리다이렉트 URI가 없으면 JSON 형식으로 응답을 반환하여 클라이언트가 이를 처리할 수 있습니다.
 */