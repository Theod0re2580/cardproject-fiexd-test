package com.example.cardtest.web;

import com.example.cardtest.domain.Event;
import com.example.cardtest.service.EventListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/event")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventListService eventListService;

    /** 목록 + 검색 */
    @GetMapping
    public String eventList(@RequestParam(required = false) String keyword, Model model) {

        List<Event> events = eventListService.searchAdminEvents(keyword);
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);

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
