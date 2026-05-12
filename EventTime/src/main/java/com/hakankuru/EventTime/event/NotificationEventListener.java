package com.hakankuru.EventTime.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Async
    @EventListener
    public void handleEventCreatedEvent(EventCreatedEvent event) {
        // Here we would implement scalable notification logic.
        // E.g., pushing to a message broker (RabbitMQ/Kafka) or sending push notifications
        // to users who are members of event.getClubId().
        
        System.out.println("Async Notification triggered for new event: " + event.getTitle() + " in club: " + event.getClubId());
    }
}
