# Project Manager Web V1 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build `project-manager-web` frontend V1 aligned with backend contracts and leadership dashboard workflow.

**Architecture:** Use a modular Vue 3 + TypeScript SPA with route-guarded pages, centralized Axios client, and domain API modules (`auth/org/pm/docs/dashboard/integration`). Keep P0 scope minimal while ensuring end-to-end operability for login, dashboard, PM flows, docs, and sync logs.

**Tech Stack:** Vue 3, TypeScript, Vite, Vue Router, Pinia, Element Plus, Axios, Vitest.

---

### Task 1: Bootstrap project and tooling

**Files:**
- Create: `project-manager-web/package.json`
- Create: `project-manager-web/vite.config.ts`
- Create: `project-manager-web/tsconfig.json`
- Create: `project-manager-web/src/main.ts`
- Create: `project-manager-web/src/App.vue`

**Step 1: Write the failing test**

- Create `project-manager-web/src/utils/auth.spec.ts` importing missing auth helper.

**Step 2: Run test to verify it fails**

- Run: `npm run test -- auth.spec.ts`
- Expected: FAIL because helper does not exist.

**Step 3: Write minimal implementation**

- Add project baseline and auth helper module.

**Step 4: Run test to verify it passes**

- Run: `npm run test -- auth.spec.ts`
- Expected: PASS.

**Step 5: Commit**

- `git add project-manager-web`
- `git commit -m "feat(web): bootstrap vite vue ts project"`

### Task 2: Implement auth and request pipeline

**Files:**
- Create: `project-manager-web/src/api/http.ts`
- Create: `project-manager-web/src/api/auth.ts`
- Create: `project-manager-web/src/store/auth.ts`
- Create: `project-manager-web/src/views/auth/LoginView.vue`

**Step 1: Write the failing test**

- Add store/auth utility test for token persistence and logout.

**Step 2: Run test to verify it fails**

- Run: `npm run test -- auth-store.spec.ts`

**Step 3: Write minimal implementation**

- Implement login API and auth store actions.

**Step 4: Run test to verify it passes**

- Run: `npm run test -- auth-store.spec.ts`

**Step 5: Commit**

- `git add project-manager-web/src/api project-manager-web/src/store project-manager-web/src/views/auth`
- `git commit -m "feat(web): add auth flow and axios interceptors"`

### Task 3: Build layout and route guards

**Files:**
- Create: `project-manager-web/src/router/index.ts`
- Create: `project-manager-web/src/layout/BasicLayout.vue`
- Create: `project-manager-web/src/views/*` placeholders

**Step 1: Write the failing test**

- Add router guard unit test for redirect behavior.

**Step 2: Run test to verify it fails**

- Run: `npm run test -- router-guard.spec.ts`

**Step 3: Write minimal implementation**

- Implement route table and guard based on token.

**Step 4: Run test to verify it passes**

- Run: `npm run test -- router-guard.spec.ts`

**Step 5: Commit**

- `git add project-manager-web/src/router project-manager-web/src/layout project-manager-web/src/views`
- `git commit -m "feat(web): add route guards and base layout"`

### Task 4: Implement domain API modules and pages

**Files:**
- Create: `project-manager-web/src/api/dashboard.ts`
- Create: `project-manager-web/src/api/org.ts`
- Create: `project-manager-web/src/api/pm.ts`
- Create: `project-manager-web/src/api/docs.ts`
- Create: `project-manager-web/src/api/integration.ts`
- Create: domain views under `project-manager-web/src/views`

**Step 1: Write the failing test**

- Add API contract smoke test for endpoint function existence and invocation shape.

**Step 2: Run test to verify it fails**

- Run: `npm run test -- api-contract.spec.ts`

**Step 3: Write minimal implementation**

- Implement P0 pages and API calls.

**Step 4: Run test to verify it passes**

- Run: `npm run test -- api-contract.spec.ts`

**Step 5: Commit**

- `git add project-manager-web/src/api project-manager-web/src/views`
- `git commit -m "feat(web): add dashboard org pm docs integration pages"`

### Task 5: Verification and frontend handoff docs

**Files:**
- Modify: `project-manager-web/README.md`

**Step 1: Write the failing test**

- Add/adjust smoke test for route availability.

**Step 2: Run test to verify it fails**

- Run: `npm run test`

**Step 3: Write minimal implementation**

- Fix failing points and complete docs.

**Step 4: Run test to verify it passes**

- Run: `npm run test`
- Run: `npm run type-check`
- Run: `npm run build`

**Step 5: Commit**

- `git add project-manager-web`
- `git commit -m "chore(web): finalize docs and pass frontend verification"`

