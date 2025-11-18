package com.example.cardtest.web;

import com.example.cardtest.domain.Member;
import com.example.cardtest.domain.Role;
import com.example.cardtest.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    /** 회원 목록 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("members", memberService.findAll());
        return "admin/members/list";
    }

    /** 회원 등록 폼 */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/members/add";
    }

    /** 회원 등록 처리 */
    @PostMapping("/add")
    public String add(@RequestParam String loginId,
                      @RequestParam String password,
                      @RequestParam String name,
                      @RequestParam String email,
                      @RequestParam String birth,
                      @RequestParam Role role) {

        Member member = Member.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .birth(LocalDate.parse(birth))
                .role(role)
                .build();

        memberService.signup(member); // 비밀번호 암호화 + role 저장

        return "redirect:/admin/members";
    }

    /** 회원 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("member", memberService.findById(id));
        model.addAttribute("roles", Role.values());
        return "admin/members/edit";
    }

    /** 회원 수정 처리 */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @RequestParam String name,
                       @RequestParam String email,
                       @RequestParam String birth,
                       @RequestParam Role role) {

        memberService.adminUpdate(id, name, email, birth, role);

        return "redirect:/admin/members";
    }

    /** 회원 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        memberService.delete(id);
        return "redirect:/admin/members";
    }

    /** 회원 상세 정보 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("member", memberService.findById(id));
        return "admin/members/detail";
    }

    /** 중복체크 */
    @GetMapping("/check-loginId")
    @ResponseBody
    public boolean checkLoginId(@RequestParam String loginId) {
        return memberService.existsByLoginId(loginId);
    }

    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return memberService.existsByEmail(email);
    }
}
