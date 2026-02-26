package com.hm.pm.pm.requirement;

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
@RequestMapping("/api/pm/requirements")
public class RequirementController {

    private final PmStore pmStore;

    public RequirementController(PmStore pmStore) {
        this.pmStore = pmStore;
    }

    @PostMapping
    public ResponseEntity<PmStore.Requirement> create(@RequestBody CreateRequirementRequest request) {
        try {
            PmStore.Requirement requirement = pmStore.createRequirement(request.productId(), request.title());
            return ResponseEntity.status(HttpStatus.CREATED).body(requirement);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}/review")
    public PmStore.Requirement review(@PathVariable Long id) {
        try {
            return pmStore.reviewRequirement(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}/close")
    public PmStore.Requirement close(@PathVariable Long id) {
        try {
            return pmStore.closeRequirement(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public PmStore.Requirement get(@PathVariable Long id) {
        try {
            return pmStore.getRequirement(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    public record CreateRequirementRequest(Long productId, String title) {
    }
}
