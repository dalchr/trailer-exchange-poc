package org.trailerexchange.events.externalization.infra;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.modulith.events.Externalized;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.Assert;
import org.trailerexchange.notifications.TransportOrderStatusUpdateEvent;

@Component
@Externalized
class TransportStatusUpdateEventPublishedKafkaProducer {

    private final KafkaOperations<String, TransportOrderStatusUpdateEvent> messageProducer;

    public TransportStatusUpdateEventPublishedKafkaProducer(KafkaOperations<String, TransportOrderStatusUpdateEvent> messageProducer) {
        this.messageProducer = messageProducer;
    }

    @EventListener
    public void publish(TransportOrderStatusUpdateEvent event) {
        Assert.notNull(event.status(), "Event status must not be null!");
        messageProducer.send("poc.transport.order.status.updated", event);
    }

    @Async
    @TransactionalEventListener
    public void publishAsync(TransportOrderStatusUpdateEvent orderUpdate) {
        Assert.notNull(orderUpdate.status(), "Event status must not be null!");
        messageProducer.send("poc.transport.order.status.updated", orderUpdate.status().name(), orderUpdate);
    }
}
