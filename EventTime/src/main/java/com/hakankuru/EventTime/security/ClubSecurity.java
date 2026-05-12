package com.hakankuru.EventTime.security;

import com.hakankuru.EventTime.entity.ClubMember;
import com.hakankuru.EventTime.entity.ClubRole;
import com.hakankuru.EventTime.repository.ClubMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("clubSecurity")
@RequiredArgsConstructor
public class ClubSecurity {

    private final ClubMemberRepository clubMemberRepository;

    public boolean isClubAdmin(Authentication authentication, Long clubId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getUser().getUserId();
            Optional<ClubMember> membership = clubMemberRepository.findActiveMembership(userId, clubId);
            return membership.isPresent() && membership.get().getClubRole() == ClubRole.ADMIN;
        }

        return false;
    }

    public boolean isClubMember(Authentication authentication, Long clubId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            Long userId = userDetails.getUser().getUserId();
            return clubMemberRepository.findActiveMembership(userId, clubId).isPresent();
        }

        return false;
    }
}
