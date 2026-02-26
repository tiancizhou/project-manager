# Project Manager V1 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a ZenTao-like project management system V1 in 2–4 weeks with full module coverage (`A/B/C/G/H/I/J`) and leadership dashboard acceptance.

**Architecture:** Start with a modular monolith backend (`Spring Boot 3 + JDK 17`) and a stable REST contract for Gemini-generated frontend. Implement P0 depth for each module, enforce RBAC + data scope, compute T+1 dashboard snapshots, and integrate one real external ticket system via adapter-based bidirectional sync.

**Tech Stack:** Spring Boot 3, Spring Security, Spring Data JPA, Flyway, MySQL, Redis, JWT, Spring Scheduler, OpenAPI, JUnit 5, Testcontainers (MySQL), Maven.

---

## Execution Rules

- Apply `@superpowers/test-driven-development` in every task.
- Apply `@superpowers/verification-before-completion` before any completion claim.
- Keep commits small and frequent (one task per commit).
- Do not expand scope beyond P0.

---

### Task 1: Bootstrap backend skeleton and health API

**Files:**
- Create: `project-manager-server/pom.xml`
- Create: `project-manager-server/src/main/java/com/hm/pm/ProjectManagerApplication.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/health/HealthController.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/health/HealthControllerTest.java`

**Step 1: Write the failing test**

```java
@SpringBootTest
@AutoConfigureMockMvc
class HealthControllerTest {
  @Autowired MockMvc mvc;

  @Test
  void health_endpoint_returns_ok() throws Exception {
    mvc.perform(get("/api/health"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("UP"));
  }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=HealthControllerTest test`

Expected: FAIL with missing controller or 404.

**Step 3: Write minimal implementation**

```java
@RestController
@RequestMapping("/api")
class HealthController {
  @GetMapping("/health")
  Map<String, String> health() { return Map.of("status", "UP"); }
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=HealthControllerTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server
git commit -m "feat(server): bootstrap spring boot skeleton and health endpoint"
```

---

### Task 2: Add Flyway baseline and RBAC schema

**Files:**
- Create: `project-manager-server/src/main/resources/application.yml`
- Create: `project-manager-server/src/main/resources/db/migration/V1__init_rbac.sql`
- Create: `project-manager-server/src/test/java/com/hm/pm/rbac/RbacSchemaMigrationTest.java`

**Step 1: Write the failing test**

```java
@SpringBootTest
class RbacSchemaMigrationTest {
  @Autowired JdbcTemplate jdbc;

  @Test
  void role_table_exists() {
    Integer count = jdbc.queryForObject(
      "SELECT COUNT(*) FROM information_schema.tables WHERE table_name='role'", Integer.class);
    assertThat(count).isEqualTo(1);
  }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=RbacSchemaMigrationTest test`

Expected: FAIL table not found.

**Step 3: Write minimal implementation**

```sql
CREATE TABLE dept (...);
CREATE TABLE user_account (...);
CREATE TABLE role (...);
CREATE TABLE permission (...);
CREATE TABLE user_role (...);
CREATE TABLE role_permission (...);
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=RbacSchemaMigrationTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/resources project-manager-server/src/test
git commit -m "feat(server): add flyway baseline and rbac schema"
```

---

### Task 3: Implement authentication and JWT issuance

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/auth/AuthController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/auth/JwtService.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/auth/SecurityConfig.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/auth/AuthControllerTest.java`

**Step 1: Write the failing test**

```java
@Test
void login_returns_token() throws Exception {
  mvc.perform(post("/api/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{"username":"admin","password":"admin123"}"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.token").isNotEmpty());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=AuthControllerTest test`

Expected: FAIL endpoint/security not configured.

**Step 3: Write minimal implementation**

```java
@PostMapping("/api/auth/login")
LoginResp login(@RequestBody LoginReq req) {
  // validate against seeded admin user; issue JWT
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=AuthControllerTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/auth project-manager-server/src/test/java/com/hm/pm/auth
git commit -m "feat(auth): add login api and jwt security baseline"
```

---

### Task 4: Build organization and role management APIs

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/org/DeptController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/org/UserController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/org/RoleController.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/org/OrgApiTest.java`

**Step 1: Write the failing test**

```java
@Test
void create_user_with_role_success() throws Exception {
  mvc.perform(post("/api/org/users").header("Authorization", bearerToken)
      .contentType(MediaType.APPLICATION_JSON)
      .content("{"name":"Tom","deptId":1,"roleIds":[2]}"))
    .andExpect(status().isCreated());
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=OrgApiTest test`

Expected: FAIL endpoint missing.

**Step 3: Write minimal implementation**

```java
@PostMapping("/api/org/users")
ResponseEntity<UserDto> createUser(@RequestBody CreateUserCmd cmd) { ... }
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=OrgApiTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/org project-manager-server/src/test/java/com/hm/pm/org
git commit -m "feat(org): add department user role management apis"
```

---

### Task 5: Deliver product and requirement lifecycle

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/pm/product/ProductController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/pm/requirement/RequirementController.java`
- Create: `project-manager-server/src/main/resources/db/migration/V2__pm_requirement.sql`
- Create: `project-manager-server/src/test/java/com/hm/pm/pm/RequirementFlowTest.java`

**Step 1: Write the failing test**

```java
@Test
void requirement_can_flow_from_new_to_closed() throws Exception {
  // create requirement -> review pass -> close
  // assert final status CLOSED
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=RequirementFlowTest test`

Expected: FAIL status transition not implemented.

**Step 3: Write minimal implementation**

```java
enum RequirementStatus { NEW, REVIEWED, IMPLEMENTING, CLOSED }
```

Add transition endpoints with server-side validation.

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=RequirementFlowTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/pm project-manager-server/src/main/resources/db/migration/V2__pm_requirement.sql project-manager-server/src/test/java/com/hm/pm/pm
git commit -m "feat(pm): add product and requirement lifecycle apis"
```

---

### Task 6: Deliver project-task-worklog lifecycle

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/pm/project/ProjectController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/pm/task/TaskController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/pm/worklog/WorklogController.java`
- Create: `project-manager-server/src/main/resources/db/migration/V3__project_task_worklog.sql`
- Create: `project-manager-server/src/test/java/com/hm/pm/pm/TaskWorklogFlowTest.java`

**Step 1: Write the failing test**

```java
@Test
void task_completion_and_worklog_aggregation_success() throws Exception {
  // create project, create task, log work hours, close task
  // assert task status DONE and worklog hours aggregated
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=TaskWorklogFlowTest test`

Expected: FAIL task/worklog endpoints missing.

**Step 3: Write minimal implementation**

```java
enum TaskStatus { TODO, DOING, DONE }
```

Implement task CRUD + state transition + worklog record API.

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=TaskWorklogFlowTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/pm project-manager-server/src/main/resources/db/migration/V3__project_task_worklog.sql project-manager-server/src/test/java/com/hm/pm/pm
git commit -m "feat(pm): add project task worklog lifecycle"
```

---

### Task 7: Implement bug lifecycle and link to task/requirement

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/pm/bug/BugController.java`
- Create: `project-manager-server/src/main/resources/db/migration/V4__bug.sql`
- Create: `project-manager-server/src/test/java/com/hm/pm/pm/BugFlowTest.java`

**Step 1: Write the failing test**

```java
@Test
void bug_can_be_opened_assigned_resolved_closed() throws Exception {
  // create bug -> assign -> resolve -> verify -> close
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=BugFlowTest test`

Expected: FAIL bug APIs absent.

**Step 3: Write minimal implementation**

```java
enum BugStatus { OPEN, ASSIGNED, RESOLVED, VERIFIED, CLOSED, REOPENED }
```

Implement bug state machine and relation fields (`taskId`, `requirementId`).

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=BugFlowTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/pm/bug project-manager-server/src/main/resources/db/migration/V4__bug.sql project-manager-server/src/test/java/com/hm/pm/pm/BugFlowTest.java
git commit -m "feat(pm): add bug lifecycle and associations"
```

---

### Task 8: Implement documentation space/page/revision APIs

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/docs/DocSpaceController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/docs/DocPageController.java`
- Create: `project-manager-server/src/main/resources/db/migration/V5__docs.sql`
- Create: `project-manager-server/src/test/java/com/hm/pm/docs/DocsFlowTest.java`

**Step 1: Write the failing test**

```java
@Test
void page_revision_created_on_update() throws Exception {
  // create page -> update content -> assert revision count == 2
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=DocsFlowTest test`

Expected: FAIL docs APIs/tables missing.

**Step 3: Write minimal implementation**

```java
// On update: insert into doc_revision then update current page content
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=DocsFlowTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/docs project-manager-server/src/main/resources/db/migration/V5__docs.sql project-manager-server/src/test/java/com/hm/pm/docs
git commit -m "feat(docs): add space page revision lifecycle"
```

---

### Task 9: Build dashboard snapshots and drill-down APIs

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/dashboard/DashboardController.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/dashboard/SnapshotScheduler.java`
- Create: `project-manager-server/src/main/resources/db/migration/V6__metric_daily_snapshot.sql`
- Create: `project-manager-server/src/test/java/com/hm/pm/dashboard/DashboardSnapshotTest.java`

**Step 1: Write the failing test**

```java
@Test
void daily_snapshot_aggregates_progress_and_workload() {
  // seed tasks/worklogs -> trigger snapshot -> assert project/department/user metrics present
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=DashboardSnapshotTest test`

Expected: FAIL snapshot service not implemented.

**Step 3: Write minimal implementation**

```java
@Scheduled(cron = "0 10 1 * * *")
void buildDailySnapshot() { ... }
```

Implement metrics: project progress, completed task count, effective worklog hours, risk counters.

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=DashboardSnapshotTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/dashboard project-manager-server/src/main/resources/db/migration/V6__metric_daily_snapshot.sql project-manager-server/src/test/java/com/hm/pm/dashboard
git commit -m "feat(dashboard): add t-plus-1 snapshot and drill-down apis"
```

---

### Task 10: Build integration adapter SPI and sync job skeleton

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/integration/spi/TicketSyncAdapter.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/integration/SyncJobService.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/integration/IntegrationController.java`
- Create: `project-manager-server/src/main/resources/db/migration/V7__integration.sql`
- Create: `project-manager-server/src/test/java/com/hm/pm/integration/IntegrationSyncJobTest.java`

**Step 1: Write the failing test**

```java
@Test
void sync_job_writes_sync_log() {
  // trigger sync job and assert sync_log created with status SUCCESS/FAILED
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=IntegrationSyncJobTest test`

Expected: FAIL integration tables/services missing.

**Step 3: Write minimal implementation**

```java
public interface TicketSyncAdapter {
  List<ExternalTicket> pullUpdatedTickets(Instant since);
  SyncResult pushLocalChanges(List<LocalTicketChange> changes);
}
```

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=IntegrationSyncJobTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/integration project-manager-server/src/main/resources/db/migration/V7__integration.sql project-manager-server/src/test/java/com/hm/pm/integration
git commit -m "feat(integration): add adapter spi and sync job skeleton"
```

---

### Task 11: Integrate one real external ticket system (bidirectional)

**Files:**
- Create: `project-manager-server/src/main/java/com/hm/pm/integration/adapters/<vendor>/VendorTicketAdapter.java`
- Create: `project-manager-server/src/main/java/com/hm/pm/integration/adapters/<vendor>/VendorClient.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/integration/VendorAdapterContractTest.java`
- Modify: `project-manager-server/src/main/resources/application.yml`

**Step 1: Write the failing test**

```java
@Test
void vendor_adapter_can_pull_and_push_core_fields() {
  // mock vendor API and verify title/status/assignee/priority/updateTime sync both directions
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=VendorAdapterContractTest test`

Expected: FAIL vendor adapter not implemented.

**Step 3: Write minimal implementation**

```java
class VendorTicketAdapter implements TicketSyncAdapter { ... }
```

Implement mapping in `integration_mapping` and idempotent upsert by external ticket ID.

**Step 4: Run test to verify it passes**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=VendorAdapterContractTest test`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/integration/adapters project-manager-server/src/main/resources/application.yml project-manager-server/src/test/java/com/hm/pm/integration/VendorAdapterContractTest.java
git commit -m "feat(integration): connect first real ticket system bidirectionally"
```

---

### Task 12: Publish API contract for Gemini frontend and run P0 smoke suite

**Files:**
- Create: `docs/apis/openapi-v1.yaml`
- Create: `docs/apis/frontend-page-contract.md`
- Create: `project-manager-server/src/test/java/com/hm/pm/smoke/P0SmokeTest.java`
- Modify: `docs/plans/2026-02-26-project-manager-design.md`

**Step 1: Write the failing test**

```java
@Test
void p0_modules_smoke_pass() {
  // auth/org/pm/bug/docs/dashboard/integration endpoints all return expected status
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -f project-manager-server/pom.xml -Dtest=P0SmokeTest test`

Expected: FAIL at least one module endpoint missing/inconsistent.

**Step 3: Write minimal implementation**

- Fix endpoint consistency to satisfy smoke flow.
- Export OpenAPI and provide frontend page-contract mapping:
  - 领导看板页
  - 项目任务页
  - 需求页
  - Bug 页
  - 组织权限页
  - 文档页
  - 对接配置页

**Step 4: Run test to verify it passes**

Run:

- `mvn -q -f project-manager-server/pom.xml -Dtest=P0SmokeTest test`
- `mvn -q -f project-manager-server/pom.xml test`

Expected: PASS for smoke and full suite.

**Step 5: Commit**

```bash
git add docs/apis docs/plans/2026-02-26-project-manager-design.md project-manager-server/src/test/java/com/hm/pm/smoke
git commit -m "chore(release): publish api contract and pass p0 smoke"
```

---

## Verification Checklist Before Release

- Backend tests pass: `mvn -q -f project-manager-server/pom.xml test`
- Flyway migration clean on empty DB.
- JWT + RBAC + data scope verified.
- `T+1` snapshot job executes and data can drill down by project/department/user.
- Real external ticket system can pull + push core fields.
- OpenAPI contract is handed to Gemini frontend implementation.

## Handoff Notes

- Frontend (Gemini) should only depend on `docs/apis/openapi-v1.yaml` and `docs/apis/frontend-page-contract.md`.
- Backend remains monolith in V1; split decisions deferred to V2.

