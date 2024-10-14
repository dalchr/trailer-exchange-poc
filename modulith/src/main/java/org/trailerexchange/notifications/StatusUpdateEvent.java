package org.trailerexchange.notifications;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;
import org.trailerexchange.transportorders.domain.model.TransportStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateEvent implements Serializable {

    private Long transportOrderId;
    private String transportLegId;
    private TransportStatus orderStatus; // e.g., "In Transit", "Completed"
    private TransportLegStatus legStatus; // e.g., "In Transit", "Completed"
    private String carrierName;


}
