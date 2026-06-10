package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.dto.UserClubDTO;
import com.hakankuru.EventTime.dto.UserProfileResponse;
import com.hakankuru.EventTime.dto.EventResponse;
import com.hakankuru.EventTime.dto.UserUpdateRequest;
import com.hakankuru.EventTime.entity.ClubMember;
import com.hakankuru.EventTime.entity.EventParticipant;
import com.hakankuru.EventTime.entity.GlobalRole;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.entity.UserRole;
import com.hakankuru.EventTime.repository.ClubMemberRepository;
import com.hakankuru.EventTime.repository.EventParticipantRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import com.hakankuru.EventTime.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final UserRoleRepository userRoleRepository;

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ClubMember> activeMemberships = clubMemberRepository.findActiveMembershipsByUserId(userId);

        List<UserClubDTO> userClubDTOs = activeMemberships.stream()
                .map(membership -> new UserClubDTO(
                        membership.getClub().getClubId(),
                        membership.getClub().getName(),
                        membership.getClubRole() != null ? membership.getClubRole() : com.hakankuru.EventTime.entity.ClubRole.MEMBER
                ))
                .collect(Collectors.toList());

        // Users_roles üzerinden üniversite bilgisi çekme (GlobalRole enum'u tek değer 'USER' içerdiği için doğrudan string kontrolü yapılabilir veya admin rolü users_roles daki rol adına göre filtrelenir)
        Long universityId = null;
        Optional<UserRole> adminRole = userRoleRepository.findByUser_UserIdAndRole(userId, "ADMIN");
        Optional<UserRole> superAdminRole = userRoleRepository.findByUser_UserIdAndRole(userId, "SUPER_ADMIN");

        if (adminRole.isPresent()) {
            universityId = adminRole.get().getUniversity() != null ? adminRole.get().getUniversity().getUniversityId() : null;
        } else if (superAdminRole.isPresent()) {
            universityId = superAdminRole.get().getUniversity() != null ? superAdminRole.get().getUniversity().getUniversityId() : null;
        }

        return new UserProfileResponse(
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getGlobalRole(),
            userClubDTOs,
            universityId
        );
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setName(request.getName());
        userRepository.save(user);
        return getUserProfile(userId);
    }

    public List<EventResponse> getUserEvents(Long userId) {
        return eventParticipantRepository.findById_UserId(userId).stream()
                .map(EventParticipant::getEvent)
                .map(event -> new EventResponse(
                        event.getEventId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getImage(),
                        event.getStartAt(),
                        event.getEndAt(),
                        event.getApplyStartAt(),
                        event.getApplyEndAt(),
                        event.getCapacity(),
                        event.getClub().getClubId(),
                        event.getClub().getName()
                )).collect(Collectors.toList());
    }
}
