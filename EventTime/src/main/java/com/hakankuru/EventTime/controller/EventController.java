package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.EventCreateRequest;
import com.hakankuru.EventTime.dto.EventResponse;
import com.hakankuru.EventTime.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("@clubSecurity.isClubAdmin(authentication, #request.clubId) or hasRole('SUPER_ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest request) {
        return ResponseEntity.ok(eventService.createEvent(request));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<Void> joinEvent(@PathVariable Long eventId, Principal principal) {
        eventService.joinEvent(eventId, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}/leave")
    public ResponseEntity<Void> leaveEvent(@PathVariable Long eventId, Principal principal) {
        eventService.leaveEvent(eventId, principal.getName());
        return ResponseEntity.ok().build();
    }
}
