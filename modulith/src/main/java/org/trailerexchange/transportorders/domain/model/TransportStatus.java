package org.trailerexchange.transportorders.domain.model;

import java.io.Serializable;

public enum TransportStatus implements Serializable {
    TRANSPORT_REQUESTED,
    ASSIGNED_TO_FIRST_CARRIER,
    DISPATCHED_FROM_CARRIER,
    DISPATCHING_TO_NEXT_OR_COMPLETED,
    TRANSPORT_COMPLETED
}
