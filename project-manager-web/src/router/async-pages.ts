import { defineAsyncComponent, type Component } from 'vue';
import PageLoadError from '@/components/PageLoadError.vue';
import PageLoadingSkeleton from '@/components/PageLoadingSkeleton.vue';

type AsyncViewModule = { default: Component };
type AsyncViewLoader = () => Promise<AsyncViewModule>;
type AsyncLoadAction = 'retry' | 'fail';
type AsyncPageLoadFailure = {
  attempts: number;
  errorMessage: string;
  timestamp: string;
};
type AsyncPageLoadDependencies = {
  reportFailure: (failure: AsyncPageLoadFailure) => void;
  scheduleRetry: (retry: () => void, delayMs: number) => void;
  now: () => string;
};

const MAX_RETRY_ATTEMPTS = 3;
const RETRY_BASE_DELAY_MS = 300;
const RETRY_MAX_DELAY_MS = 5000;

const defaultAsyncPageLoadDependencies: AsyncPageLoadDependencies = {
  reportFailure(failure) {
    if (typeof window !== 'undefined' && typeof window.dispatchEvent === 'function') {
      window.dispatchEvent(new CustomEvent('pm:async-page-load-failed', { detail: failure }));
    }
    console.error('[async-page-load-failed]', failure);
  },
  scheduleRetry(retry, delayMs) {
    globalThis.setTimeout(retry, delayMs);
  },
  now() {
    return new Date().toISOString();
  }
};

export const resolveAsyncLoadAction = (
  attempts: number,
  maxRetryAttempts = MAX_RETRY_ATTEMPTS
): AsyncLoadAction => (attempts < maxRetryAttempts ? 'retry' : 'fail');

export const getRetryDelayMs = (
  attempts: number,
  baseDelayMs = RETRY_BASE_DELAY_MS,
  maxDelayMs = RETRY_MAX_DELAY_MS
): number => {
  const safeAttempts = Math.max(attempts, 1);
  const delayMs = baseDelayMs * 2 ** (safeAttempts - 1);
  return Math.min(delayMs, maxDelayMs);
};

const resolveErrorMessage = (error: unknown): string => {
  if (error instanceof Error) {
    return error.message;
  }
  return String(error ?? 'unknown');
};

export const handleAsyncPageLoadError = (
  error: unknown,
  retry: () => void,
  fail: () => void,
  attempts: number,
  maxRetryAttempts = MAX_RETRY_ATTEMPTS,
  dependencies: AsyncPageLoadDependencies = defaultAsyncPageLoadDependencies
) => {
  const action = resolveAsyncLoadAction(attempts, maxRetryAttempts);
  if (action === 'retry') {
    dependencies.scheduleRetry(retry, getRetryDelayMs(attempts));
    return;
  }

  dependencies.reportFailure({
    attempts,
    errorMessage: resolveErrorMessage(error),
    timestamp: dependencies.now()
  });
  fail();
};

const createAsyncPage = (loader: AsyncViewLoader) =>
  defineAsyncComponent({
    loader,
    delay: 120,
    timeout: 15000,
    loadingComponent: PageLoadingSkeleton,
    errorComponent: PageLoadError,
    onError(error, retry, fail, attempts) {
      handleAsyncPageLoadError(error, retry, fail, attempts);
    }
  });

export const dashboardAsyncPage = createAsyncPage(() => import('@/views/dashboard/DashboardView.vue'));
export const requirementAsyncPage = createAsyncPage(() => import('@/views/pm/RequirementView.vue'));
export const taskAsyncPage = createAsyncPage(() => import('@/views/pm/TaskView.vue'));
export const bugAsyncPage = createAsyncPage(() => import('@/views/pm/BugView.vue'));
