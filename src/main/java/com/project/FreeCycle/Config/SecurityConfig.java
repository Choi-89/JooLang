package com.project.FreeCycle.Config;

import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.CustomOauth2UserService;
import com.project.FreeCycle.Service.CustomUserDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final CustomOauth2UserService customOauth2UserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/home/login","/home/join",
                                "/joinProc","/loginProc","/auth/**","/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        http
                .formLogin((auth) -> auth
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .loginPage("/home/login")
                        .loginProcessingUrl("/loginProc")
                        .defaultSuccessUrl("/home_user", true)
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
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOauth2UserService))
                        .defaultSuccessUrl("/home/user", true)
                        .failureUrl("/home/login?error=true")
        );

        http
                .csrf((csrf) -> csrf.disable());

        http
                .exceptionHandling(exception -> {
                    log.info("Configuring exceptionHandling");
                    exception.authenticationEntryPoint((request, response, authException) -> {
                        log.error("권한 인증 에러 error: {}", authException.getMessage());
                        response.sendRedirect("/home/login?error=true");
                    });
                });
        return http.build();
    }

    // 사용자 정보를 데이터베이스에서 조회하고,
    // 사용자가 입력한 비밀번호가 데이터베이스에 저장된 비밀번호와 일치하는지 확인하는 데 사용
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // 사용자 정보를 데이터베이스에서 조회할 수 있게 함
        authProvider.setUserDetailsService(userDetailsService(null));
        // 비밀번호를 비교할 때 사용할 입력된 비밀번호를 암호화 하여 비교함
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

    // WebSecurityCustomizer 빈 설정 - 커스터마이즈된 HttpFirewall 설정 적용
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.httpFirewall(allowAllHttpFirewall());
    }



    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailService(userRepository);
    }

    // 커스터마이즈된 HttpFirewall 빈 설정
    @Bean
    public HttpFirewall allowAllHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true); // 세미콜론을 허용하도록 설정
        firewall.setAllowUrlEncodedSlash(true); // URL 인코딩된 슬래시를 허용하도록 설정
        firewall.setAllowUrlEncodedPercent(true); // URL 인코딩된 퍼센트를 허용하도록 설정
        firewall.setAllowBackSlash(true); // 백슬래시를 허용하도록 설정
        firewall.setAllowUrlEncodedPeriod(true); // URL 인코딩된 점을 허용하도록 설정
        firewall.setAllowUrlEncodedDoubleSlash(true); // URL 인코딩된 이중 슬래시를 허용하도록 설정
        return firewall;
    }

}
