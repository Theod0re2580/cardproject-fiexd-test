package com.example.cardtest.service;

import com.example.cardtest.domain.Member;
import com.example.cardtest.domain.Role;
import com.example.cardtest.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /** 로그인 */
    public Member login(String loginId, String password) {

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        return member;
    }

    /** ID 조회 */
    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. id=" + id));
    }

    /** 회원 가입 */
    public void signup(Member member) {

        if (memberRepository.findByLoginId(member.getLoginId()).isPresent())
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");

        if (memberRepository.findByEmail(member.getEmail()).isPresent())
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        member.setRole(Role.USER);

        memberRepository.save(member);
    }

    /** 정보 수정 */
    public void update(Long id, Member updateDto) {

        Member member = findById(id);

        if (updateDto.getName() != null && !updateDto.getName().isBlank())
            member.setName(updateDto.getName());

        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank())
            member.setEmail(updateDto.getEmail());

        if (updateDto.getBirth() != null)
            member.setBirth(updateDto.getBirth());

        memberRepository.save(member);
    }

    /** 삭제 */
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    /** ============================
     *  관리자용 전체 조회
     * ============================ */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /** ============================
     *  관리자용 회원 수정
     * ============================ */
    public void adminUpdate(Long id, String name, String email, String birth, Role role) {

        Member member = findById(id);

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름이 비어 있습니다.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일이 비어 있습니다.");
        }

        if (birth == null || birth.isBlank()) {
            throw new IllegalArgumentException("생년월일이 비어 있습니다.");
        }

        if (role == null) {
            throw new IllegalArgumentException("권한(Role)이 비어 있습니다.");
        }

        member.setName(name);
        member.setEmail(email);
        member.setBirth(LocalDate.parse(birth));
        member.setRole(role);

        memberRepository.save(member);
    }

    public boolean existsByEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    public boolean existsByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }
}
