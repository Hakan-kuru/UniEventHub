package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.EventParticipant;
import com.hakankuru.EventTime.entity.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {
    List<EventParticipant> findById_UserId(Long userId);
    List<EventParticipant> findById_EventId(Long eventId);
}
