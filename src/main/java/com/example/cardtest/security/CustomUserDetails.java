package com.example.cardtest.security;

import com.example.cardtest.domain.Member;
import com.example.cardtest.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Member member;
    private Map<String, Object> attributes; // 소셜 로그인 시에만 채워짐

    // 🔹 일반 로그인용 생성자
    public CustomUserDetails(Member member) {
        this.member = member;
    }

    // 🔹 소셜 로그인용 생성자
    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    public Long getId() {
        return member.getId();
    }

    public String getLoginId() {
        return member.getLoginId();
    }

    public Role getRole() {
        return member.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = "ROLE_" + member.getRole().name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // ================== OAuth2User 구현부 ==================

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // thymeleaf에서 #authentication.name 등으로 쓸 때 사용
    @Override
    public String getName() {
        return member.getLoginId();
    }
}
