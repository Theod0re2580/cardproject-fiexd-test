package com.example.cardtest.web;

import com.example.cardtest.domain.Event;
import com.example.cardtest.service.EventListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/event")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventListService eventListService;

    /** 목록 + 검색 + 페이징 */
    @GetMapping
    public String eventList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<Event> eventPage =
                eventListService.searchAdminEventsPaged(keyword, pageable);

        int totalPages = eventPage.getTotalPages();
        int currentPage = page;

        // 🔥 페이지 블록 계산 (10개 단위)
        int blockSize = 10;
        int currentBlock = currentPage / blockSize;
        int startPage = currentBlock * blockSize;
        int endPage = Math.min(startPage + blockSize - 1, totalPages - 1);

        model.addAttribute("events", eventPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        // 🔥 HTML에서 사용하는 값들 추가
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/event/list";
    }


    /** 등록 페이지 */
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("event", new Event());
        return "admin/event/add";
    }

    /** 등록 처리 */
    @PostMapping("/add")
    public String addEvent(Event event) {
        eventListService.addEvent(event);
        return "redirect:/admin?tab=event";
    }

    /** 상세 */
    @GetMapping("/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventListService.findById(id));
        return "admin/event/detail";
    }

    /** 수정 페이지 */
    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("event", eventListService.findById(id));
        return "admin/event/edit";
    }

    /** 수정 처리 */
    @PostMapping("/{id}/edit")
    public String editEvent(@PathVariable Long id, Event event) {
        eventListService.updateEvent(id, event);
        return "redirect:/admin?tab=event";
    }

    /** 삭제 */
    @PostMapping("/{id}/delete")
    public String deleteEvent(@PathVariable Long id) {
        eventListService.deleteEvent(id);
        return "redirect:/admin?tab=event";
    }
}
