package com.example.cardtest.repository;

import com.example.cardtest.domain.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByCardNameContaining(String keyword);
    @Query(value = "SELECT * FROM CARD ORDER BY ID DESC FETCH FIRST :limit ROWS ONLY", nativeQuery = true)
    List<Card> findLatest(@Param("limit") int limit);
    List<Card> findByCardNameContainingIgnoreCaseOrCardBrandContainingIgnoreCase(
            String name, String brand
    );
    Page<Card> findByCardNameContainingIgnoreCaseOrCardBrandContainingIgnoreCase(
            String name, String brand, Pageable pageable
    );
}