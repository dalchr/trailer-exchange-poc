package org.trailerexchange.carriers.dto;


import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.transportorders.domain.model.TransportOrder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignLegRequestDTO implements Serializable {

    private TransportOrder transportOrder;
    private Location startLocation;
    private Location endLocation;
}
