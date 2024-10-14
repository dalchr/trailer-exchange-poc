package org.trailerexchange.data;

import static org.trailerexchange.data.PredefinedDataFixtures.*;
import static org.trailerexchange.data.PredefinedDataFixtures.dbSchenker;
import static org.trailerexchange.data.PredefinedDataFixtures.postenNorge;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trailerexchange.carriers.domain.model.Carrier;
import org.trailerexchange.carriers.domain.model.CarrierRepository;
import org.trailerexchange.locations.domain.model.LocationRepository;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.locations.domain.model.Region;
import org.trailerexchange.locations.domain.model.RegionRepository;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.transportorders.domain.model.Buyer;
import org.trailerexchange.transportorders.domain.model.BuyerRepository;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.domain.model.TransportOrderRepository;
import org.trailerexchange.transportorders.domain.model.TransportStatus;

@Service
public class PredefinedDataLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CarrierRepository carrierRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private TransportOrderRepository transportOrderRepository;

    private Location oslo;
    private Location goteborg;
    private Location sarpsborg;
    private Region osloSarpborg;
    private Region goteborgSarpborg;
    private Carrier postenNorge;
    private Carrier dbSchenker;

    private Buyer johnA;
    private Buyer johnB;
    private Buyer johnC;
    private Buyer johnD;

    @Transactional
    public void setupPredefinedData(){
        setupLocations();
        setupRegions();
        setupCarriers();

        setupBuyers();
        setupTransportOrders();
    }

    private void setupCarriers() {
        List<Region> dbSchenkerRegions = new ArrayList<>();
        dbSchenkerRegions.add(goteborgSarpborg);

        Carrier carrierOne = postenNorge(List.of(osloSarpborg));
        carrierOne.setStartLocation(oslo);
        carrierOne.setEndLocation(sarpsborg);
        postenNorge = carrierRepository.save(carrierOne);

        Carrier carrierTwo = dbSchenker(List.of(goteborgSarpborg));
        carrierTwo.setStartLocation(goteborg);
        carrierTwo.setEndLocation(sarpsborg);
        dbSchenker = carrierRepository.save(carrierTwo);

        // TODO: re-define regions
    }

    private void setupRegions() {
        osloSarpborg = regionRepository.save(osloSarpsborg());

        goteborgSarpborg = new Region();
        goteborgSarpborg.setName("Goteborg-Sarpsborg");
        goteborgSarpborg = regionRepository.save(goteborgSarpborg);
    }

    private void setupLocations() {
        oslo = locationRepository.save(oslo());
        goteborg = locationRepository.save(goteborg());
        sarpsborg = locationRepository.save(sarpsborg());
    }

    private void setupBuyers() {
        johnA = buyerRepository.save(johnA());
        johnB = buyerRepository.save(johnB());
        johnC = buyerRepository.save(johnC());
        johnD = buyerRepository.save(johnD());
    }

    private void setupTransportOrders() {
        createTransportOrder();
    }

    private void createTransportOrder() {
        TransportOrder transportOrder = new TransportOrder();
        transportOrder.setOrderNo("A123B120");
        ArrayList<TransportLeg> legs = new ArrayList<>();
        transportOrder.setLegs(legs);
        transportOrder.setStatus(TransportStatus.TRANSPORT_REQUESTED);
        transportOrder.setStartLocation(oslo);
        transportOrder.setEndLocation(goteborg);
        transportOrder.setBuyer(johnD);
        transportOrderRepository.save(transportOrder);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        setupPredefinedData();
    }
}
