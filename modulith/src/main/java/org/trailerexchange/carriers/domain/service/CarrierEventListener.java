package org.trailerexchange.carriers.domain.service;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.trailerexchange.notifications.TransportLegStatusUpdateEvent;

@Service
public class CarrierEventListener {

    private static final Logger LOGGER = Logger.getLogger(CarrierEventListener.class.getName());

    private final CarrierService carrierService;

    @Autowired
    public CarrierEventListener(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @EventListener
    public void handleTransportStatusUpdate(TransportLegStatusUpdateEvent event) {
        LOGGER.info("Received status update for transport order " + event.transportLegId() + ": " + event.status());

        carrierService.checkAndUpdateStatus(event.transportLegId(), event.status());
    }
}
