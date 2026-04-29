package com.hakankuru.EventTime.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facultyId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;
}