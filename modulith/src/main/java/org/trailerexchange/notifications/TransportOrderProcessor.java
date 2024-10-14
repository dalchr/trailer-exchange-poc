package org.trailerexchange.notifications;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.domain.model.TransportStatus;
import org.trailerexchange.transportorders.domain.service.TransportOrderService;

@Service
public class TransportOrderProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportOrderProcessor.class);

    @Autowired
    private TransportOrderService transportOrderService;

    @Autowired
    private CarrierNotificationService carrierNotificationService;

    @EventListener
    @Transactional
    public void handleStatusUpdate(TransportOrderStatusUpdateEvent event) {
        TransportStatus latestStatusUpdate = event.status();

        System.out.println("Received status update for transport order " + event.transportOrderId() + ": " + latestStatusUpdate);

        TransportOrder transportOrder = updateTransportOrderStatus(event, latestStatusUpdate);

        switch (latestStatusUpdate) {
            case TRANSPORT_REQUESTED -> {
                notifyFirstCarrier(transportOrder);
            }
            case DISPATCHED_FROM_CARRIER -> {
                Long firstTransportLegId = transportOrder.getLegs().iterator().next().getId();
                carrierNotificationService
                    .notifyCarrier(firstTransportLegId, TransportLegStatus.PENDING,
                        "Transport status for first leg completed. Prepare for second leg.");
            }
            case DISPATCHING_TO_NEXT_OR_COMPLETED -> {
                if (transportOrder.isSecondLegCompleted()) {
                    transportOrderService.complete(transportOrder);
                } else if (transportOrder.isFirstLegCompleted()) {
                    notifySecondLegCarrier(transportOrder.getSecondLeg());
                }
            }
        }
    }

    private TransportOrder updateTransportOrderStatus(TransportOrderStatusUpdateEvent event,
        TransportStatus latestStatusUpdate) {
        TransportOrder transportOrder = transportOrderService
            .findById(event.transportOrderId())
            .orElseThrow(() -> new RuntimeException(
                "Transport Order not found: " + event.transportOrderId()));

        transportOrder.setStatus(latestStatusUpdate);
        transportOrderService.save(transportOrder);

        LOGGER.info("Transport Order ID {} status updated to {}", transportOrder.getId(), transportOrder.getStatus());

        return transportOrder;
    }

    private void notifyFirstCarrier(TransportOrder transportOrder) {
        List<TransportLeg> transportLegs = transportOrder.getLegs();
        TransportLeg firstLeg = transportLegs.iterator().next();

        carrierNotificationService.notifyCarrier(firstLeg.getId(), TransportLegStatus.PENDING,
            "Transport status for first leg completed. Prepare for second leg.");
    }

    /**
     * Notify the second carrier responsible for the second leg of the transport.
     *
     * @param secondLeg The second leg of the transport order for which to notify the second leg
     *                  carrier.
     */
    private void notifySecondLegCarrier(TransportLeg secondLeg) {
        LOGGER.info("Notifying second carrier (ID: {}) for the second leg of transport order: {}",
            secondLeg.getId(), secondLeg.getOrder().getId());

        carrierNotificationService.notifyCarrier(secondLeg.getId(), TransportLegStatus.PENDING,
            "Transport status for first leg completed. Prepare for second leg.");
    }
}
