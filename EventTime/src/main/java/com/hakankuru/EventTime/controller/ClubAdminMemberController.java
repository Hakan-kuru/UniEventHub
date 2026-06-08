package com.hakankuru.EventTime.controller;

import com.hakankuru.EventTime.dto.AddClubMemberRequest;
import com.hakankuru.EventTime.dto.ClubMemberManagementResponse;
import com.hakankuru.EventTime.dto.UserSearchResponse;
import com.hakankuru.EventTime.security.CustomUserDetails;
import com.hakankuru.EventTime.service.ClubAdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs/{clubId}")
@RequiredArgsConstructor
public class ClubAdminMemberController {

    private final ClubAdminMemberService clubAdminMemberService;

    @GetMapping("/members-mgmt")
    public ResponseEntity<List<ClubMemberManagementResponse>> getClubMembers(
            @PathVariable Long clubId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(clubAdminMemberService.getClubMembers(clubId, userDetails.getId()));
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserSearchResponse>> searchUsersForClub(
            @PathVariable Long clubId,
            @RequestParam String email,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(clubAdminMemberService.searchUsersForClub(clubId, email, userDetails.getId()));
    }

    @PostMapping("/members")
    public ResponseEntity<Void> addMemberToClub(
            @PathVariable Long clubId,
            @RequestBody AddClubMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        clubAdminMemberService.addMemberToClub(clubId, request.getEmail(), userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/members/{userIdToRemove}")
    public ResponseEntity<Void> removeMemberFromClub(
            @PathVariable Long clubId,
            @PathVariable Long userIdToRemove,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        clubAdminMemberService.removeMemberFromClub(clubId, userIdToRemove, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
