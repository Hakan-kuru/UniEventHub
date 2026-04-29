package com.hakankuru.EventTime.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String title;
    private String description;
    private String image;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;

    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;
}