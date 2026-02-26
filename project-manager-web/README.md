# project-manager-web

前端 V1，基于 `Vue 3 + TypeScript + Vite + Pinia + Vue Router + Element Plus`。

## 启动

```bash
npm install
npm run dev
```

## 校验

```bash
npm run test
npm run type-check
npm run build
```

## 环境变量

- `VITE_API_BASE_URL`：后端地址，默认 `http://localhost:8080`

## 页面与接口映射

- 登录页：`POST /api/auth/login`
- 领导看板：`GET /api/dashboard/summary`、`POST /api/dashboard/snapshots/build`
- 组织权限：`GET /api/org/depts`、`GET /api/org/roles`、`GET/POST /api/org/users`
- 需求管理：`POST /api/pm/products`、`POST /api/pm/requirements`、`PUT /api/pm/requirements/{id}/review|close`
- 任务管理：`POST /api/pm/projects`、`POST /api/pm/tasks`、`POST /api/pm/worklogs`、`PUT /api/pm/tasks/{id}/start|done`
- Bug 管理：`POST /api/pm/bugs`、`PUT /api/pm/bugs/{id}/assign|resolve|verify|close`
- 知识库：`POST /api/docs/spaces`、`POST /api/docs/pages`、`PUT /api/docs/pages/{id}`、`GET /api/docs/pages/{id}/revisions`
- 对接日志：`POST /api/integrations/sync/run`、`GET /api/integrations/sync/logs`

## 前端异常上报链路

当前已实现“异步页面加载失败”的自动上报，链路如下：

1. 路由异步组件加载失败后，执行指数退避重试。
2. 超过最大重试次数后，派发浏览器事件：`pm:async-page-load-failed`。
3. 启动阶段注册的监听器捕获该事件并转换为标准埋点报文。
4. 前端调用后端接口：`POST /api/v1/observability/frontend-events`。

默认上报报文示例：

```json
{
  "eventType": "async-page-load-failed",
  "level": "ERROR",
  "occurredAt": "2026-02-26T00:00:00Z",
  "payload": {
    "attempts": 3,
    "errorMessage": "network down"
  }
}
```
