package org.trailerexchange.events.externalization;

import java.util.Objects;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.modulith.events.EventExternalizationConfiguration;
import org.springframework.modulith.events.RoutingTarget;
import org.trailerexchange.notifications.TransportOrderStatusUpdateEvent;
import org.trailerexchange.transportorders.domain.model.TransportStatus;

@Configuration
class EventExternalizationConfig {

    @Bean
    EventExternalizationConfiguration eventExternalizationConfiguration() {
        return EventExternalizationConfiguration.externalizing()
          .select(EventExternalizationConfiguration.annotatedAsExternalized())
          .route(
              TransportOrderStatusUpdateEvent.class,
            it -> RoutingTarget.forTarget("poc.transport.order.status.updated").andKey(it.status().name())
          )
          .mapping(
              TransportOrderStatusUpdateEvent.class,
            it -> new PostTransportStatusUpdateKafkaEvent(it.status(), it.transportOrderId())
          )
          .build();
    }

    @Bean
    KafkaOperations<String, TransportOrderStatusUpdateEvent> kafkaOperations(KafkaProperties kafkaProperties) {
        ProducerFactory<String, TransportOrderStatusUpdateEvent> producerFactory = new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
        return new KafkaTemplate<>(producerFactory);
    }

    record PostTransportStatusUpdateKafkaEvent(TransportStatus status, Long transportOrderId) {
        PostTransportStatusUpdateKafkaEvent {
            Objects.requireNonNull(status, "TransportOrder status must not be null!");
        }
    }

}

