package com.example.cardtest.web;

import com.example.cardtest.domain.Member;
import com.example.cardtest.domain.SessionMember;
import com.example.cardtest.service.MemberService;
import com.example.cardtest.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /** 로그인 폼 (Spring Security가 로그인 처리) */
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @AuthenticationPrincipal CustomUserDetails principal,
                            Model model) {

        // 이미 로그인되어 있으면 홈으로
        if (principal != null) {
            return "redirect:/";
        }

        if (error != null) {
            model.addAttribute("msg", "아이디 또는 비밀번호가 잘못되었습니다.");
        }

        return "member/login";
    }

    /** 로그아웃은 Spring Security가 처리 (컨트롤러 메서드 삭제) */
    // @PostMapping("/logout") 필요 없음

    /** 회원가입 폼 */
    @GetMapping("/signup")
    public String signupForm(@AuthenticationPrincipal CustomUserDetails principal,
                             Model model) {

        // 로그인 상태면 굳이 가입 페이지 갈 필요 없음
        if (principal != null) {
            return "redirect:/";
        }

        model.addAttribute("member", new Member());
        return "member/signup";
    }

    /** 회원가입 처리 */
    @PostMapping("/signup")
    public String signup(@ModelAttribute Member member, Model model) {

        try {
            memberService.signup(member);
            return "redirect:/member/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("msg", e.getMessage());
            model.addAttribute("member", member);
            return "member/signup";
        }
    }

    /** 내 정보 보기 */
    @GetMapping("/mypage")
    public String info(@AuthenticationPrincipal CustomUserDetails principal,
                       Model model) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        Member member = memberService.findById(principal.getId());

        // 기존처럼 loggedMember 모델에 SessionMember 담아주기 (헤더 등에서 사용 시)
        SessionMember logged = new SessionMember(member.getId(), member.getLoginId(), member.getRole());

        model.addAttribute("loggedMember", logged);
        model.addAttribute("member", member);

        return "member/mypage";
    }

    /** 정보 수정 */
    @PostMapping("/mypage")
    public String update(@ModelAttribute Member updateDto,
                         @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        memberService.update(principal.getId(), updateDto);

        return "redirect:/member/mypage";
    }

    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails principal,
                         HttpSession session) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        Long loginUserId = principal.getId();

        // 🔥 1) DB에서 회원 삭제
        memberService.delete(loginUserId);

        // 🔥 2) 인증정보 삭제 + 세션 무효화 (일반/관리자 모두)
        SecurityContextHolder.clearContext();
        session.invalidate();

        // 🔥 3) 홈으로 이동 (완전 로그아웃 상태)
        return "redirect:/";
    }

    /** 정보 수정 페이지 이동 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails principal,
                           Model model) {

        if (principal == null) {
            return "redirect:/member/login";
        }

        // 본인만 접근 가능
        if (!principal.getId().equals(id)) {
            return "redirect:/";
        }

        Member member = memberService.findById(id);
        SessionMember logged = new SessionMember(member.getId(), member.getLoginId(), member.getRole());

        model.addAttribute("loggedMember", logged);
        model.addAttribute("member", member);

        return "member/edit";
    }

    /** 정보 + 비밀번호 수정 처리 */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam String email,
                       @RequestParam String birth,
                       @RequestParam(required = false) String oldPw,
                       @RequestParam(required = false) String newPw,
                       @AuthenticationPrincipal CustomUserDetails principal,
                       Model model) {

        if (principal == null) return "redirect:/member/login";
        if (!principal.getId().equals(id)) return "redirect:/";

        // ✨ 이메일 중복 검사 — 본인 제외
        Member current = memberService.findById(id);

        if (!current.getEmail().equals(email) && memberService.existsByEmail(email)) {
            model.addAttribute("msg", "이미 사용 중인 이메일입니다.");
            model.addAttribute("member", current);
            return "member/edit";
        }

        try {
            // A) 이름 / 이메일 / 생년월일 수정
            Member updateDto = new Member();
            updateDto.setName(name);
            updateDto.setEmail(email);
            updateDto.setBirth(LocalDate.parse(birth));

            memberService.update(id, updateDto);

            // B) 비밀번호 변경
            if (oldPw != null && !oldPw.isBlank() &&
                    newPw != null && !newPw.isBlank()) {
                memberService.changePassword(id, oldPw, newPw);
            }

            return "redirect:/member/mypage";

        } catch (IllegalArgumentException e) {
            model.addAttribute("msg", e.getMessage());
            model.addAttribute("member", current);
            return "member/edit";
        }
    }

    /** 이메일 중복 AJAX */
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return memberService.existsByEmail(email);
    }
}
