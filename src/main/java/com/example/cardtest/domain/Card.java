package com.example.cardtest.domain;

import lombok.*;

@Getter
@Setter
@Builder
public class Card {
    private String id;
    private String cardName;
    private String cardBrand;
    private String cardInOut1;
    private String cardInOut2;
    private String cardInOut3;
    private String cardImg;
    private int cardRecord; // 인기 지표
    private String cardOverseas;

    public Card(String id, String cardName, String cardBrand,
                String cardInOut1, String cardInOut2, String cardInOut3,
                String cardImg, int cardRecord, String cardOverseas) {
        this.id = id;
        this.cardName = cardName;
        this.cardBrand = cardBrand;
        this.cardInOut1 = cardInOut1;
        this.cardInOut2 = cardInOut2;
        this.cardInOut3 = cardInOut3;
        this.cardImg = cardImg;
        this.cardRecord = cardRecord;
        this.cardOverseas = cardOverseas;
    }

    // getters
    public String getId() { return id; }
    public String getCardName() { return cardName; }
    public String getCardBrand() { return cardBrand; }
    public String getCardInOut1() { return cardInOut1; }
    public String getCardInOut2() { return cardInOut2; }
    public String getCardInOut3() { return cardInOut3; }
    public String getCardImg() { return cardImg; }
    public int getCardRecord() { return cardRecord; }
    public String getCardOverseas() { return cardOverseas; }
}
