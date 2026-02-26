import { reportFrontendEvent, type FrontendObservabilityEvent } from '@/api/observability';

export const ASYNC_PAGE_LOAD_FAILED_EVENT = 'pm:async-page-load-failed';

export type AsyncPageLoadFailedDetail = {
  attempts: number;
  errorMessage: string;
  timestamp: string;
};

type FrontendEventReporter = (event: FrontendObservabilityEvent) => Promise<void> | void;

const isAsyncPageLoadFailedDetail = (detail: unknown): detail is AsyncPageLoadFailedDetail => {
  if (!detail || typeof detail !== 'object') {
    return false;
  }

  const candidate = detail as Partial<AsyncPageLoadFailedDetail>;
  return (
    typeof candidate.attempts === 'number' &&
    typeof candidate.errorMessage === 'string' &&
    typeof candidate.timestamp === 'string'
  );
};

export const buildAsyncPageLoadFailedEvent = (
  detail: AsyncPageLoadFailedDetail
): FrontendObservabilityEvent => ({
  eventType: 'async-page-load-failed',
  level: 'ERROR',
  occurredAt: detail.timestamp,
  payload: {
    attempts: detail.attempts,
    errorMessage: detail.errorMessage
  }
});

export const registerFrontendObservabilityReporter = (
  reporter: FrontendEventReporter = reportFrontendEvent
): (() => void) => {
  if (typeof window === 'undefined' || typeof window.addEventListener !== 'function') {
    return () => undefined;
  }

  const handler = (event: Event) => {
    const customEvent = event as CustomEvent<unknown>;
    if (!isAsyncPageLoadFailedDetail(customEvent.detail)) {
      return;
    }

    const payload = buildAsyncPageLoadFailedEvent(customEvent.detail);
    void Promise.resolve(reporter(payload)).catch((error) => {
      console.error('[frontend-observability-report-failed]', error);
    });
  };

  window.addEventListener(ASYNC_PAGE_LOAD_FAILED_EVENT, handler as EventListener);
  return () => {
    window.removeEventListener(ASYNC_PAGE_LOAD_FAILED_EVENT, handler as EventListener);
  };
};
