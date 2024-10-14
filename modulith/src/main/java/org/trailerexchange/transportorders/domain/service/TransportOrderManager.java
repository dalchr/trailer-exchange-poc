package org.trailerexchange.transportorders.domain.service;

import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.trailerexchange.notifications.TransportOrderStatusUpdateEvent;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.domain.model.TransportStatus;
import org.trailerexchange.transportorders.dto.TransportOrderRequestDTO;

/**
 * TransportOrderManager adds a seemingly excessive layer to the application layering. Its purpose
 * is however, is to terminates the transaction boundary between transport order creation and leg
 * assignment.
 */
@Service
public class TransportOrderManager {

    private static final Logger LOGGER = Logger.getLogger(TransportOrderManager.class.getName());

    private final TransportOrderService transportOrderService;

    private final ApplicationEventPublisher eventPublisher;

    public TransportOrderManager(TransportOrderService transportOrderService,
        ApplicationEventPublisher eventPublisher) {
        this.transportOrderService = transportOrderService;
        this.eventPublisher = eventPublisher;
    }

    public TransportOrder createTransportOrder(TransportOrderRequestDTO transportOrderRequest) {

        TransportOrder createdOrder = transportOrderService.createTransportOrder(
            transportOrderRequest);

        LOGGER.info("[Transport Order] Created:" + createdOrder);

        TransportOrder assignedOrder = transportOrderService.splitLegsAndAssignCarriers(
            createdOrder);

        LOGGER.info("[Transport Order] Split and assigned to carriers: " + assignedOrder);

        requestTransport(createdOrder);

        return createdOrder;
    }

    private void requestTransport(TransportOrder createdOrder) {
        TransportOrderStatusUpdateEvent updateEvent = TransportOrderStatusUpdateEvent.builder()
            .transportOrderId(createdOrder.getId())
            .status(TransportStatus.TRANSPORT_REQUESTED)
            .build();

        eventPublisher.publishEvent(updateEvent);
    }

    public Optional<TransportOrder> findById(Long transportOrderId) {
        return transportOrderService.findById(transportOrderId);
    }

    public TransportOrder save(TransportOrder transportOrder) {
        return transportOrderService.save(transportOrder);
    }
}
