package org.trailerexchange.transportorders.dto;


import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.trailerexchange.locations.domain.model.Location;

@Getter
@Setter
public class TransportOrderRequestDTO implements Serializable {

    private Long buyerId;
    private Location startLocation;
    private Location endLocation;

}
