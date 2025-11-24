package com.example.cardtest.web;

import com.example.cardtest.domain.Benefit;
import com.example.cardtest.service.BenefitService;
import com.example.cardtest.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/benefits")
public class AdminBenefitController {

    private final BenefitService benefitService;
    private final CardService cardService;

    /** 목록 */
    @GetMapping
    public String list(
            @RequestParam(required = false, name = "adminsearch") String adminsearch,
            Model model) {

        List<Benefit> benefits;

        if (adminsearch != null && !adminsearch.trim().isEmpty()) {
            benefits = benefitService.adminSearch(adminsearch);
        } else {
            benefits = benefitService.findAll();
        }

        model.addAttribute("benefits", benefits);
        model.addAttribute("adminsearch", adminsearch);

        return "admin/benefits/list";
    }

    /** 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("benefit", benefitService.findById(id));
        return "admin/benefits/detail";
    }

    /** 추가 폼 */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("benefit", new Benefit());
        model.addAttribute("cards", cardService.findAll());
        return "admin/benefits/add";
    }

    /** 추가 처리 */
    @PostMapping("/add")
    public String add(@RequestParam Long cardId,
                      @RequestParam String bnfName,
                      @RequestParam(required = false) String bnfContent,
                      @RequestParam(required = false) String bnfDetail) {

        Benefit benefit = new Benefit();
        benefit.setBnfName(bnfName);
        benefit.setBnfContent(bnfContent);
        benefit.setBnfDetail(bnfDetail);

        benefitService.add(cardId, benefit);

        return "redirect:/admin?tab=benefits";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("benefit", benefitService.findById(id));
        model.addAttribute("cards", cardService.findAll());
        return "admin/benefits/edit";
    }

    /** 수정 처리 */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute Benefit benefit) {
        benefitService.update(id, benefit);
        return "redirect:/admin?tab=benefits";
    }

    /** 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        benefitService.delete(id);
        return "redirect:/admin?tab=benefits";
    }
}
