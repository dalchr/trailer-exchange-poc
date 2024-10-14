package org.trailerexchange.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;

@Service
public class CarrierNotificationService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void notifyCarrier(Long transportLegId, TransportLegStatus status, String message) {
        TransportLegStatusUpdateEvent legStatusUpdateEvent = TransportLegStatusUpdateEvent.builder()
            .transportLegId(transportLegId)
            .status(status)
            .build();

        eventPublisher.publishEvent(legStatusUpdateEvent);
    }
}
