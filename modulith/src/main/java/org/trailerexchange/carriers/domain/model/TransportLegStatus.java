package org.trailerexchange.carriers.domain.model;

import java.io.Serializable;

public enum TransportLegStatus implements Serializable {
    REQUESTED, PENDING, IN_PROGRESS, COMPLETED
}
