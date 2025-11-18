package com.example.cardtest.web;

import com.example.cardtest.domain.Member;
import com.example.cardtest.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    /** 로그인 폼 */
    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {

        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");
        model.addAttribute("loggedMember", logged);

        return "member/login";
    }

    /** 로그인 처리 (예외 처리 적용됨) */
    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        try {
            Member member = memberService.login(loginId, password);

            SessionMember sessionUser =
                    new SessionMember(member.getId(), member.getLoginId(), member.getRole());

            session.setAttribute("loggedMember", sessionUser);

            return "redirect:/";

        } catch (IllegalArgumentException e) {
            model.addAttribute("msg", e.getMessage());
            return "member/login";
        }
    }

    /** 로그아웃 */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /** 회원가입 폼 */
    @GetMapping("/signup")
    public String signupForm(Model model, HttpSession session) {

        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");
        model.addAttribute("loggedMember", logged);

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
    public String info(HttpSession session, Model model) {

        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");

        // 로그인 안했으면 로그인 페이지로
        if (logged == null) return "redirect:/member/login";

        Member member = memberService.findById(logged.getId());

        model.addAttribute("loggedMember", logged);
        model.addAttribute("member", member);

        return "member/mypage";
    }

    /** 정보 수정 */
    @PostMapping("/mypage")
    public String update(@ModelAttribute Member member, HttpSession session) {

        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");

        if (logged == null) return "redirect:/member/login";

        memberService.update(logged.getId(), member);

        return "redirect:/member/mypage";
    }

    /** 회원 삭제 */
    @PostMapping("/delete")
    public String delete(HttpSession session) {

        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");

        if (logged == null) return "redirect:/member/login";

        memberService.delete(logged.getId());
        session.invalidate();

        return "redirect:/";
    }

    /** 정보 수정 페이지 이동 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {

        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");

        // 로그인 검증
        if (logged == null) return "redirect:/member/login";

        // 본인만 접근 가능
        if (!logged.getId().equals(id)) return "redirect:/";

        Member member = memberService.findById(id);

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
                       HttpSession session,
                       Model model) {

        SessionMember logged = (SessionMember) session.getAttribute("loggedMember");
        if (logged == null) return "redirect:/member/login";
        if (!logged.getId().equals(id)) return "redirect:/";

        // ✨ 이메일 중복 검사 — 본인 제외
        Member current = memberService.findById(id);

        if (!current.getEmail().equals(email) && memberService.existsByEmail(email)) {
            model.addAttribute("msg", "이미 사용 중인 이메일입니다.");
            model.addAttribute("member", current);
            return "member/edit";
        }

        try {
            // ✨ A) 이름 / 이메일 / 생년월일 수정 → 기존 update() 사용
            Member updateDto = new Member();
            updateDto.setName(name);
            updateDto.setEmail(email);
            updateDto.setBirth(LocalDate.parse(birth));

            memberService.update(id, updateDto);

            // ✨ B) 비밀번호 변경
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
