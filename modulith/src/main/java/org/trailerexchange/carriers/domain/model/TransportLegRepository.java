package org.trailerexchange.carriers.domain.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportLegRepository extends JpaRepository<TransportLeg, Long> {

    List<TransportLeg> findByOrder_Id(Long orderId);
}
