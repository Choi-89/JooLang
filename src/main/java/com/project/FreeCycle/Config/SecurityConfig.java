package com.project.FreeCycle.Config;

import com.project.FreeCycle.Repository.UserRepository;
import com.project.FreeCycle.Service.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                                "/joinProc", "/loginProc").permitAll()
                        .anyRequest().authenticated()
                );

        http
                .formLogin((auth) -> auth
                        .usernameParameter("userId").passwordParameter("password")
                        .loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/home/login?error=true")
                        .permitAll()

                );

        http
                .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home/login")
                .permitAll()
                );

        http
                .csrf((csrf) -> csrf.disable());


        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new CustomUserDetailService(userRepository);
    }
}
