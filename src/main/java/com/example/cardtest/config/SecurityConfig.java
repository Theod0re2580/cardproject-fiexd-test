package com.example.cardtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CSRF는 일단 비활성화 (API/AJAX 많으면 편함, 나중에 켜도 됨)
                .csrf(csrf -> csrf.disable())

                // 🔐 인가(권한) 규칙
                .authorizeHttpRequests(auth -> auth
                        // 누구나 접근 가능
                        .requestMatchers("/", "/css/**", "/js/**", "/img/**", "/images/**").permitAll()
                        .requestMatchers("/member/signup", "/member/login", "/member/check-email").permitAll()
                        // 관리자만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 그 외 /member/** 는 로그인 필요
                        .requestMatchers("/member/**").authenticated()
                        // 나머지는 일단 다 허용
                        .anyRequest().permitAll()
                )

                // 🔑 폼 로그인 설정
                .formLogin(form -> form
                        .loginPage("/member/login")             // GET 로그인 페이지
                        .loginProcessingUrl("/member/login")    // POST 로그인 처리 (컨트롤러 X)
                        .usernameParameter("loginId")           // form name="loginId"
                        .passwordParameter("password")          // form name="password"
                        .defaultSuccessUrl("/", true)           // 로그인 성공 시
                        .failureUrl("/member/login?error=true") // 실패 시
                        .permitAll()
                )

                // 🚪 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/member/logout")            // POST /member/logout
                        .logoutSuccessUrl("/")                  // 로그아웃 후
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
