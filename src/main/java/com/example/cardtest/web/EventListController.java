package com.example.cardtest.web;

import com.example.cardtest.domain.Event;
import com.example.cardtest.service.EventListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/event")
public class EventListController {

    private final EventListService eventListService;

    public EventListController(EventListService eventListService) {
        this.eventListService = eventListService;
    }

    /**
     * 진행중 이벤트 리스트 + 검색 기능
     * 검색어(keyword)가 있으면 이벤트명에 포함된 이벤트만 보여줌
     */
    @GetMapping("/eventlist")
    public String eventList(@RequestParam(required = false) String keyword, Model model) {
        List<Event> events;
        if (keyword != null && !keyword.isBlank()) {
            events = eventListService.searchEvents(keyword);
        } else {
            events = eventListService.getOngoingEvents();
        }
        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword); // 검색창에 값 유지용
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        model.addAttribute("currentDate", today);
        return "events/eventlist";
    }

    /**
     * 특정 이벤트 상세
     */
    @GetMapping("/eventlistdetail/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        Event event = eventListService.getEventDetail(id);
        List<Event> relatedEvents = eventListService.getOngoingEvents(); // 진행중 이벤트 리스트
        model.addAttribute("event", event);
        model.addAttribute("relatedEvents", relatedEvents);
        return "events/eventlistdetail"; // src/main/resources/templates/events/eventlistdetail.html
    }
}
