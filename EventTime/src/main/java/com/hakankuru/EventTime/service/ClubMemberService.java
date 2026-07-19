package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.entity.Club;
import com.hakankuru.EventTime.entity.ClubMember;
import com.hakankuru.EventTime.entity.ClubMemberId;
import com.hakankuru.EventTime.entity.ClubRole;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.exception.NotEnoughAdminsException;
import com.hakankuru.EventTime.exception.UserAlreadyInClubException;
import com.hakankuru.EventTime.repository.ClubMemberRepository;
import com.hakankuru.EventTime.repository.ClubRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubMemberService {

    private final ClubMemberRepository clubMemberRepository;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;

    public List<com.hakankuru.EventTime.dto.ClubMemberResponse> getClubMembers(Long clubId) {
        return clubMemberRepository.findActiveMembershipsByClubId(clubId).stream()
                .map(m -> new com.hakankuru.EventTime.dto.ClubMemberResponse(
                        m.getUser().getUserId(),
                        m.getUser().getEmail(),
                        m.getUser().getName(),
                        m.getClubRole(),
                        m.getStartAt()
                )).collect(Collectors.toList());
    }

    @Transactional
    public void addMemberToClub(Long clubId, String userEmail, ClubRole role) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (clubMemberRepository.findActiveMembership(user.getUserId(), clubId).isPresent()) {
            throw new UserAlreadyInClubException("User is already an active member of this club");
        }

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        ClubMember clubMember = new ClubMember();
        clubMember.setId(new ClubMemberId(user.getUserId(), clubId));
        clubMember.setUser(user);
        clubMember.setClub(club);
        clubMember.setClubRole(role);
        clubMember.setStartAt(LocalDateTime.now());

        clubMemberRepository.save(clubMember);
    }

    @Transactional
    public void removeMemberFromClub(Long clubId, Long userId) {
        ClubMember membership = clubMemberRepository.findActiveMembership(userId, clubId)
                .orElseThrow(() -> new RuntimeException("Active membership not found"));

        if (membership.getClubRole() == ClubRole.ADMIN) {
            long activeAdmins = clubMemberRepository.countActiveAdminsByClubId(clubId);
            if (activeAdmins <= 1) {
                throw new NotEnoughAdminsException("Cannot remove the last active admin from the club");
            }
        }

        // Soft delete
        membership.setEndAt(LocalDateTime.now());
        clubMemberRepository.save(membership);
    }
}
