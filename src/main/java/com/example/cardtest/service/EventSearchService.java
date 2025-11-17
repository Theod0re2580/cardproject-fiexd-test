package com.example.cardtest.service;

import com.example.cardtest.domain.Benefit;
import com.example.cardtest.domain.Card;
import com.example.cardtest.domain.EventView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventSearchService {

    @Value("${app.csv.card}")
    private Resource cardCsv;

    @Value("${app.csv.benefit}")
    private Resource benefitCsv;

    private final Map<String, Card> cardMap = new HashMap<>();
    private final List<Benefit> benefits = new ArrayList<>();
    private final List<EventView> allEvents = new ArrayList<>();

    /* ===========================================================
       ⭐ 상위 카테고리 매핑 규칙
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
       INIT
    =========================================================== */
    @PostConstruct
    public void init() throws Exception {

        loadCardsCsv();
        loadBenefitsCsv();
        joinData();
        filterInvalidBenefits();
    }

    /* ===========================================================
       1. CSV LOADING
    =========================================================== */

    private void loadCardsCsv() throws Exception {
        List<String[]> rows = readCsv(cardCsv, 9);

        for (String[] arr : rows) {
            if (arr.length < 9) continue;

            Card c = new Card(
                    getSafe(arr, 0),
                    getSafe(arr, 1),
                    getSafe(arr, 2),
                    getSafe(arr, 3),
                    getSafe(arr, 4),
                    getSafe(arr, 5),
                    getSafe(arr, 6),
                    parseIntSafe(getSafe(arr, 7)),
                    getSafe(arr, 8)
            );

            cardMap.put(c.getId(), c);
        }
    }

    private void loadBenefitsCsv() throws Exception {
        List<String[]> rows = readCsv(benefitCsv, 5);

        for (String[] arr : rows) {
            if (arr.length < 5) continue;

            benefits.add(new Benefit(
                    getSafe(arr, 0),
                    getSafe(arr, 1),
                    getSafe(arr, 2),
                    getSafe(arr, 3),
                    getSafe(arr, 4)
            ));
        }
    }

    private List<String[]> readCsv(Resource resource, int expectedColumns) throws Exception {
        List<String[]> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            br.readLine(); // skip header

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {

                if (line.isBlank()) continue;

                buffer.append(line);
                String[] arr = splitCsv(buffer.toString());

                if (arr.length < expectedColumns) {
                    buffer.append("\n");
                    continue;
                }

                result.add(arr);
                buffer.setLength(0);
            }
        }

        return result;
    }

    /* ===========================================================
       2. JOIN CARD + BENEFITS
    =========================================================== */
    private void joinData() {

        Map<String, EventView> map = new HashMap<>();

        for (Benefit b : benefits) {
            Card card = cardMap.get(b.getCardId());
            if (card == null) continue;

            EventView ev = map.computeIfAbsent(card.getId(), id -> new EventView(card));
            ev.addBenefit(b);
        }

        allEvents.clear();
        allEvents.addAll(map.values());

        allEvents.sort(Comparator.comparingInt(EventView::getRecord).reversed());

        allEvents.forEach(ev -> ev.finalizeBenefits(CATEGORY_MAP));
    }

    /* ===========================================================
       3. BAD DATA FILTER
    =========================================================== */
    private void filterInvalidBenefits() {

        List<String> excludeWords = List.of("꼭 확인하세요!", "집갈래", "잇힝");

        allEvents.removeIf(ev ->
                ev.getBenefits().stream().anyMatch(b ->
                        excludeWords.stream().anyMatch(word ->
                                contains(b.getBnfName(), word) ||
                                        contains(b.getBnfDetail(), word)
                        )
                )
        );
    }

    /* ===========================================================
       4. SEARCH
    =========================================================== */

    public List<EventView> search(String benefit, String brand) {
        Stream<EventView> stream = allEvents.stream();

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

        if (brand != null && !brand.isBlank()) {
            String q = brand.toLowerCase();

            stream = stream.filter(ev ->
                    ev.getCardBrand() != null &&
                            ev.getCardBrand().toLowerCase().equals(q)
            );
        }

        return stream.toList();
    }

    public List<EventView> searchByBenefit(String keyword) {
        return search(keyword, null);
    }

    public List<String> getBrands() {
        return cardMap.values().stream()
                .map(Card::getCardBrand)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    /* ===========================================================
       5. MBTI 추천
    =========================================================== */

    public List<EventView> getCardsByMbti(String mbti) {
        return switch (mbti) {
            case "ENFP", "ESFP" -> getHighBenefitCards();
            case "ISTJ", "ISFJ" -> getCashbackCards();
            case "ENTJ", "INTJ" -> getPointCards();
            case "ESTJ", "ESTP" -> getTransportCards();
            case "INFP", "INFJ" -> getCultureCards();
            default -> getTop10Cards();
        };
    }

    private List<EventView> filterByKeyword(String... keywords) {
        return allEvents.stream().filter(ev ->
                ev.getBenefits().stream().anyMatch(b ->
                        Arrays.stream(keywords).anyMatch(k ->
                                contains(b.getBnfContent(), k) ||
                                        contains(b.getBnfDetail(), k)
                        )
                )
        ).toList();
    }

    public List<EventView> getHighBenefitCards() {
        return filterByKeyword("할인", "적립", "혜택");
    }

    public List<EventView> getCashbackCards() {
        return filterByKeyword("캐시백");
    }

    public List<EventView> getPointCards() {
        return filterByKeyword("포인트");
    }

    public List<EventView> getTransportCards() {
        return filterByKeyword("교통", "주유", "버스");
    }

    public List<EventView> getCultureCards() {
        return filterByKeyword("카페", "문화", "영화", "커피");
    }

    public List<EventView> getTop10Cards() {
        return allEvents.stream().limit(10).toList();
    }

    /* ===========================================================
       PUBLIC ACCESS (⭐ 추가)
    =========================================================== */
    public List<EventView> getAllEvents() {
        return allEvents;
    }

    /* ===========================================================
       UTILS
    =========================================================== */

    private boolean contains(String text, String keyword) {
        return text != null && keyword != null && text.contains(keyword);
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null &&
                text.toLowerCase().contains(keyword);
    }

    private static String[] splitCsv(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private static String getSafe(String[] arr, int idx) {
        if (idx < 0 || idx >= arr.length) return "";
        return arr[idx].replaceAll("^\"|\"$", "").trim();
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.replaceAll("[^0-9-]", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
