package org.trailerexchange.carriers.domain.service;

import org.trailerexchange.carriers.domain.model.Carrier;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;
import org.trailerexchange.carriers.dto.AssignLegRequestDTO;
import org.trailerexchange.locations.domain.model.Location;

public interface CarrierService {

    Carrier getCarrierForRoute(Location start, Location end);

    void checkAndUpdateStatus(Long transportLegId, TransportLegStatus latestStatus);

    TransportLeg assignLegToCarrier(AssignLegRequestDTO assignLegRequest);
}
