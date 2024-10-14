package org.trailerexchange.transportorders.domain.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportOrderRepository extends JpaRepository<TransportOrder, Long> {

}
