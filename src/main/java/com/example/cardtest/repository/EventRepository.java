package com.example.cardtest.repository;

import com.example.cardtest.domain.EventView;

import java.util.List;

public interface EventRepository {

    List<EventView> findHighBenefitCards();
    List<EventView> findCashbackCards();
    List<EventView> findPointCards();
    List<EventView> findTransportCards();
    List<EventView> findCultureCards();
    List<EventView> findTop10Cards();
}