package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.dto.UserClubDTO;
import com.hakankuru.EventTime.dto.UserProfileResponse;
import com.hakankuru.EventTime.entity.ClubMember;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.repository.ClubMemberRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ClubMember> activeMemberships = clubMemberRepository.findActiveMembershipsByUserId(userId);

        List<UserClubDTO> userClubDTOs = activeMemberships.stream()
                .map(membership -> new UserClubDTO(
                        membership.getClub().getClubId(),
                        membership.getClub().getName(),
                        membership.getClubRole()
                ))
                .collect(Collectors.toList());

        return new UserProfileResponse(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getGlobalRole(),
            userClubDTOs
        );
    }
}
