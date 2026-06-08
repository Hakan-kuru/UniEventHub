package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.dto.ClubMemberManagementResponse;
import com.hakankuru.EventTime.dto.UserSearchResponse;
import com.hakankuru.EventTime.entity.*;
import com.hakankuru.EventTime.exception.ClubPermissionDeniedException;
import com.hakankuru.EventTime.exception.UserAlreadyInClubException;
import com.hakankuru.EventTime.exception.UserNotInUniversityException;
import com.hakankuru.EventTime.repository.ClubMemberRepository;
import com.hakankuru.EventTime.repository.ClubRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubAdminMemberService {

    private final ClubRepository clubRepository;
    private final UserRepository userRepository;
    private final ClubMemberRepository clubMemberRepository;

    private void verifyAdminRole(Long clubId, Long adminUserId) {
        Optional<ClubMember> adminMembership = clubMemberRepository.findActiveMembership(adminUserId, clubId);
        if (adminMembership.isEmpty() || !adminMembership.get().getClubRole().equals(ClubRole.ADMIN)) {
            throw new ClubPermissionDeniedException("Sadece kulüp yöneticisi bu işlemi gerçekleştirebilir.");
        }
    }

    @Transactional(readOnly = true)
    public List<ClubMemberManagementResponse> getClubMembers(Long clubId, Long adminUserId) {
        verifyAdminRole(clubId, adminUserId);

        List<ClubMember> members = clubMemberRepository.findActiveMembershipsByClubId(clubId);
        return members.stream().map(member -> ClubMemberManagementResponse.builder()
                .userId(member.getUser().getUserId())
                .name(member.getUser().getName())
                .email(member.getUser().getEmail())
                .departmentName(member.getUser().getDepartments() != null ? member.getUser().getDepartments().getName() : null)
                .clubRole(member.getClubRole().name())
                .build()).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSearchResponse> searchUsersForClub(Long clubId, String emailSearch, Long adminUserId) {
        verifyAdminRole(clubId, adminUserId);

        Club club = clubRepository.findById(clubId).orElseThrow(() -> new RuntimeException("Kulüp bulunamadı"));
        Long universityId = club.getUniversity().getUniversityId();

        List<User> usersInUniversity = userRepository.searchByEmailAndUniversity(emailSearch, universityId);

        return usersInUniversity.stream()
                .filter(user -> clubMemberRepository.findActiveMembershipsByUserId(user.getUserId()).isEmpty()) // Başka kulüpte olanları çıkar
                .map(user -> UserSearchResponse.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .departmentName(user.getDepartments() != null ? user.getDepartments().getName() : null)
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void addMemberToClub(Long clubId, String email, Long adminUserId) {
        verifyAdminRole(clubId, adminUserId);

        Club club = clubRepository.findById(clubId).orElseThrow(() -> new RuntimeException("Kulüp bulunamadı"));
        User userToAdd = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

        if (userToAdd.getDepartments() == null || !userToAdd.getDepartments().getFaculty().getUniversity().getUniversityId().equals(club.getUniversity().getUniversityId())) {
            throw new UserNotInUniversityException("Kullanıcı kulübün bağlı olduğu üniversitede değil.");
        }

        List<ClubMember> activeMemberships = clubMemberRepository.findActiveMembershipsByUserId(userToAdd.getUserId());
        if (!activeMemberships.isEmpty()) {
            throw new UserAlreadyInClubException("Kullanıcı zaten başka bir kulüpte yer alıyor.");
        }

        ClubMember newMember = new ClubMember();
        ClubMemberId memberId = new ClubMemberId();
        memberId.setClubId(clubId);
        memberId.setUserId(userToAdd.getUserId());

        newMember.setId(memberId);
        newMember.setClub(club);
        newMember.setUser(userToAdd);
        newMember.setClubRole(ClubRole.MEMBER);
        newMember.setStartAt(LocalDateTime.now());

        clubMemberRepository.save(newMember);
    }

    @Transactional
    public void removeMemberFromClub(Long clubId, Long userIdToRemove, Long adminUserId) {
        verifyAdminRole(clubId, adminUserId);

        if (userIdToRemove.equals(adminUserId)) {
            throw new ClubPermissionDeniedException("Kendinizi silemezsiniz.");
        }

        ClubMember memberToRemove = clubMemberRepository.findActiveMembership(userIdToRemove, clubId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bu kulüpte değil."));

        if (memberToRemove.getClubRole().equals(ClubRole.ADMIN)) {
            throw new ClubPermissionDeniedException("Diğer yöneticileri silemezsiniz.");
        }

        memberToRemove.setEndAt(LocalDateTime.now());
        clubMemberRepository.save(memberToRemove);
    }
}
