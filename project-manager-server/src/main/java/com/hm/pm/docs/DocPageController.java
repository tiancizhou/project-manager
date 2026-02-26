package com.hm.pm.docs;

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

import java.util.List;

@RestController
@RequestMapping("/api/docs/pages")
public class DocPageController {

    private final DocsStore docsStore;

    public DocPageController(DocsStore docsStore) {
        this.docsStore = docsStore;
    }

    @PostMapping
    public ResponseEntity<DocsStore.Page> create(@RequestBody CreatePageRequest request) {
        try {
            DocsStore.Page page = docsStore.createPage(request.spaceId(), request.title(), request.content());
            return ResponseEntity.status(HttpStatus.CREATED).body(page);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public DocsStore.Page update(@PathVariable Long id, @RequestBody UpdatePageRequest request) {
        try {
            return docsStore.updatePage(id, request.content());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping("/{id}/revisions")
    public List<DocsStore.Revision> revisions(@PathVariable Long id) {
        try {
            return docsStore.listRevisions(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    public record CreatePageRequest(Long spaceId, String title, String content) {
    }

    public record UpdatePageRequest(String content) {
    }
}
