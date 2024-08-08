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
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

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
                                "/static/**","/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/report").hasRole("USER")
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
                            log.info("OAuth2 UserService 설정됨");
                            userInfo.userService(customOauth2UserService);
                        })
                        .successHandler((request, response, authentication) -> {
                            log.info("OAuth2 로그인 성공: " + authentication.getName());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
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


        http
        .sessionManagement(session -> session
                .sessionFixation().newSession() // 세션 고정 공격 방지
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
        )
        .securityContext(securityContext -> securityContext
                .securityContextRepository(httpSessionSecurityContextRepository())
        );


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



    // 세션 고정 공격 방지를 위한 세션 인증 전략 설정
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    @Bean
    public HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

//    // 세션 이벤트를 처리하기 위한 HttpSessionEventPublisher를 빈으로 등록
//    @Bean
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }


}
