package com.hm.pm.integration.adapters.vendor;

import com.hm.pm.integration.spi.TicketSyncAdapter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class VendorClient {

    public List<TicketSyncAdapter.ExternalTicket> pullUpdatedTickets(Instant since) {
        return List.of();
    }

    public TicketSyncAdapter.SyncResult pushChanges(List<TicketSyncAdapter.LocalTicketChange> changes) {
        return new TicketSyncAdapter.SyncResult(0, changes == null ? 0 : changes.size());
    }
}
