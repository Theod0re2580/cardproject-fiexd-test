package com.example.cardtest.web;

import com.example.cardtest.domain.EventView;
import com.example.cardtest.service.EventSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventSearchService service;

    /** 브랜드 선택 페이지 */
    @GetMapping
    public String brands(Model model) {
        model.addAttribute("brands", service.getBrands());
        return "events/brands";
    }

    /** 특정 브랜드 카드 리스트 */
    @GetMapping("/brand")
    public String eventsByBrand(
            @RequestParam(required = false) String brand,
            Model model
    ) {
        List<EventView> events = service.search(null, brand);

        model.addAttribute("events", events);
        model.addAttribute("brand", brand);

        return "events/list";
    }

    /** 인기 카드 TOP 10 */
    @GetMapping("/top")
    public String topCards(Model model) {

        List<EventView> top = service.getTop10Cards();
        model.addAttribute("events", top);

        return "events/top";
    }

    /** 혜택별 추천 */
    @GetMapping("/benefits")
    public String benefitCards(
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        List<EventView> list = service.searchByBenefit(keyword);

        model.addAttribute("events", list);
        model.addAttribute("keyword", keyword);

        return "events/benefits";
    }

    /** 랜덤 카드 3개 추천 */
    @GetMapping("/random")
    public String randomCards(Model model) {

        List<EventView> all = service.getAllEvents();

        if (all != null && all.size() > 3) {
            Collections.shuffle(all);
        }

        List<EventView> randomThree = all.stream()
                .limit(3)
                .toList();

        model.addAttribute("randoms", randomThree);
        model.addAttribute("date", LocalDate.now());

        return "events/random";
    }

    /** MBTI 테스트 페이지 */
    @GetMapping("/mbti")
    public String mbtiTest() {
        return "events/mbti-test";
    }

    /** MBTI 결과 페이지 */
    @PostMapping("/mbti/result")
    public String mbtiResult(
            @RequestParam String mbti,
            Model model
    ) {

        List<EventView> cards = service.getCardsByMbti(mbti);

        model.addAttribute("mbti", mbti);
        model.addAttribute("cards", cards);

        return "events/mbti-result";
    }

    @GetMapping("/list")
    public String listPage(
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        List<EventView> events;

        if (keyword == null || keyword.isBlank()) {
            events = service.getAllEvents();  // 전체 카드
        } else {
            events = service.search(keyword, null); // 키워드 검색
        }

        model.addAttribute("events", events);
        model.addAttribute("brands", service.getBrands());
        model.addAttribute("keyword", keyword);

        return "events/list";  // 🔥 이 파일이 렌더링됨
    }
}
