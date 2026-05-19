package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.entity.ClubRole;
import com.hakankuru.EventTime.service.ClubMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clubs/{clubId}/members")
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService clubMemberService;

    @PostMapping
    @PreAuthorize("@clubSecurity.isClubAdmin(authentication, #clubId) or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> addMember(
            @PathVariable Long clubId,
            @RequestParam String userEmail,
            @RequestParam(defaultValue = "MEMBER") ClubRole role) {
        clubMemberService.addMemberToClub(clubId, userEmail, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@clubSecurity.isClubAdmin(authentication, #clubId) or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> removeMember(@PathVariable Long clubId, @PathVariable Long userId) {
        clubMemberService.removeMemberFromClub(clubId, userId);
        return ResponseEntity.ok().build();
    }
}
