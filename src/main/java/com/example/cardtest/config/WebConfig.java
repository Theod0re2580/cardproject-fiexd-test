package com.example.cardtest.config;

import com.example.cardtest.web.interceptor.AdminCheckInterceptor;
import com.example.cardtest.web.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final AdminCheckInterceptor adminCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 로그인 필요 페이지
        registry.addInterceptor(loginCheckInterceptor)
                .addPathPatterns("/member/info", "/member/delete", "/admin/**")
                .excludePathPatterns("/member/login", "/member/signup", "/member/logout");

        // 관리자 전용 페이지
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**");
    }
}
