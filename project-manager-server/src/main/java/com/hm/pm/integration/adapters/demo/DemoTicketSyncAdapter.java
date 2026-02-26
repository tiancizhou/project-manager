package com.hm.pm.integration.adapters.demo;

import com.hm.pm.integration.spi.TicketSyncAdapter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class DemoTicketSyncAdapter implements TicketSyncAdapter {

    @Override
    public String systemCode() {
        return "demo";
    }

    @Override
    public List<ExternalTicket> pullUpdatedTickets(Instant since) {
        return List.of();
    }

    @Override
    public SyncResult pushLocalChanges(List<LocalTicketChange> changes) {
        return new SyncResult(0, changes == null ? 0 : changes.size());
    }
}
