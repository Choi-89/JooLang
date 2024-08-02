package com.project.FreeCycle.Config;

import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/home/login","/home/join",
                                "/joinProc","/loginProc").permitAll()
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
                .permitAll()
                );

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
        authProvider.setUserDetailsService(userDetailsService(null));
        // 비밀번호를 비교할 때 사용할 입력된 비밀번호를 암호화 하여 비교함
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }


    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailService(userRepository);
    }
}
