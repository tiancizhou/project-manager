import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import { getToken } from '@/utils/auth';
import {
  bugAsyncPage,
  dashboardAsyncPage,
  requirementAsyncPage,
  taskAsyncPage
} from './async-pages';
import { shouldRedirectToLogin } from './guard';

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { requiresAuth: false, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/BasicLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: dashboardAsyncPage,
        meta: { requiresAuth: true, title: '领导看板' }
      },
      {
        path: 'org',
        name: 'org',
        component: () => import('@/views/org/OrgView.vue'),
        meta: { requiresAuth: true, title: '组织权限' }
      },
      {
        path: 'pm/requirements',
        name: 'pm-requirements',
        component: requirementAsyncPage,
        meta: { requiresAuth: true, title: '需求管理' }
      },
      {
        path: 'pm/tasks',
        name: 'pm-tasks',
        component: taskAsyncPage,
        meta: { requiresAuth: true, title: '任务管理' }
      },
      {
        path: 'pm/bugs',
        name: 'pm-bugs',
        component: bugAsyncPage,
        meta: { requiresAuth: true, title: 'Bug 管理' }
      },
      {
        path: 'docs',
        name: 'docs',
        component: () => import('@/views/docs/DocsView.vue'),
        meta: { requiresAuth: true, title: '知识库' }
      },
      {
        path: 'integrations',
        name: 'integrations',
        component: () => import('@/views/integration/IntegrationView.vue'),
        meta: { requiresAuth: true, title: '对接日志' }
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth !== false;
  const redirect = shouldRedirectToLogin(requiresAuth, getToken());
  if (redirect) {
    next('/login');
    return;
  }
  if (to.path === '/login' && getToken()) {
    next('/dashboard');
    return;
  }
  next();
});

router.afterEach((to) => {
  document.title = String(to.meta.title || 'Project Manager');
});

export default router;
