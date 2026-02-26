package com.hm.pm.pm.project;

import com.hm.pm.pm.PmStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pm/projects")
public class ProjectController {

    private final PmStore pmStore;

    public ProjectController(PmStore pmStore) {
        this.pmStore = pmStore;
    }

    @PostMapping
    public ResponseEntity<PmStore.Project> create(@RequestBody CreateProjectRequest request) {
        try {
            PmStore.Project project = pmStore.createProject(request.name(),
                    request.owner(),
                    request.startDate(),
                    request.endDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<PmStore.Project> list(@RequestParam(required = false) PmStore.ProjectStatus status) {
        return pmStore.listProjects(status);
    }

    @PutMapping("/{id}/archive")
    public PmStore.Project archive(@PathVariable Long id) {
        try {
            return pmStore.archiveProject(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PutMapping("/{id}/restore")
    public PmStore.Project restore(@PathVariable Long id) {
        try {
            return pmStore.restoreProject(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    public record CreateProjectRequest(String name,
                                       String owner,
                                       LocalDate startDate,
                                       LocalDate endDate) {
    }
}
