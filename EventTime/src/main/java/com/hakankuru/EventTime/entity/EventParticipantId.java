package com.hakankuru.EventTime.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class EventParticipantId implements Serializable {
    private Long userId;
    private Long eventId;
}