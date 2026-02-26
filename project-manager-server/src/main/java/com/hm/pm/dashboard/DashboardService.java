package com.hm.pm.dashboard;

import com.hm.pm.pm.PmStore;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DashboardService {

    private final PmStore pmStore;
    private final Map<LocalDate, DashboardSnapshot> snapshots = new ConcurrentHashMap<>();

    public DashboardService(PmStore pmStore) {
        this.pmStore = pmStore;
    }

    public DashboardSnapshot buildSnapshot(LocalDate date) {
        PmStore.MetricsSnapshot metrics = pmStore.calculateSnapshotMetrics();
        DashboardSnapshot snapshot = new DashboardSnapshot(
                date,
                metrics.totalTaskCount(),
                metrics.completedTaskCount(),
                metrics.effectiveHours()
        );
        snapshots.put(date, snapshot);
        return snapshot;
    }

    public DashboardSnapshot getSnapshot(LocalDate date) {
        DashboardSnapshot snapshot = snapshots.get(date);
        if (snapshot == null) {
            throw new IllegalArgumentException("snapshot not found");
        }
        return snapshot;
    }

    public record DashboardSnapshot(LocalDate date,
                                    Integer totalTaskCount,
                                    Integer completedTaskCount,
                                    Double effectiveHours) {
    }
}
