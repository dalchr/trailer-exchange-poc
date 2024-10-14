package org.trailerexchange.carriers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.dto.AssignLegRequestDTO;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.transportorders.domain.model.TransportOrder;

@Service
public class CarrierClient {

    @Value("${carrier.api.baseUrl}")
    private String carrierApiUrl;

    public TransportLeg assignLegToCarrier(TransportOrder transportOrder, Location startLocation,
        Location endLocation) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            AssignLegRequestDTO requestDTO = AssignLegRequestDTO.builder()
                .transportOrder(transportOrder)
                .startLocation(startLocation)
                .endLocation(endLocation)
                .build();

            String assignCarrierForLegUrl = carrierApiUrl + "/assign-leg";

            return restTemplate
                .postForEntity(assignCarrierForLegUrl, requestDTO, TransportLeg.class)
                .getBody();

        } catch (RestClientException e) {
            throw new RuntimeException("Failed to retrieve status from carrier: " + e.getMessage());
        }
    }
}

