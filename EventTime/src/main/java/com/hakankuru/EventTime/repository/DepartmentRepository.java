package com.hakankuru.EventTime.repository;

import com.hakankuru.EventTime.entity.Departments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository
        extends JpaRepository<Departments, Long> {
}