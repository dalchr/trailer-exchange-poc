package org.trailerexchange.data;

import java.util.List;
import org.trailerexchange.carriers.domain.model.Carrier;
import org.trailerexchange.locations.domain.model.Location;
import org.trailerexchange.locations.domain.model.Region;
import org.trailerexchange.transportorders.domain.model.Buyer;

public class PredefinedDataFixtures {

    public static Buyer johnA() {
        return Buyer.builder()
            .id(100L)
            .firstName("John")
            .lastName("A")
            .email("john.a@yahoo.com")
            .build();
    }

    public static Buyer johnB() {
        return Buyer.builder()
            .id(101L)
            .firstName("John")
            .lastName("B")
            .email("john.b@yahoo.com")
            .build();
    }
    public static Buyer johnC() {
        return Buyer.builder()
            .id(102L)
            .firstName("John")
            .lastName("C")
            .email("john.c@yahoo.com")
            .build();
    }

    public static Buyer johnD() {
        return Buyer.builder()
            .id(103L)
            .firstName("John")
            .lastName("D")
            .email("john.d@yahoo.com")
            .build();
    }

    public static Location oslo() {
        return Location.builder()
            .latitude(59.9139)
            .longitude(10.7522)
            .build();
    }

    public static Location goteborg() {
        return Location.builder()
            .latitude(57.7089)
            .longitude(11.9746)
            .build();
    }

    public static Location sarpsborg() {
        return Location.builder()
            .latitude(59.2840)
            .longitude(11.1096)
            .build();
    }

    public static Region osloSarpsborg() {
        return Region.builder()
            .name("Oslo-Sarpsborg")
            .build();
    }

    public static Carrier dbSchenker(List<Region> regions) {
        return Carrier.builder()
            .name("DB Schenker")
            // .regions(regions)
            .build();
    }

    public static Carrier postenNorge(List<Region> regions) {
        return Carrier.builder()
            .name("Posten Norge")
            // .regions(regions)
            .build();
    }
}
