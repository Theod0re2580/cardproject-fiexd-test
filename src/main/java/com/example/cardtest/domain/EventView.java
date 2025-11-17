package com.example.cardtest.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class EventView {

    private Card card;

    private List<Benefit> benefits = new ArrayList<>();

    private String jsonBenefits;     // 모달용 JSON
    private String benefitNames;     // "커피,편의점"
    private String categoryString;   // "카페/디저트,편의점"  ← HTML 필터용 (중요!)
    private Set<String> categories = new HashSet<>(); // 내부 관리용 Set

    public EventView() {}
    public EventView(Card card) {
        this.card = card;
    }

    public void addBenefit(Benefit b) {
        if (b != null) benefits.add(b);
    }

    /**
     * joinData() 이후 실행
     */
    public void finalizeBenefits(Map<String, String> categoryMap) {

        /* -------------------------
           1) 혜택 JSON 생성
        ------------------------- */
        this.jsonBenefits = benefits.stream()
                .map(b -> String.format(
                        "{\"name\":\"%s\",\"content\":\"%s\",\"detail\":\"%s\"}",
                        safe(b.getBnfName()), safe(b.getBnfContent()), safe(b.getBnfDetail())
                ))
                .collect(Collectors.joining(",", "[", "]"));

        /* -------------------------
           2) 혜택명 리스트 생성
        ------------------------- */
        this.benefitNames = benefits.stream()
                .map(Benefit::getBnfName)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .distinct()
                .collect(Collectors.joining(","));

        /* -------------------------
           3) 상위 카테고리 그룹 자동 생성
        ------------------------- */
        this.categories = benefits.stream()
                .map(Benefit::getBnfName)
                .filter(Objects::nonNull)
                .flatMap(name ->
                        categoryMap.entrySet().stream()
                                .filter(e -> name.contains(e.getKey()))   // 포함 매칭
                                .map(Map.Entry::getValue)
                )
                .collect(Collectors.toSet());

        if (this.categories.isEmpty()) {
            this.categories.add("기타");
        }

        /* HTML에서 사용할 문자열 버전 */
        this.categoryString = String.join(",", this.categories);
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    /* Getter */
    public String getCardName() {
        return card != null ? card.getCardName() : "";
    }

    public String getCardBrand() {
        return card != null ? card.getCardBrand() : "";
    }

    public int getRecord() {
        return card != null ? card.getCardRecord() : 0;
    }


}
