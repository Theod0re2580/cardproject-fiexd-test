package com.example.cardtest.repository;

import com.example.cardtest.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventListRepository extends JpaRepository<Event, Long> {

    /** 진행 중인 이벤트 */
    List<Event> findByStartDateBeforeAndEndDateAfter(LocalDate now1, LocalDate now2);

    /** 진행 중 + 이름 또는 설명에 검색어 포함 */
    @Query("SELECT e FROM Event e " +
            "WHERE e.startDate <= :now AND e.endDate >= :now " +
            "AND (e.eventName LIKE %:keyword% OR e.eventDescription LIKE %:keyword%)")
    List<Event> searchRunningEvents(@Param("now") LocalDate now,
                                    @Param("keyword") String keyword);
}
