package com.example.cardtest.web;

import com.example.cardtest.domain.Card;
import com.example.cardtest.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cards")
public class AdminCardController {

    private final CardService cardService;

    /** 카드 목록 + 등록 폼 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("cards", cardService.findAll());
        model.addAttribute("card", new Card());
        return "admin/cards/list";
    }

    /** 카드 등록 */
    @PostMapping
    public String add(@ModelAttribute Card card) {
        cardService.add(card);
        return "redirect:/admin/cards";
    }

    /** 카드 추가 폼 */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("card", new Card());
        return "admin/cards/add";
    }


    /** 카드 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("card", cardService.findById(id));
        return "admin/cards/edit";
    }

    /** 카드 수정 처리 */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute Card card) {
        cardService.update(id, card);
        return "redirect:/admin/cards";
    }

    /** 카드 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        cardService.delete(id);
        return "redirect:/admin/cards";
    }

    /** 카드 상세 */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("card", cardService.findById(id));
        return "admin/cards/detail";
    }

    /** 카드 검색 */
    @GetMapping("/search")
    @ResponseBody
    public List<Card> searchCards(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return cardService.search(keyword);
    }
}
