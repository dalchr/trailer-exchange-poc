package org.trailerexchange.notifications;

import lombok.Builder;
import org.springframework.modulith.events.Externalized;
import org.trailerexchange.transportorders.domain.model.TransportStatus;

@Builder
@Externalized("poc.transport.order.status.updated::#{status()}")
public record TransportOrderStatusUpdateEvent(Long transportOrderId, TransportStatus status) {

}
