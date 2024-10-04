package com.project.FreeCycle.Config;

import com.project.FreeCycle.Handler.CustomOAuth2FailureHandler;
import com.project.FreeCycle.Handler.CustomOAuth2SuccessHandler;
import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.CustomOauth2UserService;
//import com.project.FreeCycle.Service.CustomUserDetailService;
import com.project.FreeCycle.Util.JWTFilter;
import com.project.FreeCycle.Util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final UserRepository userRepository;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(CustomOauth2UserService customOauth2UserService, UserRepository userRepository, CustomOAuth2SuccessHandler customOAuth2SuccessHandler, CustomOAuth2FailureHandler customOAuth2FailureHandler, JwtUtil jwtUtil) {
        this.customOauth2UserService = customOauth2UserService;
        this.userRepository = userRepository;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.customOAuth2FailureHandler = customOAuth2FailureHandler;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/home/**","/loginProc","/auth/**","/error",
                                "/static/**","/favicon.ico","/certifyUser","/certifyUserProc",
                                "/verifyCode","/verifyCodeProc","/sendCodeProc",
                                "/editPassword","/updatePasswordProc",
                                "/v3/api-docs/**", "/swagger/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/postlist","/post/**","post_detail/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);


        http
                .formLogin((formLogin) -> formLogin.disable());

        // 커스텀 로그인 API 사용
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        http
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // 세션 무효화
                        .permitAll()
                );

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

        http
                .csrf((csrf) -> csrf.disable());

//        http
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")  // 모든 경로에 대해 CORS 설정 적용
//                        .allowedOrigins("http://localhost:3000")  // React 앱의 주소
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메소드
//                        .allowedHeaders("*")  // 모든 헤더 허용
//                        .allowCredentials(true);  // 자격 증명(쿠키, 인증 정보 등) 허용
//            }
//        };
//    }
}
