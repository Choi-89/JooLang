package com.project.FreeCycle.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.FreeCycle.Domain.RefreshEntity;
import com.project.FreeCycle.Dto.LoginRequestDTO;
import com.project.FreeCycle.Repository.RefreshRepository;
import com.project.FreeCycle.Util.CookieUtil;
import com.project.FreeCycle.Util.ExpiredTime;
import com.project.FreeCycle.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final Long accessMs = ExpiredTime.accessMs;  // 1시간
    private final Long refreshMs = ExpiredTime.refreshMs;    // 24시간
    private RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CookieUtil cookieUtil, RefreshRepository refreshRepository) {
        super.setFilterProcessesUrl("/home/login");
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();

        try {
            ObjectMapper mapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequestDTO = mapper.readValue(messageBody, LoginRequestDTO.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String userId = loginRequestDTO.getUserId();
        String password = loginRequestDTO.getPassword();

        System.out.println("userId = " + userId);
        System.out.println("password = " + password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password, null);

        return authenticationManager.authenticate(authToken); // authToken 정보 바탕으로 authenticationManager 여기서 검증을 진행함


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {


        String userId = authResult.getName();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access",userId, role, accessMs);
        String refresh = jwtUtil.createJwt("refresh",userId, role, refreshMs);
        System.out.println("로그인 성공");

        //Refresh 토큰 저장
        addRefreshEntity(userId, refresh, refreshMs);

        // 응답설정
        response.setHeader("access", access);
        response.addCookie(cookieUtil.createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("로그인 실패");
        response.setStatus(401);

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
