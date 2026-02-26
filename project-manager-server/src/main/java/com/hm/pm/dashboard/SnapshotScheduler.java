package com.hm.pm.dashboard;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SnapshotScheduler {

    private final DashboardService dashboardService;

    public SnapshotScheduler(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Scheduled(cron = "0 10 1 * * *")
    public void buildDailySnapshot() {
        dashboardService.buildSnapshot(LocalDate.now().minusDays(1));
    }
}
