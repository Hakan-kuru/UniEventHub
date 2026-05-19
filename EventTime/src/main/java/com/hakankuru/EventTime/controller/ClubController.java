package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.ClubCreateRequest;
import com.hakankuru.EventTime.entity.Club;
import com.hakankuru.EventTime.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Club> createClub(@RequestBody ClubCreateRequest request) {
        Club club = clubService.createClub(request);
        return ResponseEntity.ok(club);
    }

    @PostMapping("/{clubId}/assign-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> assignAdmin(@PathVariable Long clubId, @RequestParam String userEmail) {
        clubService.assignClubAdmin(clubId, userEmail);
        return ResponseEntity.ok().build();
    }
}
