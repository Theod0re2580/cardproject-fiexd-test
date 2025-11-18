package com.example.cardtest.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BENEFIT")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class Benefit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "benefit_seq")
    @SequenceGenerator(name = "benefit_seq", sequenceName = "SEQ_BENEFIT", allocationSize = 1)
    private Long id;

    @Column(name = "bnf_name")
    private String bnfName;

    @Column(name = "bnf_content")
    private String bnfContent;

    @Column(name = "bnf_detail")
    private String bnfDetail;

    /** 🔥 insert / update 되는 진짜 FK 값 */
    @Column(name = "card_id")
    private Long cardId;

    /** 🔥 연관관계는 읽기 전용으로 둬야 충돌이 안 남 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", insertable = false, updatable = false)
    private Card card;
}

