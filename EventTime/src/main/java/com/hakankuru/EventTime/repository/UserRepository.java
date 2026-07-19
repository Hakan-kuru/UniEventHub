package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.departments.faculty.university.universityId = :universityId AND LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))")
    List<User> searchByEmailAndUniversity(@Param("email") String email, @Param("universityId") Long universityId);
}
