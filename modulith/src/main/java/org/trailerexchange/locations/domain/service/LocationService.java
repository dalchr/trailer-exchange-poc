package org.trailerexchange.locations.domain.service;

import org.trailerexchange.locations.domain.model.Location;

public interface LocationService {

    Location findExchangeLocation(Location start, Location end);

    Location save(Location startLocation);
}
