package com.hm.pm.integration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/integrations")
public class IntegrationController {

    private final SyncJobService syncJobService;

    public IntegrationController(SyncJobService syncJobService) {
        this.syncJobService = syncJobService;
    }

    @PostMapping("/sync/run")
    public SyncJobService.SyncLog runSync(@RequestParam String systemCode) {
        return syncJobService.runSync(systemCode);
    }

    @GetMapping("/sync/logs")
    public List<SyncJobService.SyncLog> logs() {
        return syncJobService.listLogs();
    }
}
