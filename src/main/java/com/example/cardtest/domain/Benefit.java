package com.example.cardtest.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Benefit {
    private String id;
    private String bnfName;
    private String bnfContent;
    private String bnfDetail;
    private String cardId;

    public Benefit(String id, String bnfName, String bnfContent, String bnfDetail, String cardId) {
        this.id = id;
        this.bnfName = bnfName;
        this.bnfContent = bnfContent;
        this.bnfDetail = bnfDetail;
        this.cardId = cardId;
    }

    public String getId() { return id; }
    public String getBnfName() { return bnfName; }
    public String getBnfContent() { return bnfContent; }
    public String getBnfDetail() { return bnfDetail; }
    public String getCardId() { return cardId; }
}
