package com.project.FreeCycle.Util;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.CustomUserDetail;
import com.project.FreeCycle.Dto.UserDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JWTFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars") ||
                path.startsWith("/loginProc") ||
                path.startsWith("/auth") ||
                path.startsWith("/static") ||
                path.startsWith("/certifyUser") ||
                path.startsWith("/verifyCode") ||
                path.startsWith("/sendCodeProc") ||
                path.startsWith("/home") ||
                path.startsWith("/editPassword") ||
                path.startsWith("/updatePasswordProc")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        // 헤더에 Auth 정보가 없을 경우 쿠키에서 토큰 검색

        // 헤더에 Authorization 정보가 없을 경우 쿠키에서 토큰 검색
        if (authorization == null || authorization.isEmpty()) {
            authorization = getTokenFromCookies(request.getCookies());
            if (authorization != null) {
                // 쿠키에서 가져온 토큰을 Authorization 헤더로 설정
                response.setHeader("Authorization", "Bearer " + authorization);
            }
        }

        // 최종적으로 Authorization이 null일 경우 필터 진행 후 종료
        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.replace("Bearer ", "");

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            SecurityContextHolder.clearContext(); // 세션 초기화
            filterChain.doFilter(request, response);
            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        setUpAuthentication(token);
        filterChain.doFilter(request, response);
    }

    // 쿠키에서 토큰을 가져오는 메서드
    private String getTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if ("Authorization".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void setUpAuthentication(String token) {
        String userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);

        User user = new User();  //user를 생성하여 값 set
        user.setUserId(userId);
        user.setRole(role);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("userId", userId);
        attributes.put("role", role);

        CustomUserDetail customOAuth2User = new CustomUserDetail(user, attributes); // UserDetails에 회원 정보 객체 담기

        // 스플이 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
