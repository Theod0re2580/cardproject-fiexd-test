package com.example.cardtest.service;

import com.example.cardtest.domain.Member;
import com.example.cardtest.domain.Role;
import com.example.cardtest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .orElse(null);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + id));
    }

    public void signup(Member member) {
        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRole(Role.USER); // 기본 USER
        memberRepository.save(member);
    }

    public void update(Long id, Member updateDto) {
        Member member = findById(id);
        member.setName(updateDto.getName());
        member.setEmail(updateDto.getEmail());
        member.setBirth(updateDto.getBirth());
        memberRepository.save(member);
    }

    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    /** 관리자용 전체 조회 */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /** 관리자용 권한 변경 */
    public void updateRole(Long id, Role role) {
        Member member = findById(id);
        member.setRole(role);
        memberRepository.save(member);
    }
}
