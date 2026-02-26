# Frontend Page Contract (Gemini)

## 1. 登录页

- API: `POST /api/auth/login`
- 输入：`username`、`password`
- 输出：`token`、`tokenType`

## 2. 领导看板页

- API: `GET /api/dashboard/summary?date=YYYY-MM-DD`
- 可选触发：`POST /api/dashboard/snapshots/build?date=YYYY-MM-DD`
- 展示字段：`totalTaskCount`、`completedTaskCount`、`effectiveHours`

## 3. 组织与权限页

- API: `GET /api/org/depts`
- API: `GET /api/org/roles`
- API: `POST /api/org/users`

## 4. 产品与需求页

- API: `POST /api/pm/products`
- API: `POST /api/pm/requirements`
- API: `PUT /api/pm/requirements/{id}/review`
- API: `PUT /api/pm/requirements/{id}/close`

## 5. 项目任务页

- API: `POST /api/pm/projects`
- API: `POST /api/pm/tasks`
- API: `PUT /api/pm/tasks/{id}/start`
- API: `PUT /api/pm/tasks/{id}/done`
- API: `POST /api/pm/worklogs`

## 6. Bug 页

- API: `POST /api/pm/bugs`
- API: `PUT /api/pm/bugs/{id}/assign`
- API: `PUT /api/pm/bugs/{id}/resolve`
- API: `PUT /api/pm/bugs/{id}/verify`
- API: `PUT /api/pm/bugs/{id}/close`

## 7. 知识库页

- API: `POST /api/docs/spaces`
- API: `POST /api/docs/pages`
- API: `PUT /api/docs/pages/{id}`
- API: `GET /api/docs/pages/{id}/revisions`

## 8. 对接配置与日志页

- API: `POST /api/integrations/sync/run?systemCode=demo|vendor`
- API: `GET /api/integrations/sync/logs`

## 9. 前端异常上报约定

- 触发场景：路由异步页面加载失败（当前事件名：`pm:async-page-load-failed`）
- 上报 API：`POST /api/v1/observability/frontend-events`
- 请求体字段：`eventType`、`level`、`occurredAt`、`payload`
- 固定值约定：`eventType=async-page-load-failed`，`level=ERROR`
- `payload` 推荐字段：`attempts`（重试次数）、`errorMessage`（错误信息）

示例：

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

## 10. 统一约定

- 鉴权：除 `/api/health`、`/api/auth/**` 外，均需 `Authorization: Bearer <token>`
- 日期字段：`YYYY-MM-DD`
- 错误态：统一使用 HTTP 状态码 + JSON 错误消息（由后端异常映射）
