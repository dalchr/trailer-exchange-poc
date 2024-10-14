package org.trailerexchange.transportorders.domain.service;

import java.util.Optional;
import org.trailerexchange.transportorders.domain.model.TransportOrder;
import org.trailerexchange.transportorders.dto.TransportOrderRequestDTO;

public interface TransportOrderService {

    TransportOrder createTransportOrder(TransportOrderRequestDTO orderRequest);

    TransportOrder splitLegsAndAssignCarriers(TransportOrder transportOrder);

    Optional<TransportOrder> findById(Long transportOrderId);

    TransportOrder save(TransportOrder transportOrder);

    void complete(TransportOrder transportOrder);
}
