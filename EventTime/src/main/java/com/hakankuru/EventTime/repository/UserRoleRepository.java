package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUser_UserId(Long userId);

    Optional<UserRole> findByUser_UserIdAndRole(Long userId, String role);

    List<UserRole> findByRole(String role);
}
