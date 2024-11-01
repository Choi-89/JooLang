package com.project.FreeCycle.Config;

import com.project.FreeCycle.Handler.CustomOAuth2FailureHandler;
import com.project.FreeCycle.Handler.CustomOAuth2SuccessHandler;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.CustomOauth2UserService;
//import com.project.FreeCycle.Service.CustomUserDetailService;
import com.project.FreeCycle.Util.JWTFilter;
import com.project.FreeCycle.Util.JwtUtil;
import com.project.FreeCycle.Util.LoginFilter;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
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

    @Autowired
    public SecurityConfig(CustomOauth2UserService customOauth2UserService, CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
                          CustomOAuth2FailureHandler customOAuth2FailureHandler, JwtUtil jwtUtil, AuthenticationConfiguration authenticationConfiguration) {
        this.customOauth2UserService = customOauth2UserService;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.customOAuth2FailureHandler = customOAuth2FailureHandler;
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
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
                                "/editPassword","/updatePasswordProc",
                                "/v3/api-docs/**", "/swagger/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/postlist","/post/**","post_detail/**").hasRole("USER")
                        .anyRequest().authenticated()
                );

        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);
            // form 로그인 jwt 로직 커스텀화

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // 세션 무효화
                        .permitAll()
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
                                log.info("OAuth2 UserService 설정 시도 중...");
                                userInfo.userService(customOauth2UserService);
                                log.info("OAuth2 UserService 설정됨");
                            } catch (Exception e) {
                                log.error("OAuth2 UserService 설정 중 오류 발생: ", e);
                            }
                        })
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
        );


        http
                .addFilterAfter(new JWTFilter(jwtUtil), OAuth2LoginAuthenticationFilter.class);


        http
                .exceptionHandling(exception -> {
                    //log.info("Configuring exceptionHandling");
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        //log.error("권한 인증 에러 error: {}", authException.getMessage());
                        response.sendRedirect("/home/login?error=true");
                    });
                });

//        http
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
