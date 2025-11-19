package com.example.cardtest.init;

import com.example.cardtest.domain.Member;
import com.example.cardtest.domain.Role;
import com.example.cardtest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 🔥 1) 관리자 계정 생성
        String adminLoginId = "admin";
        if (memberRepository.findByLoginId(adminLoginId).isEmpty()) {
            Member admin = Member.builder()
                    .loginId(adminLoginId)
                    .password(passwordEncoder.encode("admin1234"))
                    .name("관리자")
                    .email("admin@example.com")
                    .birth(LocalDate.of(1990, 1, 1))
                    .role(Role.ADMIN)
                    .build();

            memberRepository.save(admin);
            System.out.println("⭐ 관리자 계정 생성 완료: admin / admin1234");
        } else {
            System.out.println("⭐ 관리자 계정 이미 존재함");
        }

        // 🔥 2) 일반 사용자 계정 생성
        String userLoginId = "user1";
        if (memberRepository.findByLoginId(userLoginId).isEmpty()) {
            Member user = Member.builder()
                    .loginId(userLoginId)
                    .password(passwordEncoder.encode("user1234"))
                    .name("일반유저")
                    .email("user@example.com")
                    .birth(LocalDate.of(2000, 1, 1))
                    .role(Role.USER)
                    .build();

            memberRepository.save(user);
            System.out.println("⭐ 일반 사용자 계정 생성 완료: user1 / user1234");
        } else {
            System.out.println("⭐ 일반 사용자 계정 이미 존재함");
        }
    }
}
