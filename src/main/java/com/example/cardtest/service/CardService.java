package com.example.cardtest.service;

import com.example.cardtest.domain.Card;
import com.example.cardtest.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    public Card findById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카드를 찾을 수 없습니다. id=" + id));
    }

    public void add(Card card) {
        cardRepository.save(card);
    }

    public void update(Long id, Card update) {
        Card card = findById(id);
        card.setCardName(update.getCardName());
        card.setCardBrand(update.getCardBrand());
        card.setCardInOut1(update.getCardInOut1());
        card.setCardInOut2(update.getCardInOut2());
        card.setCardInOut3(update.getCardInOut3());
        card.setCardImg(update.getCardImg());
        card.setCardRecord(update.getCardRecord());
        card.setCardOverseas(update.getCardOverseas());
        cardRepository.save(card);
    }

    public void delete(Long id) {
        cardRepository.deleteById(id);
    }
}
