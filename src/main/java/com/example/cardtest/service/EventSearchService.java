package com.example.cardtest.service;

import com.example.cardtest.domain.Benefit;
import com.example.cardtest.domain.Card;
import com.example.cardtest.domain.EventView;
import com.example.cardtest.repository.CardRepository;
import com.example.cardtest.repository.BenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventSearchService {

    private final CardRepository cardRepository;
    private final BenefitRepository benefitRepository;

    /* ===========================================================
       ⭐ 상위 카테고리 매핑
    =========================================================== */
    private static final Map<String, String> CATEGORY_MAP = Map.ofEntries(
            Map.entry("커피", "카페·디저트"),
            Map.entry("카페", "카페·디저트"),
            Map.entry("디저트", "카페·디저트"),

            Map.entry("편의점", "편의점"),

            Map.entry("배달", "배달앱"),
            Map.entry("배달앱", "배달앱"),

            Map.entry("영화", "문화·영화"),
            Map.entry("문화", "문화·영화"),
            Map.entry("도서", "문화·영화"),

            Map.entry("마트", "마트/식료품"),
            Map.entry("식료품", "마트/식료품"),

            Map.entry("교통", "교통"),
            Map.entry("버스", "교통"),
            Map.entry("지하철", "교통"),

            Map.entry("주유", "주유"),
            Map.entry("주유소", "주유"),

            Map.entry("통신", "통신비"),
            Map.entry("휴대폰", "통신비"),

            Map.entry("쇼핑", "온라인쇼핑"),
            Map.entry("이커머스", "온라인쇼핑"),

            Map.entry("구독", "구독/스트리밍"),
            Map.entry("스트리밍", "구독/스트리밍")
    );

    /* ===========================================================
       ⭐ DB → EventView 변환
       🔥 Card.id / Benefit.cardId 둘 다 String 으로 강제 통일
    =========================================================== */
    private List<EventView> buildEventViews() {

        List<Card> cards = cardRepository.findAll();
        List<Benefit> benefits = benefitRepository.findAll();

        // KEY = Long (card.id)
        Map<Long, EventView> map = new HashMap<>();

        // 카드 등록
        for (Card card : cards) {
            map.put(card.getId(), new EventView(card));
        }

        // 혜택 연결
        for (Benefit b : benefits) {
            Long bCardId = b.getCardId();
            if (bCardId == null) continue;

            EventView ev = map.get(bCardId);
            if (ev != null) {
                ev.addBenefit(b);
            }
        }

        List<EventView> list = new ArrayList<>(map.values());

        // 카테고리/JSON 생성
        list.forEach(ev -> ev.finalizeBenefits(CATEGORY_MAP));

        return list;
    }

    /* ===========================================================
       🔍 검색
    =========================================================== */
    public List<EventView> search(String benefit, String brand) {

        List<EventView> events = buildEventViews();
        Stream<EventView> stream = events.stream();

        // 혜택 검색
        if (benefit != null && !benefit.isBlank()) {
            String q = benefit.toLowerCase();
            stream = stream.filter(ev ->
                    ev.getBenefits().stream().anyMatch(b ->
                            containsIgnoreCase(b.getBnfName(), q) ||
                                    containsIgnoreCase(b.getBnfContent(), q) ||
                                    containsIgnoreCase(b.getBnfDetail(), q)
                    )
            );
        }

        // 브랜드 검색
        if (brand != null && !brand.isBlank()) {
            String q = brand.toLowerCase();
            stream = stream.filter(ev ->
                    ev.getCard().getCardBrand() != null &&
                            ev.getCard().getCardBrand().equalsIgnoreCase(q)
            );
        }

        return stream.toList();
    }

    public List<EventView> searchByBenefit(String keyword) {
        return search(keyword, null);
    }

    /* ===========================================================
       ⭐ TOP 10
    =========================================================== */
    public List<EventView> getTop10Cards() {
        List<EventView> list = buildEventViews();

        list.sort(Comparator.comparingInt(EventView::getRecord).reversed());
        return list.stream().limit(10).toList();
    }

    /* ===========================================================
       ⭐ 브랜드 리스트
    =========================================================== */
    public List<String> getBrands() {
        return buildEventViews().stream()
                .map(ev -> ev.getCard().getCardBrand())
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    /* ===========================================================
       ⭐ MBTI 추천
    =========================================================== */
    public List<EventView> getCardsByMbti(String mbti) {

        List<EventView> list = buildEventViews();

        return switch (mbti) {
            case "ENFP", "ESFP" -> filterByKeyword(list, "할인", "적립", "혜택");
            case "ISTJ", "ISFJ" -> filterByKeyword(list, "캐시백");
            case "ENTJ", "INTJ" -> filterByKeyword(list, "포인트");
            case "ESTJ", "ESTP" -> filterByKeyword(list, "교통", "주유", "버스");
            case "INFP", "INFJ" -> filterByKeyword(list, "카페", "문화", "영화", "커피");
            default -> list.stream().limit(10).toList();
        };
    }

    private List<EventView> filterByKeyword(List<EventView> list, String... keywords) {
        return list.stream().filter(ev ->
                ev.getBenefits().stream().anyMatch(b ->
                        Arrays.stream(keywords).anyMatch(k ->
                                containsIgnoreCase(b.getBnfContent(), k) ||
                                        containsIgnoreCase(b.getBnfDetail(), k)
                        )
                )
        ).toList();
    }

    /* ===========================================================
       ⭐ 전체
    =========================================================== */
    public List<EventView> getAllEvents() {
        return buildEventViews();
    }

    /* ===========================================================
       ⭐ util
    =========================================================== */
    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null &&
                text.toLowerCase().contains(keyword);
    }
}
