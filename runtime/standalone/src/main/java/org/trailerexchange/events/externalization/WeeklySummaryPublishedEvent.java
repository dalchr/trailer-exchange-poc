package org.trailerexchange.events.externalization;

import org.springframework.modulith.events.Externalized;

@Externalized
record WeeklySummaryPublishedEvent(String handle, String heading) {
}
