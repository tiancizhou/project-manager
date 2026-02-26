package com.hm.pm.org;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/org/users")
public class UserController {

    private final OrgStore orgStore;

    public UserController(OrgStore orgStore) {
        this.orgStore = orgStore;
    }

    @PostMapping
    public ResponseEntity<OrgStore.User> create(@RequestBody CreateUserRequest request) {
        try {
            OrgStore.User user = orgStore.createUser(request.name(), request.deptId(), request.roleIds());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @GetMapping
    public List<OrgStore.User> list() {
        return orgStore.listUsers();
    }

    public record CreateUserRequest(String name, Long deptId, List<Long> roleIds) {
    }
}
