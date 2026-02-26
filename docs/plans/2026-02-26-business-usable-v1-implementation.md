# Business Usable V1 (A-tier) Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Make the system truly usable for daily work by completing login + project/requirement/task/bug core workflows end-to-end.

**Architecture:** Keep the existing Spring Boot modular-monolith + Vue SPA structure, but refactor by workflow boundaries instead of page-by-page patching. Backend provides complete CRUD/list/flow endpoints and strict state transitions; frontend adopts a unified PM page skeleton (filters + table + detail editor + flow actions). Preserve compatibility with current APIs where possible and extend incrementally.

**Tech Stack:** Spring Boot 3, JDK 17, Flyway, MockMvc/JUnit 5, Vue 3, TypeScript, Vite, Pinia, Vue Router, Element Plus, Vitest.

---

### Task 1: Backend project module reaches usable flow

**Files:**
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java`
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/project/ProjectController.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/pm/ProjectBusinessUsableApiTest.java`

**Step 1: Write the failing test**

Add test cases for:
- create project with owner/date/status fields
- list projects by status
- archive and restore project state

```java
@Test
void project_can_be_listed_archived_and_restored() throws Exception {
  // create -> list -> archive -> list(archived) -> restore -> list(active)
}
```

**Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=ProjectBusinessUsableApiTest test`
Expected: FAIL (missing list/archive/restore endpoints and fields).

**Step 3: Write minimal implementation**

- Extend `PmStore.Project` fields: `owner/startDate/endDate/status/updatedAt`
- Add store methods: `listProjects(...)`, `updateProject(...)`, `archiveProject(...)`, `restoreProject(...)`
- Expose controller endpoints:
  - `GET /api/pm/projects`
  - `PUT /api/pm/projects/{id}`
  - `PUT /api/pm/projects/{id}/archive`
  - `PUT /api/pm/projects/{id}/restore`

**Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=ProjectBusinessUsableApiTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java project-manager-server/src/main/java/com/hm/pm/pm/project/ProjectController.java project-manager-server/src/test/java/com/hm/pm/pm/ProjectBusinessUsableApiTest.java
git commit -m "feat(pm): make project workflow business-usable"
```

### Task 2: Backend requirement module reaches usable flow

**Files:**
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java`
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/requirement/RequirementController.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/pm/RequirementBusinessUsableApiTest.java`

**Step 1: Write the failing test**

Cover:
- create requirement with `projectId/priority/owner/description/acceptanceCriteria`
- list requirements with status filter
- `review`, `reject`, `close` transitions
- invalid transitions return 400

**Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=RequirementBusinessUsableApiTest test`
Expected: FAIL (missing fields/list/reject endpoint).

**Step 3: Write minimal implementation**

- Extend `PmStore.Requirement` data model and transition methods.
- Add controller endpoints:
  - `GET /api/pm/requirements`
  - `PUT /api/pm/requirements/{id}`
  - `PUT /api/pm/requirements/{id}/reject`
- Keep existing `create/review/close/get` compatible.

**Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=RequirementBusinessUsableApiTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java project-manager-server/src/main/java/com/hm/pm/pm/requirement/RequirementController.java project-manager-server/src/test/java/com/hm/pm/pm/RequirementBusinessUsableApiTest.java
git commit -m "feat(pm): add requirement business fields and reject flow"
```

### Task 3: Backend task module reaches usable flow

**Files:**
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java`
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/task/TaskController.java`
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/worklog/WorklogController.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/pm/TaskBusinessUsableApiTest.java`

**Step 1: Write the failing test**

Cover:
- create task with `requirementId/assignee/estimatedHours`
- list by status/project/requirement
- update basic fields
- `start`, `add worklog`, `done` updates spent hours

**Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=TaskBusinessUsableApiTest test`
Expected: FAIL (missing list/update/extended fields).

**Step 3: Write minimal implementation**

- Extend `PmStore.Task` to include business fields and `spentHours` aggregation.
- Add controller endpoints:
  - `GET /api/pm/tasks`
  - `PUT /api/pm/tasks/{id}`
  - keep `start/done/get`
- Keep `/api/pm/worklogs` and add alias `POST /api/pm/tasks/{id}/worklogs`.

**Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=TaskBusinessUsableApiTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java project-manager-server/src/main/java/com/hm/pm/pm/task/TaskController.java project-manager-server/src/main/java/com/hm/pm/pm/worklog/WorklogController.java project-manager-server/src/test/java/com/hm/pm/pm/TaskBusinessUsableApiTest.java
git commit -m "feat(pm): complete task execution workflow endpoints"
```

### Task 4: Backend bug module reaches usable flow

**Files:**
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java`
- Modify: `project-manager-server/src/main/java/com/hm/pm/pm/bug/BugController.java`
- Create: `project-manager-server/src/test/java/com/hm/pm/pm/BugBusinessUsableApiTest.java`

**Step 1: Write the failing test**

Cover:
- create bug with `projectId/taskId/stepsToReproduce`
- list bugs by status/project
- update title/severity/steps
- flow `assign -> resolve -> verify -> close`

**Step 2: Run test to verify it fails**

Run: `mvn -q -Dtest=BugBusinessUsableApiTest test`
Expected: FAIL (missing list/update and extended fields).

**Step 3: Write minimal implementation**

- Extend `PmStore.Bug` fields and update method.
- Add controller endpoints:
  - `GET /api/pm/bugs`
  - `PUT /api/pm/bugs/{id}`
- Keep existing flow endpoints unchanged.

**Step 4: Run test to verify it passes**

Run: `mvn -q -Dtest=BugBusinessUsableApiTest test`
Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-server/src/main/java/com/hm/pm/pm/PmStore.java project-manager-server/src/main/java/com/hm/pm/pm/bug/BugController.java project-manager-server/src/test/java/com/hm/pm/pm/BugBusinessUsableApiTest.java
git commit -m "feat(pm): complete bug lifecycle and list update APIs"
```

### Task 5: Backend contract and regression verification

**Files:**
- Modify: `docs/apis/openapi-v1.yaml`
- Modify: `docs/apis/frontend-page-contract.md`

**Step 1: Write the failing check**

Add/adjust a focused test that asserts at least one new API route is reachable, e.g. `GET /api/pm/projects` in existing smoke flow.

**Step 2: Run check to verify it fails**

Run: `mvn -q -Dtest=P0SmokeTest test`
Expected: FAIL due contract mismatch before docs/API alignment.

**Step 3: Write minimal implementation**

- Sync new request/response schemas and routes into docs.

**Step 4: Run verification to pass**

Run:
- `mvn -q -Dtest=P0SmokeTest test`
- `mvn -q test`

Expected: PASS.

**Step 5: Commit**

```bash
git add docs/apis/openapi-v1.yaml docs/apis/frontend-page-contract.md
git commit -m "docs(api): align business-usable pm contracts"
```

### Task 6: Frontend PM API module expansion

**Files:**
- Modify: `project-manager-web/src/api/pm.ts`
- Create: `project-manager-web/src/api/pm.spec.ts`

**Step 1: Write the failing test**

Use `vi.mock('@/api/http')` to assert call shapes for:
- `listProjects/listRequirements/listTasks/listBugs`
- `update*` and new flow helpers

```ts
it('calls list tasks endpoint with filters', async () => {
  await listTasks({ status: 'DOING', projectId: 1 });
  expect(http.get).toHaveBeenCalledWith('/api/pm/tasks', { params: { status: 'DOING', projectId: 1 } });
});
```

**Step 2: Run test to verify it fails**

Run: `npm run test -- pm.spec.ts`
Expected: FAIL (functions not implemented).

**Step 3: Write minimal implementation**

- Add new TypeScript interfaces for extended fields.
- Add list/update/action API wrappers matching backend routes.

**Step 4: Run test to verify it passes**

Run: `npm run test -- pm.spec.ts`
Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-web/src/api/pm.ts project-manager-web/src/api/pm.spec.ts
git commit -m "feat(web): expand pm api module for business-usable flows"
```

### Task 7: Frontend route/menu includes dedicated project management page

**Files:**
- Create: `project-manager-web/src/views/pm/ProjectView.vue`
- Modify: `project-manager-web/src/router/index.ts`
- Modify: `project-manager-web/src/router/async-pages.ts`
- Modify: `project-manager-web/src/layout/BasicLayout.vue`
- Modify: `project-manager-web/src/router/lazy-load.spec.ts`

**Step 1: Write the failing test**

Add assertions that:
- route name `pm-projects` exists
- menu route path `/pm/projects` is reachable
- lazy-load strategy matches chosen pattern

**Step 2: Run test to verify it fails**

Run: `npm run test -- lazy-load.spec.ts`
Expected: FAIL because `pm-projects` route absent.

**Step 3: Write minimal implementation**

- Add `ProjectView` with list/create/edit/archive/restore skeleton.
- Register route + menu entry.

**Step 4: Run test to verify it passes**

Run: `npm run test -- lazy-load.spec.ts`
Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-web/src/views/pm/ProjectView.vue project-manager-web/src/router/index.ts project-manager-web/src/router/async-pages.ts project-manager-web/src/layout/BasicLayout.vue project-manager-web/src/router/lazy-load.spec.ts
git commit -m "feat(web): add project management page and route"
```

### Task 8: Frontend requirement page refactor to usable mode

**Files:**
- Modify: `project-manager-web/src/views/pm/RequirementView.vue`
- Create: `project-manager-web/src/views/pm/requirement-state.ts`
- Create: `project-manager-web/src/views/pm/requirement-state.spec.ts`

**Step 1: Write the failing test**

Test pure helpers:
- allowed actions by requirement status
- request payload mapper for create/update

**Step 2: Run test to verify it fails**

Run: `npm run test -- requirement-state.spec.ts`
Expected: FAIL.

**Step 3: Write minimal implementation**

- Implement status action helpers.
- Refactor page to:
  - filter + list + detail panel
  - review/reject/close actions
  - create/edit form with key fields

**Step 4: Run test to verify it passes**

Run: `npm run test -- requirement-state.spec.ts`
Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-web/src/views/pm/RequirementView.vue project-manager-web/src/views/pm/requirement-state.ts project-manager-web/src/views/pm/requirement-state.spec.ts
git commit -m "feat(web): refactor requirement page for business-usable flow"
```

### Task 9: Frontend task and bug pages refactor to usable mode

**Files:**
- Modify: `project-manager-web/src/views/pm/TaskView.vue`
- Modify: `project-manager-web/src/views/pm/BugView.vue`
- Create: `project-manager-web/src/views/pm/task-state.ts`
- Create: `project-manager-web/src/views/pm/task-state.spec.ts`
- Create: `project-manager-web/src/views/pm/bug-state.ts`
- Create: `project-manager-web/src/views/pm/bug-state.spec.ts`

**Step 1: Write the failing test**

Add tests for:
- task action matrix (`TODO/DOING/DONE`)
- bug action matrix (`OPEN/ASSIGNED/RESOLVED/VERIFIED/CLOSED`)

**Step 2: Run test to verify it fails**

Run:
- `npm run test -- task-state.spec.ts`
- `npm run test -- bug-state.spec.ts`

Expected: FAIL.

**Step 3: Write minimal implementation**

- Implement helper logic and integrate into pages.
- Refactor pages to list/filter/edit + flow operations + error feedback.

**Step 4: Run test to verify it passes**

Run:
- `npm run test -- task-state.spec.ts bug-state.spec.ts`

Expected: PASS.

**Step 5: Commit**

```bash
git add project-manager-web/src/views/pm/TaskView.vue project-manager-web/src/views/pm/BugView.vue project-manager-web/src/views/pm/task-state.ts project-manager-web/src/views/pm/task-state.spec.ts project-manager-web/src/views/pm/bug-state.ts project-manager-web/src/views/pm/bug-state.spec.ts
git commit -m "feat(web): refactor task and bug pages for business-usable workflows"
```

### Task 10: End-to-end verification and handoff docs

**Files:**
- Modify: `project-manager-web/README.md`
- Modify: `docs/apis/frontend-page-contract.md`

**Step 1: Write the failing check**

Add/adjust one integration-level frontend smoke test ensuring PM routes and required actions are present.

**Step 2: Run check to verify it fails**

Run: `npm run test`
Expected: FAIL before final wiring.

**Step 3: Write minimal implementation**

- Finalize route/menu docs and usage instructions.

**Step 4: Run full verification**

Run:
- `npm run test`
- `npm run type-check`
- `npm run build`
- `mvn -q test`

Expected: all PASS.

**Step 5: Commit**

```bash
git add project-manager-web/README.md docs/apis/frontend-page-contract.md
git commit -m "chore: finalize business-usable v1 handoff docs"
```
