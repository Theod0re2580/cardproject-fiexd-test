package com.example.cardtest.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class EventView {

    private Card card;

    // 카드 혜택 목록
    private List<Benefit> benefits = new ArrayList<>();

    // HTML/JS에서 사용할 값들
    private String jsonBenefits;       // 모달용 JSON
    private String benefitNames;       // "커피,편의점"
    private String categoryString;     // "카페·디저트,편의점"
    private Set<String> categories = new HashSet<>(); // 중복 제거용 Set

    public EventView() {}

    public EventView(Card card) {
        this.card = card;
    }

    public void addBenefit(Benefit b) {
        if (b != null) benefits.add(b);
    }

    /**
     * joinData() 이후 실행
     * 카테고리 매핑 + JSON + 정제 문자열 생성
     */
    public void finalizeBenefits(Map<String, String> categoryMap) {

        /* -------------------------
           1) JSON 변환
        ------------------------- */
        this.jsonBenefits = benefits.stream()
                .map(b -> String.format(
                        "{\"name\":\"%s\",\"content\":\"%s\",\"detail\":\"%s\"}",
                        safe(b.getBnfName()), safe(b.getBnfContent()), safe(b.getBnfDetail())
                ))
                .collect(Collectors.joining(",", "[", "]"));

        /* -------------------------
           2) 혜택명 리스트
        ------------------------- */
        this.benefitNames = benefits.stream()
                .map(Benefit::getBnfName)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.joining(","));

        /* -------------------------
           3) 상위 카테고리 그룹 생성
        ------------------------- */
        this.categories = benefits.stream()
                .map(Benefit::getBnfName)
                .filter(Objects::nonNull)
                .flatMap(name ->
                        categoryMap.entrySet().stream()
                                .filter(e -> name.contains(e.getKey()))
                                .map(Map.Entry::getValue)
                )
                .collect(Collectors.toSet());

        if (this.categories.isEmpty()) {
            this.categories.add("기타");
        }

        /* HTML에서 사용 가능한 문자열 */
        this.categoryString = String.join(",", this.categories);
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    /* 편의 Getter */
    public String getCardName() {
        return card != null ? card.getCardName() : "";
    }

    public String getCardBrand() {
        return card != null ? card.getCardBrand() : "";
    }

    public int getRecord() {
        try { return Integer.parseInt(card.getCardRecord()); }
        catch (Exception e) { return 0; }
    }

}
