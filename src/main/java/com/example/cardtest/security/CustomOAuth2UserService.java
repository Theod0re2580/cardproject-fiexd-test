package com.example.cardtest.security;

import com.example.cardtest.domain.Member;
import com.example.cardtest.domain.Role;
import com.example.cardtest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        // 기본 OAuth2 정보 로드
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google, kakao, naver
        Map<String, Object> attributes = oauth2User.getAttributes();

        String providerId;
        String email = null;
        String name = null;

        /** ────────────────────────────────
         * GOOGLE
         * ──────────────────────────────── */
        if ("google".equals(provider)) {
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        /** ────────────────────────────────
         * KAKAO (email 없을 수 있음)
         * ──────────────────────────────── */
        else if ("kakao".equals(provider)) {
            providerId = String.valueOf(attributes.get("id"));

            Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = account != null
                    ? (Map<String, Object>) account.get("profile")
                    : null;

            // 이메일 존재 여부 확인
            if (account != null && account.containsKey("email")) {
                email = (String) account.get("email");
            }

            name = profile != null ? (String) profile.get("nickname") : "카카오사용자";

            // 🔥 비즈 앱이 아니면 email이 절대 없음 → 대체 이메일 생성
            if (email == null || email.isBlank()) {
                email = "kakao_" + providerId + "@kakao-temp.com";
            }
        }

        /** ────────────────────────────────
         * NAVER
         * ──────────────────────────────── */
        else if ("naver".equals(provider)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            providerId = (String) response.get("id");
            email = (String) response.get("email");
            name = (String) response.get("name");
        }

        /** ────────────────────────────────
         * UNKNOWN
         * ──────────────────────────────── */
        else {
            throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        }

        // DB에서 찾거나 새로 생성
        Member member = findOrCreateUser(provider, providerId, email, name);

        return new CustomUserDetails(member, attributes);
    }

    /** ─────────────────────────────────────────
     * 기존 계정 있으면 로그인, 없으면 새로 생성
     * ───────────────────────────────────────── */
    private Member findOrCreateUser(String provider, String providerId, String email, String name) {

        // email 기준으로 동일 유저 찾기
        Optional<Member> existing = memberRepository.findByEmail(email);

        if (existing.isPresent()) {
            return existing.get();
        }

        // 신규 로그인 ID
        String loginId = provider + "_" + providerId;

        // 랜덤 패스워드 (null 금지)
        String randomPw = passwordEncoder.encode(UUID.randomUUID().toString());

        Member newMember = Member.builder()
                .loginId(loginId)
                .password(randomPw)
                .name(name)
                .email(email)
                .birth(LocalDate.of(2000, 1, 1))  // 기본값
                .role(Role.USER)
                .build();

        return memberRepository.save(newMember);
    }
}
