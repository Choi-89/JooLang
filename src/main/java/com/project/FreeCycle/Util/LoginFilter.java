package com.project.FreeCycle.Util;

import com.project.FreeCycle.Dto.CustomUserDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Long expiredMs = 60 * 60 * 1000L;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

//        String username = obtainUsername(request);
        String userId = request.getParameter("userId");
        String password = obtainPassword(request);

        System.out.println("userId = " + userId);
        System.out.println("password = " + password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password, null);

        return authenticationManager.authenticate(authToken); // authToken 정보 바탕으로 authenticationManager 여기서 검증을 진행함

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        CustomUserDetail customUserDetail = (CustomUserDetail) authResult.getPrincipal();

        String userId = customUserDetail.getUsername();

        // authResult 객체로부터 role 값을 추출
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities(); 
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();  
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 추출한 role과 userId를 가지고 jwt 토큰 생성
        String token = jwtUtil.createJwt(userId, role, expiredMs);

        // HTTP 인증방식은 RFC 7235 정의에 따라 접두사 + 토큰 형식의 형태를 가져야함.
        response.addHeader("Authorization", "Bearer " + token);
        
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(401);

    }

}
