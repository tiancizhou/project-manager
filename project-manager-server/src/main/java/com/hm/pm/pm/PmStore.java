package com.hm.pm.pm;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PmStore {

    private final AtomicLong productIdGenerator = new AtomicLong(100);
    private final AtomicLong requirementIdGenerator = new AtomicLong(1000);
    private final AtomicLong projectIdGenerator = new AtomicLong(200);
    private final AtomicLong taskIdGenerator = new AtomicLong(3000);
    private final AtomicLong worklogIdGenerator = new AtomicLong(6000);
    private final AtomicLong bugIdGenerator = new AtomicLong(9000);

    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final Map<Long, Requirement> requirements = new ConcurrentHashMap<>();
    private final Map<Long, Project> projects = new ConcurrentHashMap<>();
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final Map<Long, List<Worklog>> worklogsByTask = new ConcurrentHashMap<>();
    private final Map<Long, Bug> bugs = new ConcurrentHashMap<>();

    public Product createProduct(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("product name is required");
        }
        long productId = productIdGenerator.incrementAndGet();
        Product product = new Product(productId, name);
        products.put(productId, product);
        return product;
    }

    public Requirement createRequirement(Long productId, String title) {
        if (productId == null || !products.containsKey(productId)) {
            throw new IllegalArgumentException("productId is invalid");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }

        long requirementId = requirementIdGenerator.incrementAndGet();
        Requirement requirement = new Requirement(requirementId, productId, title, RequirementStatus.NEW);
        requirements.put(requirementId, requirement);
        return requirement;
    }

    public Requirement reviewRequirement(Long id) {
        Requirement requirement = getRequirement(id);
        if (requirement.status() != RequirementStatus.NEW) {
            throw new IllegalStateException("invalid requirement status to review");
        }
        Requirement updated = new Requirement(requirement.id(), requirement.productId(), requirement.title(), RequirementStatus.REVIEWED);
        requirements.put(id, updated);
        return updated;
    }

    public Requirement closeRequirement(Long id) {
        Requirement requirement = getRequirement(id);
        if (requirement.status() != RequirementStatus.REVIEWED
                && requirement.status() != RequirementStatus.IMPLEMENTING) {
            throw new IllegalStateException("invalid requirement status to close");
        }
        Requirement updated = new Requirement(requirement.id(), requirement.productId(), requirement.title(), RequirementStatus.CLOSED);
        requirements.put(id, updated);
        return updated;
    }

    public Requirement getRequirement(Long id) {
        Requirement requirement = requirements.get(id);
        if (requirement == null) {
            throw new IllegalArgumentException("requirement not found");
        }
        return requirement;
    }

    public record Product(Long id, String name) {
    }

    public Project createProject(String name) {
        return createProject(name, null, null, null);
    }

    public Project createProject(String name, String owner, LocalDate startDate, LocalDate endDate) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("project name is required");
        }
        if (owner != null && owner.isBlank()) {
            throw new IllegalArgumentException("owner cannot be blank");
        }
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate cannot be before startDate");
        }

        long projectId = projectIdGenerator.incrementAndGet();
        String resolvedOwner = owner == null ? "unassigned" : owner;
        Project project = new Project(projectId, name, resolvedOwner, startDate, endDate, ProjectStatus.ONGOING);
        projects.put(projectId, project);
        return project;
    }

    public List<Project> listProjects(ProjectStatus status) {
        return projects.values().stream()
                .filter(project -> status == null || project.status() == status)
                .sorted(Comparator.comparing(Project::id))
                .toList();
    }

    public Project archiveProject(Long projectId) {
        Project project = getProject(projectId);
        if (project.status() == ProjectStatus.ARCHIVED) {
            return project;
        }

        Project updated = new Project(project.id(),
                project.name(),
                project.owner(),
                project.startDate(),
                project.endDate(),
                ProjectStatus.ARCHIVED);
        projects.put(projectId, updated);
        return updated;
    }

    public Project restoreProject(Long projectId) {
        Project project = getProject(projectId);
        if (project.status() == ProjectStatus.ONGOING) {
            return project;
        }

        Project updated = new Project(project.id(),
                project.name(),
                project.owner(),
                project.startDate(),
                project.endDate(),
                ProjectStatus.ONGOING);
        projects.put(projectId, updated);
        return updated;
    }

    public Project getProject(Long projectId) {
        Project project = projects.get(projectId);
        if (project == null) {
            throw new IllegalArgumentException("project not found");
        }
        return project;
    }

    public Task createTask(Long projectId, String title) {
        if (projectId == null || !projects.containsKey(projectId)) {
            throw new IllegalArgumentException("projectId is invalid");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("task title is required");
        }

        long taskId = taskIdGenerator.incrementAndGet();
        Task task = new Task(taskId, projectId, title, TaskStatus.TODO, 0D);
        tasks.put(taskId, task);
        return task;
    }

    public Worklog addWorklog(Long taskId, Double hours) {
        if (taskId == null || !tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("taskId is invalid");
        }
        if (hours == null || hours <= 0) {
            throw new IllegalArgumentException("hours must be positive");
        }

        long worklogId = worklogIdGenerator.incrementAndGet();
        Worklog worklog = new Worklog(worklogId, taskId, hours);
        worklogsByTask.computeIfAbsent(taskId, ignored -> new ArrayList<>()).add(worklog);
        return worklog;
    }

    public Task startTask(Long taskId) {
        Task task = getTask(taskId);
        if (task.status() != TaskStatus.TODO) {
            throw new IllegalStateException("task cannot start in current status");
        }

        Task updated = new Task(task.id(), task.projectId(), task.title(), TaskStatus.DOING, task.effectiveHours());
        tasks.put(taskId, updated);
        return updated;
    }

    public Task doneTask(Long taskId) {
        Task task = getTask(taskId);
        if (task.status() == TaskStatus.DONE) {
            return task;
        }
        if (task.status() != TaskStatus.TODO && task.status() != TaskStatus.DOING) {
            throw new IllegalStateException("task cannot be marked done in current status");
        }

        double effectiveHours = totalHours(taskId);
        Task updated = new Task(task.id(), task.projectId(), task.title(), TaskStatus.DONE, effectiveHours);
        tasks.put(taskId, updated);
        return updated;
    }

    public Task getTask(Long taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new IllegalArgumentException("task not found");
        }
        return task;
    }

    private double totalHours(Long taskId) {
        return worklogsByTask.getOrDefault(taskId, List.of())
                .stream()
                .mapToDouble(Worklog::hours)
                .sum();
    }

    public Bug createBug(String title, String severity, Long taskId) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("bug title is required");
        }
        if (severity == null || severity.isBlank()) {
            throw new IllegalArgumentException("severity is required");
        }
        if (taskId != null && !tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("taskId is invalid");
        }

        long bugId = bugIdGenerator.incrementAndGet();
        Bug bug = new Bug(bugId, title, severity, taskId, null, BugStatus.OPEN);
        bugs.put(bugId, bug);
        return bug;
    }

    public Bug assignBug(Long bugId, String assignee) {
        Bug bug = getBug(bugId);
        if (assignee == null || assignee.isBlank()) {
            throw new IllegalArgumentException("assignee is required");
        }
        if (bug.status() != BugStatus.OPEN && bug.status() != BugStatus.REOPENED) {
            throw new IllegalStateException("bug cannot be assigned in current status");
        }

        Bug updated = new Bug(bug.id(), bug.title(), bug.severity(), bug.taskId(), assignee, BugStatus.ASSIGNED);
        bugs.put(bugId, updated);
        return updated;
    }

    public Bug resolveBug(Long bugId) {
        Bug bug = getBug(bugId);
        if (bug.status() != BugStatus.ASSIGNED) {
            throw new IllegalStateException("bug cannot be resolved in current status");
        }
        Bug updated = new Bug(bug.id(), bug.title(), bug.severity(), bug.taskId(), bug.assignee(), BugStatus.RESOLVED);
        bugs.put(bugId, updated);
        return updated;
    }

    public Bug verifyBug(Long bugId) {
        Bug bug = getBug(bugId);
        if (bug.status() != BugStatus.RESOLVED) {
            throw new IllegalStateException("bug cannot be verified in current status");
        }
        Bug updated = new Bug(bug.id(), bug.title(), bug.severity(), bug.taskId(), bug.assignee(), BugStatus.VERIFIED);
        bugs.put(bugId, updated);
        return updated;
    }

    public Bug closeBug(Long bugId) {
        Bug bug = getBug(bugId);
        if (bug.status() != BugStatus.VERIFIED) {
            throw new IllegalStateException("bug cannot be closed in current status");
        }
        Bug updated = new Bug(bug.id(), bug.title(), bug.severity(), bug.taskId(), bug.assignee(), BugStatus.CLOSED);
        bugs.put(bugId, updated);
        return updated;
    }

    public Bug getBug(Long bugId) {
        Bug bug = bugs.get(bugId);
        if (bug == null) {
            throw new IllegalArgumentException("bug not found");
        }
        return bug;
    }

    public MetricsSnapshot calculateSnapshotMetrics() {
        int totalTaskCount = tasks.size();
        int completedTaskCount = (int) tasks.values().stream()
                .filter(task -> task.status() == TaskStatus.DONE)
                .count();
        double effectiveHours = worklogsByTask.values().stream()
                .flatMap(List::stream)
                .mapToDouble(Worklog::hours)
                .sum();

        return new MetricsSnapshot(totalTaskCount, completedTaskCount, effectiveHours);
    }

    public record Requirement(Long id, Long productId, String title, RequirementStatus status) {
    }

    public record Project(Long id,
                          String name,
                          String owner,
                          LocalDate startDate,
                          LocalDate endDate,
                          ProjectStatus status) {
    }

    public record Task(Long id, Long projectId, String title, TaskStatus status, Double effectiveHours) {
    }

    public record Worklog(Long id, Long taskId, Double hours) {
    }

    public record Bug(Long id,
                      String title,
                      String severity,
                      Long taskId,
                      String assignee,
                      BugStatus status) {
    }

    public record MetricsSnapshot(Integer totalTaskCount,
                                  Integer completedTaskCount,
                                  Double effectiveHours) {
    }

    public enum RequirementStatus {
        NEW,
        REVIEWED,
        IMPLEMENTING,
        CLOSED
    }

    public enum ProjectStatus {
        ONGOING,
        ARCHIVED
    }

    public enum TaskStatus {
        TODO,
        DOING,
        DONE
    }

    public enum BugStatus {
        OPEN,
        ASSIGNED,
        RESOLVED,
        VERIFIED,
        CLOSED,
        REOPENED
    }
}
