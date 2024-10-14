package org.trailerexchange.transportorders;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.domain.service.TransportOrderManager;
import org.trailerexchange.transportorders.dto.TransportOrderRequestDTO;

@RestController
@RequestMapping("/api/transport-order")
public class TransportOrderController {

    private final TransportOrderManager transportOrderManager;

    @Autowired
    public TransportOrderController(TransportOrderManager transportOrderManager) {
        this.transportOrderManager = transportOrderManager;
    }

    @PostMapping(path = "/handle", produces = "application/json")
    @Operation(summary = "Creates transport order with two legs")
    public ResponseEntity<TransportOrder> handleTransportOrder(
        @RequestBody TransportOrderRequestDTO transportOrderRequest) {
        if (transportOrderRequest == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        TransportOrder transportOrder = transportOrderManager.createTransportOrder(
            transportOrderRequest);
        return ResponseEntity.ok(transportOrder);
    }

    @GetMapping(path = "/{transportOrderId}")
    @Operation(summary = "Finds transport order by id")
    public ResponseEntity<TransportOrder> findTransportOrder(@PathVariable Long transportOrderId) {
        try {
            return ResponseEntity.ok(transportOrderManager.findById(transportOrderId).get());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stored the transport order"),
        @ApiResponse(responseCode = "500", description = "Operation failed")
    })
    @PostMapping(path = "/save", produces = "application/json")
    @Operation(summary = "Stores transport order")
    public ResponseEntity<String> save(@RequestBody TransportOrder transportOrder) {
        try {
            TransportOrder stored = transportOrderManager.save(transportOrder);
            return ResponseEntity.ok(
                "Transport Order was stored successfully [id " + stored.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}


