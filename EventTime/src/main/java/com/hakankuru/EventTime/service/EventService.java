package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.dto.EventCreateRequest;
import com.hakankuru.EventTime.entity.Club;
import com.hakankuru.EventTime.entity.Event;
import com.hakankuru.EventTime.event.EventCreatedEvent;
import com.hakankuru.EventTime.dto.EventResponse;
import com.hakankuru.EventTime.entity.EventParticipant;
import com.hakankuru.EventTime.entity.EventParticipantId;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.repository.ClubRepository;
import com.hakankuru.EventTime.repository.EventParticipantRepository;
import com.hakankuru.EventTime.repository.EventRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final ClubRepository clubRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public EventResponse createEvent(EventCreateRequest request) {
        Club club = clubRepository.findById(request.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found"));

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setClub(club);
        event.setStartAt(request.getStartAt() != null ? request.getStartAt() : LocalDateTime.now().plusDays(1));
        event.setEndAt(request.getEndAt());
        event.setCapacity(request.getCapacity());

        event = eventRepository.save(event);

        eventPublisher.publishEvent(new EventCreatedEvent(this, event.getEventId(), club.getClubId(), event.getTitle()));

        return mapToResponse(event);
    }

    public List<EventResponse> getEventsByClub(Long clubId) {
        return eventRepository.findByClub_ClubId(clubId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public void joinEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Event event = eventRepository.findById(eventId).orElseThrow();

        EventParticipant ep = new EventParticipant();
        ep.setId(new EventParticipantId(user.getUserId(), eventId));
        ep.setUser(user);
        ep.setEvent(event);
        ep.setRegisteredAt(LocalDateTime.now());
        eventParticipantRepository.save(ep);
    }

    @Transactional
    public void leaveEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        eventParticipantRepository.deleteById(new EventParticipantId(user.getUserId(), eventId));
    }

    private EventResponse mapToResponse(Event event) {
        return new EventResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getImage(),
                event.getStartAt(),
                event.getEndAt(),
                event.getApplyStartAt(),
                event.getApplyEndAt(),
                event.getCapacity(),
                event.getClub().getClubId(),
                event.getClub().getName()
        );
    }
}
