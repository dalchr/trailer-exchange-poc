package org.trailerexchange.locations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.locations.domain.service.LocationService;
import org.trailerexchange.locations.dto.FindExchangeLocationRequestDTO;


@RestController
@RequestMapping("/api/location")
@Tag(name = "Location Controller", description = "Location API endpoints (v1)")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping(path = "/store", produces = "application/json")
    private ResponseEntity<Location> saveLocation(Location location) {
        return ResponseEntity.ok(locationService.save(location));
    }

    @PostMapping(path = "/exchange-location", produces = "application/json")
    @Operation(summary = "Finds exchange location between given start and end locations")
    public ResponseEntity<Location> findExchangeLocation(
        @RequestBody FindExchangeLocationRequestDTO request) {
        Location exchangeLocation = locationService
            .findExchangeLocation(request.getStartLocation(), request.getEndLocation());
        return ResponseEntity.ok(exchangeLocation);
    }
}
