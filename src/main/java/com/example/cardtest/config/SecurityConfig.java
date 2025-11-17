package com.example.cardtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/events/**",   // 🔥 MBTI 포함 전체 허용
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/mbti/**"      // 혹시 단독으로도 쓰면 대비
                        ).permitAll()
                        .anyRequest().permitAll() // 🔥 모든 요청 허용 (로그인 페이지 안뜨게)
                )
                .formLogin(form -> form.disable())   // 🔥 로그인창 완전 비활성화
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
