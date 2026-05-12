package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.dto.EventCreateRequest;
import com.hakankuru.EventTime.entity.Club;
import com.hakankuru.EventTime.entity.Event;
import com.hakankuru.EventTime.event.EventCreatedEvent;
import com.hakankuru.EventTime.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final ClubRepository clubRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Event createEvent(EventCreateRequest request) {
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setClub(club);
        event.setStartAt(LocalDateTime.now().plusDays(1)); // example default

        // Save event to repository (assuming EventRepository exists, omitting for brevity or pseudo-saving)
        // eventRepository.save(event);
        Long generatedId = 1L; // pseudo id

        // Publish event for notification system
        eventPublisher.publishEvent(new EventCreatedEvent(this, generatedId, club.getClubId(), event.getTitle()));

        return event;
    }
}
