package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.ClubMember;
import com.hakankuru.EventTime.entity.ClubMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, ClubMemberId> {

    @Query("SELECT cm FROM ClubMember cm WHERE cm.user.userId = :userId AND cm.club.clubId = :clubId AND cm.endAt IS NULL")
    Optional<ClubMember> findActiveMembership(Long userId, Long clubId);

    @Query("SELECT cm FROM ClubMember cm WHERE cm.user.userId = :userId AND cm.endAt IS NULL")
    List<ClubMember> findActiveMembershipsByUserId(Long userId);

    @Query("SELECT cm FROM ClubMember cm WHERE cm.club.clubId = :clubId AND cm.endAt IS NULL")
    List<ClubMember> findActiveMembershipsByClubId(Long clubId);
    
    @Query("SELECT COUNT(cm) FROM ClubMember cm WHERE cm.club.clubId = :clubId AND cm.clubRole = 'ADMIN' AND cm.endAt IS NULL")
    long countActiveAdminsByClubId(Long clubId);
}
