package org.trailerexchange.locations.domain.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.locations.domain.model.LocationRepository;

@Service
public class LocationServiceImpl implements LocationService {

    @Value("${location.service.toleranceInKm}")
    private Integer toleranceInKm;

    @Autowired
    private LocationRepository locationRepository;

    public static boolean isLocationBetween(Location startLocation, Location endLocation,
        Location midLocation, double toleranceKm) {
        double totalDistance = haversineDistance(startLocation, endLocation);
        double distanceStartToMid = haversineDistance(startLocation, midLocation);
        double distanceMidToEnd = haversineDistance(midLocation, endLocation);

        // Check if the sum of the distances from start->mid + mid->end equals the total distance with a tolerance
        return Math.abs((distanceStartToMid + distanceMidToEnd) - totalDistance) <= toleranceKm;
    }

    public static double haversineDistance(Location loc1, Location loc2) {
        final int EARTH_RADIUS_KM = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double lonDistance = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(
            Math.toRadians(loc2.getLatitude()))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c; // distance in kilometers
    }

    public Location findExchangeLocation(Location start, Location end) {
        List<Location> exchangeLocations = locationRepository.findAll();
        Location exchangeLocation = findExchangeLocation(start, end, exchangeLocations);
        return exchangeLocation;
    }

    @Override
    @Transactional
    public Location save(Location location) {
        return locationRepository.save(location);
    }

    private Location findExchangeLocation(Location start, Location end,
        List<Location> exchangeLocations) {
        return exchangeLocations.stream()
            .filter(location -> isLocationBetween(start, end, location, toleranceInKm))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No exchange location found"));
    }
}

