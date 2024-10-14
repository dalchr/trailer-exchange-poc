package org.trailerexchange.transportorders.domain.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.ApplicationModule;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trailerexchange.carriers.CarrierClient;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.domain.model.TransportLegRepository;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.locations.domain.service.LocationService;
import org.trailerexchange.transportorders.domain.model.Buyer;
import org.trailerexchange.transportorders.domain.model.BuyerRepository;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.domain.model.TransportOrderRepository;
import org.trailerexchange.transportorders.domain.model.TransportStatus;
import org.trailerexchange.transportorders.dto.TransportOrderRequestDTO;

@ApplicationModule(displayName = "transportorder")
@Service
public class TransportOrderServiceImpl implements TransportOrderService {

    private static final Logger logger = Logger.getLogger(TransportOrderServiceImpl.class.getName());

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CarrierClient carrierClient;

    @Autowired
    private TransportOrderRepository transportOrderRepository;

    @Autowired
    private TransportLegRepository transportLegRepository;

    private static String generateOrderNo() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    @Transactional
    public TransportOrder createTransportOrder(TransportOrderRequestDTO transportOrderRequest) {

        //... Obtain buyer
        Buyer buyer = buyerRepository.findById(transportOrderRequest.getBuyerId()).orElseThrow(
            () -> new IllegalArgumentException(
                "Cannot find buyer " + transportOrderRequest.getBuyerId()));

        // ... Get starting and endpoint locations
        Location startLocation = locationService.save(transportOrderRequest.getStartLocation());
        Location endLocation = locationService.save(transportOrderRequest.getEndLocation());

        // ... Find an exchange location based on the start and end points
        Location exchangeLocation = locationService.findExchangeLocation(startLocation,
            endLocation);
        locationService.save(exchangeLocation);

        TransportOrder transportOrder = new TransportOrder();
        transportOrder.setOrderNo(generateOrderNo());
        transportOrder.setBuyer(buyer);
        transportOrder.setStartLocation(startLocation);
        transportOrder.setEndLocation(endLocation);
        transportOrder.setStatus(TransportStatus.ASSIGNED_TO_FIRST_CARRIER);
        transportOrder.setLegs(new ArrayList<>());

        return transportOrderRepository.save(transportOrder);
    }

    @Transactional
    public TransportOrder splitLegsAndAssignCarriers(TransportOrder transportOrder) {

        // ... Get starting and endpoint locations
        Location startLocation = transportOrder.getStartLocation();
        Location endLocation = transportOrder.getEndLocation();

        // ... Find an exchange location based on the start and end points
        Location exchangeLocation = locationService.findExchangeLocation(startLocation,
            endLocation);

        // ... Split into two legs and assign carriers to each
        TransportLeg firstCarrier = carrierClient.assignLegToCarrier(transportOrder, startLocation,
            exchangeLocation);
        TransportLeg secondCarrier = carrierClient.assignLegToCarrier(transportOrder,
            exchangeLocation, endLocation);

        // ... Create a full transport order with two legs
        transportOrder.addLeg(transportLegRepository.save(firstCarrier));
        transportOrder.addLeg(transportLegRepository.save(secondCarrier));

        return transportOrderRepository.save(transportOrder);
    }

    @Override
    public void complete(TransportOrder transportOrder) {
        transportOrder.setStatus(TransportStatus.TRANSPORT_COMPLETED);
        save(transportOrder);

        System.out.println(
            "===============================================================================)");
        System.out.println(
            "Transport Order ID " + transportOrder.getId() + ": " + transportOrder.getStatus());
        System.out.println(
            "===============================================================================)");
    }

    @Override
    public Optional<TransportOrder> findById(Long transportOrderId) {
        return transportOrderRepository.findById(transportOrderId);
    }

    @Override
    @Transactional
    public TransportOrder save(TransportOrder transportOrder) {
        return transportOrderRepository.save(transportOrder);
    }
}
