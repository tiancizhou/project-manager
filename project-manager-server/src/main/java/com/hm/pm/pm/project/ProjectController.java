package com.hm.pm.pm.project;

import com.hm.pm.pm.PmStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
            PmStore.Project project = pmStore.createProject(request.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public record CreateProjectRequest(String name) {
    }
}
