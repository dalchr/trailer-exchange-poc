package org.trailerexchange.carriers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.domain.model.TransportLegRepository;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;
import org.trailerexchange.carriers.domain.service.CarrierService;
import org.trailerexchange.carriers.dto.AssignLegRequestDTO;

@RestController
@RequestMapping("/api/carrier")
public class CarrierController {

    @Autowired
    private TransportLegRepository transportLegRepository;

    @Autowired
    private CarrierService carrierService;

    @PostMapping(path = "/assign-leg", produces = "application/json")
    @Operation(summary = "Assigns a transport leg to a carrier")
    public ResponseEntity<TransportLeg> assignLegToCarrier(
        @RequestBody AssignLegRequestDTO assignLeg) {
        return ResponseEntity.ok(carrierService.assignLegToCarrier(assignLeg));
    }

    @GetMapping(path = "/status/{transportLegId}")
    @Operation(summary = "Retrieves the status of a leg")
    public ResponseEntity<TransportLegStatus> getStatus(@PathVariable Long transportLegId) {
        try {
            TransportLeg transportLeg = transportLegRepository
                .findById(transportLegId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No transport order leg found with id" + transportLegId));

            return ResponseEntity.ok(transportLeg.getStatus());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
