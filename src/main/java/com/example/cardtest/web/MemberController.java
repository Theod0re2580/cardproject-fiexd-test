package com.example.cardtest.web;

import com.example.cardtest.domain.Member;
import com.example.cardtest.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /** 로그인 폼 */
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    /** 로그인 처리 */
    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Member member = memberService.login(loginId, password);

        if (member == null) {
            model.addAttribute("msg", "아이디 또는 비밀번호가 틀렸습니다.");
            return "member/login";
        }

        session.setAttribute("loggedMember",
                new SessionMember(member.getId(), member.getLoginId(), member.getRole()));

        return "redirect:/";
    }

    /** 로그아웃 */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /** 회원가입 폼 */
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("member", new Member());
        return "member/signup";
    }

    /** 회원가입 처리 */
    @PostMapping("/signup")
    public String signup(@ModelAttribute Member member) {
        memberService.signup(member);
        return "redirect:/member/login";
    }

    /** 내 정보 보기 */
    @GetMapping("/info")
    public String info(HttpSession session, Model model) {
        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");
        Member member = memberService.findById(logged.getId());
        model.addAttribute("member", member);
        return "member/info";
    }

    /** 내 정보 수정 */
    @PostMapping("/info")
    public String update(@ModelAttribute Member member, HttpSession session) {
        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");
        memberService.update(logged.getId(), member);
        return "redirect:/member/info";
    }

    /** 회원 탈퇴 (자기 자신) */
    @PostMapping("/delete")
    public String delete(HttpSession session) {
        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");
        memberService.delete(logged.getId());
        session.invalidate();
        return "redirect:/";
    }
}
