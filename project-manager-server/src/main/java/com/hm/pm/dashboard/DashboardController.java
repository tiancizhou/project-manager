package com.hm.pm.dashboard;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PostMapping("/snapshots/build")
    public DashboardService.DashboardSnapshot buildSnapshot(@RequestParam LocalDate date) {
        return dashboardService.buildSnapshot(date);
    }

    @GetMapping("/summary")
    public DashboardService.DashboardSnapshot summary(@RequestParam LocalDate date) {
        try {
            return dashboardService.getSnapshot(date);
        } catch (IllegalArgumentException ex) {
            return dashboardService.buildSnapshot(date);
        }
    }
}
