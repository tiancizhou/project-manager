package com.hm.pm.org;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/org/roles")
public class RoleController {

    private final OrgStore orgStore;

    public RoleController(OrgStore orgStore) {
        this.orgStore = orgStore;
    }

    @GetMapping
    public List<OrgStore.Role> list() {
        return orgStore.listRoles();
    }
}
