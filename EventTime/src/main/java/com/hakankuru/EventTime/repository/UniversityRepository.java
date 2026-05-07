package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniversityRepository
        extends JpaRepository<University, Long> {
}