package com.example.cardtest.service;

import com.example.cardtest.domain.Benefit;
import com.example.cardtest.repository.BenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** 페이징 + 검색 조회 */
    public Page<Benefit> findAll(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return benefitRepository.findAll(pageable);
        }
        return benefitRepository.findByBnfNameContainingIgnoreCase(keyword, pageable);
    }

    /** 단건 조회 */
    public Benefit findById(Long id) {
        return benefitRepository.findById(id)
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

        benefit.setBnfName((update.getBnfName() == null) ? "" : update.getBnfName().trim());
        benefit.setBnfContent((update.getBnfContent() == null) ? "" : update.getBnfContent().trim());
        benefit.setBnfDetail((update.getBnfDetail() == null) ? "" : update.getBnfDetail().trim());

        if (update.getCardId() == null) {
            throw new IllegalArgumentException("카드 ID는 반드시 선택해야 합니다.");
        }
        benefit.setCardId(update.getCardId());

        benefitRepository.save(benefit);
    }

    /** 혜택 삭제 */
    public void delete(Long id) {
        benefitRepository.deleteById(id);
    }

    /** 🔥 관리자 대시보드용 - 최신 카드 N개 조회 */
    public List<Benefit> findLatest(int limit) {
        return benefitRepository.findLatest(limit);
    }

    public List<Benefit> adminSearch(String keyword) {
        return benefitRepository
                .findByBnfNameContainingIgnoreCaseOrCard_CardNameContainingIgnoreCase(keyword, keyword);
    }
}
