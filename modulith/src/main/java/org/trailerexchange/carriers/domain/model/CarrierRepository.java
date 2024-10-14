package org.trailerexchange.carriers.domain.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {

    /*
    TODO: [improvement] add support for find carriers by region:
    @Query("SELECT c FROM Carrier c WHERE :startLocation IN c.regions AND :endLocation IN c.regions")
    List<Carrier> findCarriersByRegion(@Param("startLocation") Location startLocation, @Param("endLocation") Location endLocation);
    */
}

