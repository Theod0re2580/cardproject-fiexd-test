package com.example.cardtest.web;

import com.example.cardtest.service.BenefitService;
import com.example.cardtest.service.CardService;
import com.example.cardtest.service.EventListService;
import com.example.cardtest.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final CardService cardService;
    private final BenefitService benefitService;
    private final MemberService memberService;
    private final EventListService eventListService;

    @GetMapping
    public String adminHome(Model model) {
        return "admin/index";
    }

    @GetMapping("/cards/list")
    public String cardsList() {
        return "admin/cards/list";
    }

    @GetMapping("/benefits/list")
    public String benefitsList() {
        return "admin/benefits/list";
    }

    @GetMapping("/members/list")
    public String membersList() {
        return "admin/members/list";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("latestCards", cardService.findLatest(5));
        model.addAttribute("latestBenefits", benefitService.findLatest(5));
        model.addAttribute("latestMembers", memberService.findLatest(5));
        model.addAttribute("latestEvents", eventListService.findLatest(5));

        return "admin/dashboard";
    }
}
