package com.project.FreeCycle.Config;

import com.project.FreeCycle.Filter.*;
import com.project.FreeCycle.Repository.RefreshRepository;
import com.project.FreeCycle.Service.CustomOauth2UserService;
//import com.project.FreeCycle.Service.CustomUserDetailService;
import com.project.FreeCycle.Util.JwtUtil;
import edu.emory.mathcs.backport.java.util.Collections;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshRepository refreshRepository;

    @Autowired
    public SecurityConfig(CustomOauth2UserService customOauth2UserService, CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
                          CustomOAuth2FailureHandler customOAuth2FailureHandler, JwtUtil jwtUtil, AuthenticationConfiguration authenticationConfiguration, RefreshRepository refreshRepository) {
        this.customOauth2UserService = customOauth2UserService;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.customOAuth2FailureHandler = customOAuth2FailureHandler;
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshRepository = refreshRepository;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();

                                config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                config.setAllowedMethods(Collections.singletonList("*")); // 허용할 메소드 Get ect on
                                config.setAllowCredentials(true);
                                config.setAllowedHeaders(Collections.singletonList("*"));
                                config.setMaxAge(3600L);

                                config.setExposedHeaders(Collections.singletonList("Authorization"));

                                return config;
                            }
                        }));
        http
                .csrf((csrf) -> csrf.disable());


        http
                .formLogin((formLogin) -> formLogin.disable());
        // 커스텀 로그인 API 사용

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/home/**","/loginProc","/auth/**","/error",
                                "/static/**","/favicon.ico","/certifyUser","/certifyUserProc",
                                "/verifyCode","/verifyCodeProc","/sendCodeProc",
                                "/editPassword","/updatePasswordProc","/auth/**",
                                "/v3/api-docs/**", "/swagger/**", "/swagger-ui/**","/reissue").permitAll()
                        .requestMatchers("/postlist","/post/**","post_detail/**").hasRole("USER")
                        .anyRequest().authenticated()
                );

//        http
//                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
            // form 로그인 jwt 로직 커스텀화
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);


        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );


        /**
         * 클라이언트 측에서 JWT 삭제:
         * 사용자가 로그아웃 버튼을 클릭하면, 클라이언트 측에서 JWT를 저장한 곳 (쿠키, localStorage 등)에서 해당 JWT를 삭제합니다.
         * */

        // oAuth2 방식
        http
                .oauth2Login((oauth) -> oauth
                        .userInfoEndpoint((userInfo) -> {
                            try {
                                userInfo.userService(customOauth2UserService);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
        );

        http
                .exceptionHandling(exception -> {
                    //log.info("Configuring exceptionHandling");
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        //log.error("권한 인증 에러 error: {}", authException.getMessage());
                        response.sendRedirect("/home/login?error=true");
                    });
                });

        return http.build();
    }

}
