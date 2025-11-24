package com.example.cardtest.service;

import com.example.cardtest.domain.Event;
import com.example.cardtest.repository.EventListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventListService {

    private final EventListRepository eventListRepository;

    /** 전체 조회 */
    public List<Event> findAll() {
        return eventListRepository.findAll();
    }

    /** 단건 조회 */
    public Event findById(Long id) {
        return eventListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다. id=" + id));
    }

    /** 사용자용 상세 조회 */
    public Event getEventDetail(Long id) {
        return findById(id);
    }

    /** 진행중 이벤트 조회 */
    public List<Event> getOngoingEvents() {
        LocalDate today = LocalDate.now();
        return eventListRepository.findByStartDateBeforeAndEndDateAfter(today, today);
    }

    /** 사용자 검색 */
    public List<Event> searchEvents(String keyword) {
        LocalDate today = LocalDate.now();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getOngoingEvents();
        }
        return eventListRepository.searchRunningEvents(today, keyword);
    }

    /** 🔥 관리자 검색 (리스트) */
    public List<Event> searchAdminEvents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return eventListRepository.findByEventNameContainingIgnoreCase(keyword);
    }

    /** 🔥 관리자 페이징 조회 */
    public Page<Event> searchAdminEventsPaged(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return eventListRepository.findAll(pageable);
        }
        return eventListRepository
                .findByEventNameContainingIgnoreCaseAndEventIdIsNotNull(keyword, pageable);
    }

    /** 이벤트 등록 */
    public Event addEvent(Event event) {
        return eventListRepository.save(event);
    }

    /** 이벤트 수정 */
    public Event updateEvent(Long id, Event update) {
        Event event = findById(id);

        event.setEventName(update.getEventName());
        event.setEventDescription(update.getEventDescription());
        event.setStartDate(update.getStartDate());
        event.setEndDate(update.getEndDate());
        event.setBannerImage(update.getBannerImage());
        event.setBenefit(update.getBenefit());

        return eventListRepository.save(event);
    }

    /** 이벤트 삭제 */
    public void deleteEvent(Long id) {
        if (!eventListRepository.existsById(id)) {
            throw new IllegalArgumentException("삭제할 이벤트가 존재하지 않습니다. id=" + id);
        }
        eventListRepository.deleteById(id);
    }

    /** 최신 N개 조회 */
    public List<Event> findLatest(int limit) {
        return eventListRepository.findLatest(limit);
    }
}
