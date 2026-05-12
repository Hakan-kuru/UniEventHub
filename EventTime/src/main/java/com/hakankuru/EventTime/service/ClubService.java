package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.dto.ClubCreateRequest;
import com.hakankuru.EventTime.entity.Club;
import com.hakankuru.EventTime.entity.ClubMember;
import com.hakankuru.EventTime.entity.ClubMemberId;
import com.hakankuru.EventTime.entity.ClubRole;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.exception.UserAlreadyInClubException;
import com.hakankuru.EventTime.repository.ClubMemberRepository;
import com.hakankuru.EventTime.repository.ClubRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Transactional
    public Club createClub(ClubCreateRequest request) {
        Club club = new Club();
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        // For now, leaving university as null or you can fetch default university.
        return clubRepository.save(club);
    }

    @Transactional
    public void assignClubAdmin(Long clubId, String userEmail) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (clubMemberRepository.findActiveMembership(user.getUserId(), clubId).isPresent()) {
            throw new UserAlreadyInClubException("User is already an active member of this club");
        }

        ClubMember clubMember = new ClubMember();
        clubMember.setId(new ClubMemberId(user.getUserId(), clubId));
        clubMember.setUser(user);
        clubMember.setClub(club);
        clubMember.setClubRole(ClubRole.ADMIN);
        clubMember.setStartAt(LocalDateTime.now());
        
        clubMemberRepository.save(clubMember);
    }
}
