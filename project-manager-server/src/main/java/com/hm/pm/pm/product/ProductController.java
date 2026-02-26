package com.hm.pm.pm.product;

import com.hm.pm.pm.PmStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/pm/products")
public class ProductController {

    private final PmStore pmStore;

    public ProductController(PmStore pmStore) {
        this.pmStore = pmStore;
    }

    @PostMapping
    public ResponseEntity<PmStore.Product> create(@RequestBody CreateProductRequest request) {
        try {
            PmStore.Product product = pmStore.createProduct(request.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    public record CreateProductRequest(String name) {
    }
}
