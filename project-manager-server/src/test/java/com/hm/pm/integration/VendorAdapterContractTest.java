package com.hm.pm.integration;

import com.hm.pm.integration.adapters.vendor.VendorTicketAdapter;
import com.hm.pm.integration.spi.TicketSyncAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VendorAdapterContractTest {

    @Autowired
    private VendorTicketAdapter vendorTicketAdapter;

    @Test
    void vendor_adapter_can_pull_and_push_core_fields() {
        List<TicketSyncAdapter.ExternalTicket> pulled = vendorTicketAdapter.pullUpdatedTickets(Instant.now().minusSeconds(3600));
        assertThat(pulled).isNotNull();

        TicketSyncAdapter.LocalTicketChange change = new TicketSyncAdapter.LocalTicketChange(
                "L-1",
                "test",
                "OPEN",
                "tom",
                "HIGH",
                Instant.now()
        );
        TicketSyncAdapter.SyncResult result = vendorTicketAdapter.pushLocalChanges(List.of(change));
        assertThat(result.pushedCount()).isEqualTo(1);
    }
}
