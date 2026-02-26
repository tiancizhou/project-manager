package com.hm.pm.integration;

import com.hm.pm.integration.spi.TicketSyncAdapter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SyncJobService {

    private final Map<String, TicketSyncAdapter> adapterMap = new ConcurrentHashMap<>();
    private final AtomicLong logIdGenerator = new AtomicLong(1000);
    private final List<SyncLog> logs = new ArrayList<>();

    public SyncJobService(List<TicketSyncAdapter> adapters) {
        for (TicketSyncAdapter adapter : adapters) {
            adapterMap.put(adapter.systemCode(), adapter);
        }
    }

    public synchronized SyncLog runSync(String systemCode) {
        TicketSyncAdapter adapter = adapterMap.get(systemCode);
        if (adapter == null) {
            SyncLog failed = new SyncLog(
                    logIdGenerator.incrementAndGet(),
                    systemCode,
                    "FAILED",
                    0,
                    0,
                    Instant.now()
            );
            logs.add(failed);
            return failed;
        }

        List<TicketSyncAdapter.ExternalTicket> pulled = adapter.pullUpdatedTickets(Instant.now().minusSeconds(24 * 3600));
        TicketSyncAdapter.SyncResult result = adapter.pushLocalChanges(List.of());

        SyncLog success = new SyncLog(
                logIdGenerator.incrementAndGet(),
                systemCode,
                "SUCCESS",
                pulled.size(),
                result.pushedCount(),
                Instant.now()
        );
        logs.add(success);
        return success;
    }

    public synchronized List<SyncLog> listLogs() {
        return new ArrayList<>(logs);
    }

    public record SyncLog(Long id,
                          String systemCode,
                          String status,
                          Integer pulledCount,
                          Integer pushedCount,
                          Instant runAt) {
    }
}
