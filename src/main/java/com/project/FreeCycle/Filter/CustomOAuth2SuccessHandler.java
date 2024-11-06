package com.project.FreeCycle.Filter;

import com.project.FreeCycle.Domain.RefreshEntity;
import com.project.FreeCycle.Dto.CustomUserDetail;
import com.project.FreeCycle.Repository.RefreshRepository;
import com.project.FreeCycle.Util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final Long accessMs = 60 * 60 * 1000L;  // 1시간
    private final Long refreshMs = 24 * 60 * 60 * 1000L;    // 24시간

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    /**
     * processOAuth2User에서 생성된 Refresh Token이 이미 존재한다면
     * 재생성하지 않고 그대로 사용하게 하여,
     * 중복 생성되지 않도록 합니다.
     * */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();

        String userId = customUserDetail.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

//        String token = jwtUtil.createJwt(null,userId,role, expiredMs);

        // 세션에서 신규 사용자 여부 확인
        HttpSession session = request.getSession(false);
        String refresh = session != null ? (String) session.getAttribute("refresh") : null;

        if (refresh == null) {
            refresh = jwtUtil.createJwt("refresh", userId, role, refreshMs);

            //Refresh 토큰 저장
            addRefreshEntity(userId, refresh, refreshMs);
            response.addCookie(createCookie("refresh", refresh));
        }


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

    private void addRefreshEntity(String userId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUserId(userId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

}