package com.example.cardtest.service;

import com.example.cardtest.domain.Benefit;
import com.example.cardtest.repository.BenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private final BenefitRepository benefitRepository;

    /** 전체 조회 */
    public List<Benefit> findAll() {
        return benefitRepository.findAll();
    }

    /** 단건 조회 (Long → String 변환) */
    public Benefit findById(Long id) {
        return benefitRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("혜택을 찾을 수 없습니다. id=" + id));
    }

    /** 혜택 추가 */
    public void add(Long cardId, Benefit benefit) {

        if (cardId == null) {
            throw new IllegalArgumentException("카드 ID가 없습니다.");
        }

        benefit.setCardId(cardId);
        if (benefit.getBnfName() == null) benefit.setBnfName("");
        if (benefit.getBnfContent() == null) benefit.setBnfContent("");
        if (benefit.getBnfDetail() == null) benefit.setBnfDetail("");

        benefitRepository.save(benefit);
    }

    /** 혜택 수정 */
    public void update(Long id, Benefit update) {

        Benefit benefit = findById(id);

        // 🔥 문자열 null-safe
        benefit.setBnfName((update.getBnfName() == null) ? "" : update.getBnfName().trim());
        benefit.setBnfContent((update.getBnfContent() == null) ? "" : update.getBnfContent().trim());
        benefit.setBnfDetail((update.getBnfDetail() == null) ? "" : update.getBnfDetail().trim());

        // 🔥 cardId null-safe (Long)
        if (update.getCardId() == null) {
            throw new IllegalArgumentException("카드 ID는 반드시 선택해야 합니다.");
        }

        benefit.setCardId(update.getCardId());

        benefitRepository.save(benefit);
    }

    /** 혜택 삭제 */
    public void delete(Long id) {
        benefitRepository.deleteById(String.valueOf(id));
    }
}
