package com.example.cardtest.repository;

import com.example.cardtest.domain.Benefit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    // 기존 기능 유지
    List<Benefit> findByCardId(Long cardId);

    // 추가 기능: 혜택명 검색 + 페이지네이션
    Page<Benefit> findByBnfNameContainingIgnoreCase(String keyword, Pageable pageable);

    // 카드별 혜택 목록 페이지네이션 (선택 요소, 필요하면 사용)
    Page<Benefit> findByCardId(Long cardId, Pageable pageable);

    @Query(value = "SELECT * FROM BENEFIT ORDER BY ID DESC FETCH FIRST :limit ROWS ONLY", nativeQuery = true)
    List<Benefit> findLatest(@Param("limit") int limit);
}
