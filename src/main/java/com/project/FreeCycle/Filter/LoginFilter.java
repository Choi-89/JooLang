package com.project.FreeCycle.Filter;

import com.project.FreeCycle.Domain.RefreshEntity;
import com.project.FreeCycle.Repository.RefreshRepository;
import com.project.FreeCycle.Util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Long accessMs = 60 * 60 * 1000L;  // 1시간
    private final Long refreshMs = 24 * 60 * 60 * 1000L;    // 24시간
    private RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
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


//        CustomUserDetail customUserDetail = (CustomUserDetail) authResult.getPrincipal();
//
//        String userId = customUserDetail.getUsername();

        String userId = authResult.getName();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String access = jwtUtil.createJwt("access",userId, role, accessMs);
        String refresh = jwtUtil.createJwt("refresh",userId, role, refreshMs);

        //Refresh 토큰 저장
        addRefreshEntity(userId, refresh, refreshMs);

        // 응답설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
        
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        response.setStatus(401);

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); // 쿠키가 살아있을 시간 ( 1시간 )
//        cookie.setSecure(true);  //https 일 경우 주석 삭제
//        cookie.setPath("/");  // 쿠키가 적용 될 범위
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
