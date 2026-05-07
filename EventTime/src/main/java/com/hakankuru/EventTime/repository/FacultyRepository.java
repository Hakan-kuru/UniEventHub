package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultyRepository
        extends JpaRepository<Faculty, Long> {
}