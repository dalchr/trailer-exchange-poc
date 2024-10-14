package org.trailerexchange.events.externalization.listener;

import java.util.ArrayList;
import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TestListener {
    private List<String> events = new ArrayList<>();

    @KafkaListener(id = "test-id", topics = "poc.transport.order.status.updated")
    public void listen(String event) {
        events.add(event);
    }

    public List<String> getEvents() {
        return events;
    }

    public void reset() {
        events = new ArrayList<>();
    }
}
