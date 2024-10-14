package org.trailerexchange.carriers.domain.service;

import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.ApplicationModule;
import org.springframework.stereotype.Service;
import org.trailerexchange.carriers.domain.model.Carrier;
import org.trailerexchange.carriers.domain.model.CarrierRepository;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.domain.model.TransportLegRepository;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;
import org.trailerexchange.carriers.dto.AssignLegRequestDTO;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.notifications.StatusUpdatePublisher;
import org.trailerexchange.notifications.TransportOrderStatusUpdateEvent;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.domain.model.TransportStatus;

@ApplicationModule(displayName = "carriers")
@Service
public class CarrierServiceImpl implements CarrierService {

    private static final Logger LOGGER = Logger.getLogger(CarrierServiceImpl.class.getName());

    @Autowired
    private CarrierRepository carrierRepository;  // Repository for fetching carriers

    @Autowired
    private StatusUpdatePublisher statusUpdatePublisher;  // Kafka Publisher

    @Autowired
    private TransportLegRepository transportLegs;
    @Autowired
    private TransportLegRepository transportLegRepository;

    @Override
    public Carrier getCarrierForRoute(Location start, Location end) {
        // Fetching carriers operating in the start region
        List<Carrier> carriers = carrierRepository.findAll();
        // TODO: List<Carrier> carriers = carrierRepository.findCarriersByRegion(start, end);

        // If no carriers are available, handle error
        if (carriers.isEmpty()) {
            throw new IllegalArgumentException("No carriers available for this route.");
        }

        // Business logic for selecting the most suitable carrier
        return selectBestCarrier(carriers, start, end);
    }

    @Override
    public void checkAndUpdateStatus(Long transportLegId, TransportLegStatus latestStatus) {
        transportLegs.findById(transportLegId)
            .ifPresent(leg -> {
                if (isStatusUpdateRequired(latestStatus, leg.getStatus())) {
                    leg.setStatus(latestStatus);
                    transportLegRepository.save(leg);

                    if (latestStatus.equals(TransportLegStatus.PENDING)) {
                        emulateTransport(leg);

                        // ... Notify transport order update
                        TransportOrderStatusUpdateEvent statusUpdateEvent = TransportOrderStatusUpdateEvent.builder()
                            .status(TransportStatus.DISPATCHING_TO_NEXT_OR_COMPLETED)
                            .transportOrderId(leg.getOrder().getId())
                            .build();

                        statusUpdatePublisher.publishStatusUpdate(statusUpdateEvent);
                    }
                }
            });
    }

    @Override
    public TransportLeg assignLegToCarrier(AssignLegRequestDTO assignLegRequest) {
        TransportOrder transportOrder = assignLegRequest.getTransportOrder();
        Location startLocation = assignLegRequest.getStartLocation();
        Location endLocation = assignLegRequest.getEndLocation();

        Carrier carrierForRoute = getCarrierForRoute(startLocation, endLocation);

        TransportLeg transportLeg = TransportLeg.builder()
            .order(transportOrder)
            .carrier(carrierForRoute)
            .startLocation(startLocation)
            .endLocation(endLocation)
            .status(TransportLegStatus.REQUESTED)
            .build();

        return transportLeg;
    }

    private void emulateTransport(TransportLeg leg) {
        /**
         * ... EMULATING - time passes and that the transport completes...
         */
        leg.setStatus(TransportLegStatus.IN_PROGRESS);
        transportLegRepository.save(leg);

        // ... Carrier does the transport of the cargo between start- and end locations ...

        leg.setStatus(TransportLegStatus.COMPLETED);
        transportLegRepository.save(leg);

        LOGGER.info(
            "Carrier: " + leg.getCarrier().getName()
                + " [COMPLETED TRANSPORT LEG"
                + " with id: " + leg.getId()
                + " for order " + leg.getOrder().getId()
                + " between " + leg.getStartLocation()
                + " and " + leg.getEndLocation()
                + "]");
    }

    private boolean isStatusUpdateRequired(TransportLegStatus latestStatus,
        TransportLegStatus currentStatus) {
        // Define conditions under which we should notify others (e.g., status changed to "Completed")
        return !latestStatus.equals(currentStatus);
    }

    private Carrier selectBestCarrier(List<Carrier> carriers, Location start, Location end) {

        LOGGER.info("BEST CARRIER HAS NOT BEEN IMPLEMENTED AND WILL ALWAYS RETURN THE SAME CARRIER");

        /*
        List<Carrier> selectableCarriers = carriers.stream().filter(carrier ->
                LocationServiceImpl.isLocationBetween(start, end, carrier.getStartLocation(), toleranceInKm)
                    || LocationServiceImpl.isLocationBetween(start, end, carrier.getEndLocation(), toleranceInKm))
            .toList();
         */

        // TODO: apply rules here, e.g., sustainability profile, capacity or speed (etc)
        // Example logic to choose the first available carrier (you can enhance this logic)
        return carriers.get(0);  // Or implement more sophisticated logic
    }
}

