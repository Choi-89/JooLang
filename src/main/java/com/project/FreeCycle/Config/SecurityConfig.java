package com.project.FreeCycle.Config;

import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.CustomOauth2UserService;
import com.project.FreeCycle.Service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService(userRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/home/login","/home/join",
                                "/joinProc","/loginProc","/auth/**","/error",
                                "/static/**","/favicon.ico","/certifyUser","/certifyUserProc",
                                "/verifyCode","/verifyCodeProc","/sendCodeProc",
                                "/editPassword","/updatePasswordProc").permitAll()
                        .requestMatchers("/postlist","/post/**","post_detail/**").hasRole("USER")
                        .anyRequest().authenticated()
                );

        http
                .formLogin((auth) -> auth
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .loginPage("/home/login")
                        .loginProcessingUrl("/loginProc")
                        .successHandler(new SimpleUrlAuthenticationSuccessHandler("/home_user"))
                        .failureUrl("/home/login?error=true")
                        .permitAll()
                );

        http
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true) // 세션 무효화
                        .permitAll()
                );

        http
                .oauth2Login(oauth -> oauth
                        .loginPage("/home/login")
                        .userInfoEndpoint(userInfo -> {
                            try {
                                log.info("OAuth2 UserService 설정 시도 중...");
                                userInfo.userService(customOauth2UserService);
                                log.info("OAuth2 UserService 설정됨");
                            } catch (Exception e) {
                                log.error("OAuth2 UserService 설정 중 오류 발생: ", e);
                            }
                        })
                        .successHandler((request, response, authentication) -> {
                            log.info("OAuth2 로그인 성공: " + authentication.getName());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("SecurityContext에 저장된 인증 정보: " + SecurityContextHolder.getContext().getAuthentication());
                            new SimpleUrlAuthenticationSuccessHandler("/home_user").onAuthenticationSuccess(request, response, authentication);
                        })
                        .failureHandler((request, response, exception) -> {
                            log.error("OAuth2 로그인 실패: " + exception.getMessage());
                            response.sendRedirect("/home/login?error=true");
                        })
        );

        http
                .exceptionHandling(exception -> {
                    log.info("Configuring exceptionHandling");
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        log.error("권한 인증 에러 error: {}", authException.getMessage());
                        response.sendRedirect("/home/login?error=true");
                    });
                });

        http
                .csrf((csrf) -> csrf.disable());



        return http.build();
    }

    // 사용자 정보를 데이터베이스에서 조회하고,
    // 사용자가 입력한 비밀번호가 데이터베이스에 저장된 비밀번호와 일치하는지 확인하는 데 사용
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // 사용자 정보를 데이터베이스에서 조회할 수 있게 함
        authProvider.setUserDetailsService(userDetailsService());
        // 비밀번호를 비교할 때 사용할 입력된 비밀번호를 암호화 하여 비교함
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
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
