package com.hakankuru.EventTime.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubId;

    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "university_id")
    private University university;
}