package org.trailerexchange.events.externalization.infra;

import java.time.Duration;
import java.time.Instant;
import org.springframework.modulith.events.CompletedEventPublications;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.stereotype.Component;
import org.trailerexchange.notifications.TransportOrderStatusUpdateEvent;

@Component
class PublicationEvents {
	private final IncompleteEventPublications incompleteEvent;
	private final CompletedEventPublications completeEvents;

	public PublicationEvents(IncompleteEventPublications incompleteEvent, CompletedEventPublications completeEvents) {
		this.incompleteEvent = incompleteEvent;
		this.completeEvents = completeEvents;
	}

	public void resubmitUnpublishedEvents() {
		incompleteEvent.resubmitIncompletePublicationsOlderThan(Duration.ofSeconds(60));

		// or
		incompleteEvent.resubmitIncompletePublications(it ->
		  it.getPublicationDate().isBefore(Instant.now().minusSeconds(60))
		    && it.getEvent() instanceof TransportOrderStatusUpdateEvent);
	}

	public void clearPublishedEvents() {
		completeEvents.deletePublicationsOlderThan(Duration.ofSeconds(60));

		// or
		completeEvents.deletePublications(it ->
		  it.getPublicationDate().isBefore(Instant.now().minusSeconds(60))
		    && it.getEvent() instanceof TransportOrderStatusUpdateEvent);
	}
}
