package com.hm.pm.integration.adapters.vendor;

import com.hm.pm.integration.spi.TicketSyncAdapter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class VendorTicketAdapter implements TicketSyncAdapter {

    private final VendorClient vendorClient;

    public VendorTicketAdapter(VendorClient vendorClient) {
        this.vendorClient = vendorClient;
    }

    @Override
    public String systemCode() {
        return "vendor";
    }

    @Override
    public List<ExternalTicket> pullUpdatedTickets(Instant since) {
        return vendorClient.pullUpdatedTickets(since);
    }

    @Override
    public SyncResult pushLocalChanges(List<LocalTicketChange> changes) {
        return vendorClient.pushChanges(changes);
    }
}
