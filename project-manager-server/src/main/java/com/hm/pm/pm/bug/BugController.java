package com.hm.pm.pm.bug;

import com.hm.pm.pm.PmStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pm/bugs")
public class BugController {

    private final PmStore pmStore;

    public BugController(PmStore pmStore) {
        this.pmStore = pmStore;
    }

    @PostMapping
    public ResponseEntity<PmStore.Bug> create(@RequestBody CreateBugRequest request) {
        try {
            PmStore.Bug bug = pmStore.createBug(request.title(), request.severity(), request.taskId());
            return ResponseEntity.status(HttpStatus.CREATED).body(bug);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}/assign")
    public PmStore.Bug assign(@PathVariable Long id, @RequestBody AssignBugRequest request) {
        try {
            return pmStore.assignBug(id, request.assignee());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}/resolve")
    public PmStore.Bug resolve(@PathVariable Long id) {
        try {
            return pmStore.resolveBug(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}/verify")
    public PmStore.Bug verify(@PathVariable Long id) {
        try {
            return pmStore.verifyBug(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}/close")
    public PmStore.Bug close(@PathVariable Long id) {
        try {
            return pmStore.closeBug(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public PmStore.Bug get(@PathVariable Long id) {
        try {
            return pmStore.getBug(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    public record CreateBugRequest(String title, String severity, Long taskId) {
    }

    public record AssignBugRequest(String assignee) {
    }
}
