package org.trailerexchange.locations.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.trailerexchange.locations.domain.model.Location;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindExchangeLocationRequestDTO implements Serializable {

    private Location startLocation;

    private Location endLocation;
}
