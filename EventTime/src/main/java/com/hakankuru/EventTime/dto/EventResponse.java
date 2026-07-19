package com.hakankuru.EventTime.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long eventId;
    private String title;
    private String description;
    private String image;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime applyStartAt;
    private LocalDateTime applyEndAt;
    private Integer capacity;
    private Long clubId;
    private String clubName;
}
