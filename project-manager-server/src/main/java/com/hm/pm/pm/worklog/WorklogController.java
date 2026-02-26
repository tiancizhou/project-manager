package com.hm.pm.pm.worklog;

import com.hm.pm.pm.PmStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pm/worklogs")
public class WorklogController {

    private final PmStore pmStore;

    public WorklogController(PmStore pmStore) {
        this.pmStore = pmStore;
    }

    @PostMapping
    public ResponseEntity<PmStore.Worklog> create(@RequestBody CreateWorklogRequest request) {
        try {
            PmStore.Worklog worklog = pmStore.addWorklog(request.taskId(), request.hours());
            return ResponseEntity.status(HttpStatus.CREATED).body(worklog);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public record CreateWorklogRequest(Long taskId, Double hours) {
    }
}
