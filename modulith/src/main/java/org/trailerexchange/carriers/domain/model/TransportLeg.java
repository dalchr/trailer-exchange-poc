package org.trailerexchange.carriers.domain.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.transportorders.domain.model.TransportOrder;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransportLeg implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private TransportOrder order;

    @ManyToOne
    @JoinColumn(name = "start_location_id")
    private Location startLocation;

    @ManyToOne
    @JoinColumn(name = "end_location_id")
    private Location endLocation;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;

    private TransportLegStatus status;

}
