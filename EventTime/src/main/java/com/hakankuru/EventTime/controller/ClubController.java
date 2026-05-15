package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.ClubCreateRequest;
import com.hakankuru.EventTime.entity.Club;
import com.hakankuru.EventTime.service.ClubService;
import com.hakankuru.EventTime.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Club> createClub(@RequestBody ClubCreateRequest request) {
        Club club = clubService.createClub(request);
        return ResponseEntity.ok(club);
    }

    @GetMapping
    public ResponseEntity<List<com.hakankuru.EventTime.dto.ClubResponse>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @PutMapping("/{clubId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<com.hakankuru.EventTime.dto.ClubResponse> updateClub(@PathVariable Long clubId, @RequestBody com.hakankuru.EventTime.dto.ClubUpdateRequest request) {
        return ResponseEntity.ok(clubService.updateClub(clubId, request));
    }

    @PostMapping("/{clubId}/assign-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> assignAdmin(@PathVariable Long clubId, @RequestParam String userEmail) {
        clubService.assignClubAdmin(clubId, userEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{clubId}/events")
    public ResponseEntity<List<com.hakankuru.EventTime.dto.EventResponse>> getClubEvents(@PathVariable Long clubId) {
        return ResponseEntity.ok(eventService.getEventsByClub(clubId));
    }
}
