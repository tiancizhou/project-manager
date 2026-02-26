package com.hm.pm.docs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/docs/spaces")
public class DocSpaceController {

    private final DocsStore docsStore;

    public DocSpaceController(DocsStore docsStore) {
        this.docsStore = docsStore;
    }

    @PostMapping
    public ResponseEntity<DocsStore.Space> create(@RequestBody CreateSpaceRequest request) {
        try {
            DocsStore.Space space = docsStore.createSpace(request.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(space);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public record CreateSpaceRequest(String name) {
    }
}
