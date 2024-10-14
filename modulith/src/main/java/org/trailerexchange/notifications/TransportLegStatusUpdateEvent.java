package org.trailerexchange.notifications;

import lombok.Builder;
import org.springframework.modulith.events.Externalized;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;

@Builder
@Externalized("poc.transport.leg.status.updated::#{status()}")
public record TransportLegStatusUpdateEvent(Long transportLegId, TransportLegStatus status) {

}
