package com.hakankuru.EventTime.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventCreatedEvent extends ApplicationEvent {
    private final Long eventId;
    private final Long clubId;
    private final String title;

    public EventCreatedEvent(Object source, Long eventId, Long clubId, String title) {
        super(source);
        this.eventId = eventId;
        this.clubId = clubId;
        this.title = title;
    }
}
