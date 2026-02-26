package com.hm.pm.org;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/org/depts")
public class DeptController {

    private final OrgStore orgStore;

    public DeptController(OrgStore orgStore) {
        this.orgStore = orgStore;
    }

    @GetMapping
    public List<OrgStore.Dept> list() {
        return orgStore.listDepts();
    }
}
