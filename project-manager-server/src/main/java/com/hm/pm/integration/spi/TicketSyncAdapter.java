package com.hm.pm.integration.spi;

import java.time.Instant;
import java.util.List;

public interface TicketSyncAdapter {

    String systemCode();

    List<ExternalTicket> pullUpdatedTickets(Instant since);

    SyncResult pushLocalChanges(List<LocalTicketChange> changes);

    record ExternalTicket(String externalId,
                          String title,
                          String status,
                          String assignee,
                          String priority,
                          Instant updatedAt) {
    }

    record LocalTicketChange(String localId,
                             String title,
                             String status,
                             String assignee,
                             String priority,
                             Instant updatedAt) {
    }

    record SyncResult(Integer pulledCount, Integer pushedCount) {
    }
}
