package com.hakankuru.EventTime.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clubs")
@Getter
@Setter
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