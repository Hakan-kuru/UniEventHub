package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByClub_ClubId(Long clubId);
}
