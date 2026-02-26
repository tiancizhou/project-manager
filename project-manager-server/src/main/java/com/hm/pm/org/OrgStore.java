package com.hm.pm.org;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrgStore {

    private final AtomicLong userIdGenerator = new AtomicLong(1000);
    private final Map<Long, Dept> depts = new ConcurrentHashMap<>();
    private final Map<Long, Role> roles = new ConcurrentHashMap<>();
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    public OrgStore() {
        depts.put(1L, new Dept(1L, "默认部门", null));

        roles.put(1L, new Role(1L, "leader", "领导"));
        roles.put(2L, new Role(2L, "member", "成员"));
        roles.put(3L, new Role(3L, "pm", "项目经理"));
    }

    public List<Dept> listDepts() {
        return new ArrayList<>(depts.values());
    }

    public List<Role> listRoles() {
        return new ArrayList<>(roles.values());
    }

    public List<User> listUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(String name, Long deptId, List<Long> roleIds) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (deptId == null || !depts.containsKey(deptId)) {
            throw new IllegalArgumentException("deptId is invalid");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("roleIds is required");
        }
        for (Long roleId : roleIds) {
            if (!roles.containsKey(roleId)) {
                throw new IllegalArgumentException("roleId is invalid: " + roleId);
            }
        }

        long userId = userIdGenerator.incrementAndGet();
        User user = new User(userId, name, deptId, List.copyOf(roleIds));
        users.put(userId, user);
        return user;
    }

    public record Dept(Long id, String name, Long parentId) {
    }

    public record Role(Long id, String code, String name) {
    }

    public record User(Long id, String name, Long deptId, List<Long> roleIds) {
    }
}
