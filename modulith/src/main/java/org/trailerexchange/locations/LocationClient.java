package org.trailerexchange.locations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.locations.dto.FindExchangeLocationRequestDTO;

@Service
public class LocationClient {

    @Value("${location.api.baseUrl}")
    private String locationApiUrl;

    public Location store(Location location) {
        RestTemplate restTemplate = new RestTemplate();
        String url = locationApiUrl + "/store";
        try {
            return restTemplate
                .postForEntity(url, location, Location.class)
                .getBody();  // Expected to be a status string (e.g., "In Transit", "Completed")
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to retrieve status from carrier: " + e.getMessage());
        }
    }

    public Location findExchangeLocation(Location start, Location end) {
        FindExchangeLocationRequestDTO payload = FindExchangeLocationRequestDTO.builder()
            .startLocation(start)
            .endLocation(end)
            .build();

        String url = locationApiUrl + "/exchange-location";

        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate
                .postForEntity(url, payload, Location.class)
                .getBody();  // Expected to be a status string (e.g., "In Transit", "Completed")
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to retrieve status from carrier: " + e.getMessage());
        }
    }
}
