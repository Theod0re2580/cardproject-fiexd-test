package com.example.cardtest.repository;

import com.example.cardtest.domain.Card;
import com.example.cardtest.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventListRepository extends JpaRepository<Event, Long> {

    /** 진행 중 이벤트 */
    List<Event> findByStartDateBeforeAndEndDateAfter(LocalDate now1, LocalDate now2);

    /** 진행 중 + 검색 */
    @Query("SELECT e FROM Event e " +
            "WHERE e.startDate <= :now AND e.endDate >= :now " +
            "AND (LOWER(e.eventName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.eventDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) )")
    List<Event> searchRunningEvents(@Param("now") LocalDate now,
                                    @Param("keyword") String keyword);

    /** 최신 이벤트 N개 */
    @Query(value = "SELECT * FROM EVENT ORDER BY EVENT_ID DESC FETCH FIRST :limit ROWS ONLY",
            nativeQuery = true)
    List<Event> findLatest(@Param("limit") int limit);

    /** 🔥 기존 관리자 검색(리스트 용) */
    List<Event> findByEventNameContainingIgnoreCase(String keyword);

    /** 🔥 페이징 전용 검색 — 리스트용과 충돌 방지 */
    Page<Event> findByEventNameContainingIgnoreCaseAndEventIdIsNotNull(
            String keyword,
            Pageable pageable
    );
}
