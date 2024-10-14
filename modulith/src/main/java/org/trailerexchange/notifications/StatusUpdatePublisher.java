package org.trailerexchange.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class StatusUpdatePublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishStatusUpdate(TransportOrderStatusUpdateEvent event) {
        eventPublisher.publishEvent(event);
    }
}
