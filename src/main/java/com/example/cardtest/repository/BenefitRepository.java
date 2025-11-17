package com.example.cardtest.repository;

import com.example.cardtest.domain.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, String> {
    List<Benefit> findByCardId(Long cardId);
}