package com.example.cardtest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "BENEFIT")
@Getter @Setter @NoArgsConstructor
public class Benefit {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bnf_name")
    private String bnfName;

    @Column(name = "bnf_content")
    private String bnfContent;

    @Column(name = "bnf_detail")
    private String bnfDetail;

    @Column(name = "card_id", insertable = false, updatable = false)
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;
}

