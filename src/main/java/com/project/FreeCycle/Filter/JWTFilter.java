package com.project.FreeCycle.Filter;

import com.project.FreeCycle.Domain.User;
import com.project.FreeCycle.Dto.CustomUserDetail;
import com.project.FreeCycle.Util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.io.PrintWriter;
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

//        String authorization = request.getHeader("Authorization");
        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 헤더에 Authorization 정보가 없을 경우 쿠키에서 토큰 검색
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = getTokenFromCookies(request.getCookies());
            if (accessToken != null) {
                // 쿠키에서 가져온 토큰을 Authorization 헤더로 설정
                response.setHeader("Authorization", "Bearer " + accessToken);
            }
        }

        // 최종적으로 Authorization이 null일 경우 필터 진행 후 종료
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String category = jwtUtil.getCategory(accessToken);

        // 엑세스 토큰인지 검증
        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 헤더에 Auth 정보가 없을 경우 쿠키에서 토큰 검색
        String token = accessToken.replace("Bearer ", "");

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

        // 스프링이 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}



/**
 *  Authorization 정보를 클라이언트가 사용해야 할 경우에는 response.setHeader를 사용하고, 서버에서만 참조할 경우에는 request.setAttribute가 적합
 * */