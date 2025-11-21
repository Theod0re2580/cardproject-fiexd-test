    package com.example.cardtest.service;

    import com.example.cardtest.domain.Event;
    import com.example.cardtest.repository.EventListRepository;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.util.List;

    @Service
    public class EventListService {

        private final EventListRepository eventListRepository;

        public EventListService(EventListRepository eventListRepository) {
            this.eventListRepository = eventListRepository;
        }

        /** 진행중 이벤트 전체 조회 */
        public List<Event> getOngoingEvents() {
            LocalDate today = LocalDate.now();
            return eventListRepository.findByStartDateBeforeAndEndDateAfter(today, today);
        }

        /** 검색 + 진행중 이벤트 */
        public List<Event> searchEvents(String keyword) {
            LocalDate today = LocalDate.now();

            // 검색어 없으면 전체 리스트 보여주기
            if (keyword == null || keyword.trim().isEmpty()) {
                return getOngoingEvents();
            }

            // 변경된 JPQL 검색 메서드 사용
            return eventListRepository.searchRunningEvents(today, keyword);
        }

        /** 이벤트 상세 조회 */
        public Event getEventDetail(Long id) {
            return eventListRepository.findById(id).orElse(null);
        }
    }
