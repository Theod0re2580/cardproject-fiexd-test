package com.example.cardtest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CARD")
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @Column(name = "ID")
    @SequenceGenerator(
            name = "card_seq_generator",
            sequenceName = "SEQ_CARD",   // Oracle에 만들어 둔 시퀀스 이름
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_seq_generator")
    private Long id;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "card_brand")
    private String cardBrand;

    @Column(name = "card_in_out_1")
    private String cardInOut1;

    @Column(name = "card_in_out_2")
    private String cardInOut2;

    @Column(name = "card_in_out_3")
    private String cardInOut3;

    @Column(name = "card_img")
    private String cardImg;

    @Column(name = "card_record")
    private String cardRecord;

    @Column(name = "card_overseas")
    private String cardOverseas;
}
