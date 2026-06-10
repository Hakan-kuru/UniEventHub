package com.hakankuru.EventTime.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventParticipantId that = (EventParticipantId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventId);
    }
}