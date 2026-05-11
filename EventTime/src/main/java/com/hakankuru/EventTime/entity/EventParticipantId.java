package com.hakankuru.EventTime.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class EventParticipantId implements Serializable {

    private Long userId;
    private Long eventId;

    public EventParticipantId() {}

    public EventParticipantId(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
}