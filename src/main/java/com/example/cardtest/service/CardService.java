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

    /** 공통 입력 검증 (null 방지 + 필수값 체크) */
    private void validateCard(Card card) {

        if (card.getCardName() == null || card.getCardName().trim().isEmpty()) {
            throw new RuntimeException("카드 이름은 필수 입력값입니다.");
        }

        if (card.getCardBrand() == null || card.getCardBrand().trim().isEmpty()) {
            throw new RuntimeException("카드 브랜드는 필수 입력값입니다.");
        }

        // 선택 필드 null 방지
        if (card.getCardInOut1() == null) card.setCardInOut1("");
        if (card.getCardInOut2() == null) card.setCardInOut2("");
        if (card.getCardInOut3() == null) card.setCardInOut3("");
        if (card.getCardImg() == null) card.setCardImg("");
        if (card.getCardRecord() == null) card.setCardRecord("");
        if (card.getCardOverseas() == null) card.setCardOverseas("");
    }

    /** 카드 등록 */
    public void add(Card card) {
        try {
            validateCard(card);
            cardRepository.save(card);
        } catch (Exception e) {
            System.out.println("[ERROR] 카드 등록 실패: " + e.getMessage());
            throw new RuntimeException("카드 등록에 실패했습니다. " + e.getMessage());
        }
    }

    /** 카드 수정 */
    public void update(Long id, Card update) {

        Card card = findById(id);

        try {
            validateCard(update);

            card.setCardName(update.getCardName());
            card.setCardBrand(update.getCardBrand());
            card.setCardInOut1(update.getCardInOut1());
            card.setCardInOut2(update.getCardInOut2());
            card.setCardInOut3(update.getCardInOut3());
            card.setCardImg(update.getCardImg());
            card.setCardRecord(update.getCardRecord());
            card.setCardOverseas(update.getCardOverseas());

            cardRepository.save(card);
        } catch (Exception e) {
            System.out.println("[ERROR] 카드 수정 실패: " + e.getMessage());
            throw new RuntimeException("카드 수정에 실패했습니다. " + e.getMessage());
        }
    }

    /** 카드 삭제 */
    public void delete(Long id) {
        try {
            if (!cardRepository.existsById(id)) {
                throw new RuntimeException("삭제할 카드가 존재하지 않습니다.");
            }

            cardRepository.deleteById(id);

        } catch (Exception e) {
            System.out.println("[ERROR] 카드 삭제 실패: " + e.getMessage());
            throw new RuntimeException("카드 삭제 중 오류가 발생했습니다.");
        }

    }

    /** 카드 검색 */
    public List<Card> search(String keyword) {
        return cardRepository.findByCardNameContaining(keyword);
    }

    /** 🔥 관리자 대시보드용 - 최신 카드 N개 조회 */
    public List<Card> findLatest(int limit) {
        return cardRepository.findLatest(limit);
    }

    /** 관리자 전용 검색 */
    public List<Card> adminSearch(String keyword) {
        return cardRepository.findByCardNameContainingIgnoreCaseOrCardBrandContainingIgnoreCase(
                keyword, keyword
        );
    }
}
