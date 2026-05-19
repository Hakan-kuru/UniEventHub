package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.EventCreateRequest;
import com.hakankuru.EventTime.entity.Event;
import com.hakankuru.EventTime.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("@clubSecurity.isClubAdmin(authentication, #request.clubId) or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody EventCreateRequest request) {
        Event event = eventService.createEvent(request);
        return ResponseEntity.ok(event);
    }
}
