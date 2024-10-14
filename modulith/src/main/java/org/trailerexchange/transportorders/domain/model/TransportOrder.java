package org.trailerexchange.transportorders.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import org.trailerexchange.carriers.domain.model.TransportLeg;
import org.trailerexchange.carriers.domain.model.TransportLegStatus;
import org.trailerexchange.locations.domain.model.Location;

@Entity
@Data
public class TransportOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderNo;

    private TransportStatus status;

    @ManyToOne
    private Buyer buyer;

    @ManyToOne
    private Location startLocation;

    @ManyToOne
    private Location endLocation;

    @OneToMany
    private List<TransportLeg> legs;

    public void addLeg(TransportLeg leg) {
        this.legs.add(leg);
    }

    public boolean isFirstLegCompleted() {
        if (getLegs().isEmpty()) {
            return false;
        }
        return TransportLegStatus.COMPLETED.equals(getLegs().get(0).getStatus());
    }

    public boolean isSecondLegCompleted() {
        if (getLegs().size() < 2) {
            return false;
        }
        return TransportLegStatus.COMPLETED.equals(getLegs().get(1).getStatus());
    }

    public TransportLeg getSecondLeg() {
        List<TransportLeg> transportLegs = getLegs();
        if (transportLegs.size() != 2) {
            return null;
        }
        return transportLegs.get(1);
    }
}
