import { describe, expect, it, vi } from 'vitest';
import {
  ASYNC_PAGE_LOAD_FAILED_EVENT,
  buildAsyncPageLoadFailedEvent,
  registerFrontendObservabilityReporter
} from './frontend-events';

describe('frontend observability reporter', () => {
  it('builds frontend event payload for async page load failures', () => {
    const event = buildAsyncPageLoadFailedEvent({
      attempts: 3,
      errorMessage: 'network down',
      timestamp: '2026-02-26T00:00:00.000Z'
    });

    expect(event).toEqual({
      eventType: 'async-page-load-failed',
      level: 'ERROR',
      occurredAt: '2026-02-26T00:00:00.000Z',
      payload: {
        attempts: 3,
        errorMessage: 'network down'
      }
    });
  });

  it('reports when async-page-load-failed browser event is dispatched', () => {
    const reporter = vi.fn();
    const dispose = registerFrontendObservabilityReporter(reporter);

    window.dispatchEvent(
      new CustomEvent(ASYNC_PAGE_LOAD_FAILED_EVENT, {
        detail: {
          attempts: 2,
          errorMessage: 'temporary error',
          timestamp: '2026-02-26T00:00:00.000Z'
        }
      })
    );

    expect(reporter).toHaveBeenCalledTimes(1);
    expect(reporter).toHaveBeenCalledWith(
      expect.objectContaining({
        eventType: 'async-page-load-failed',
        level: 'ERROR',
        occurredAt: '2026-02-26T00:00:00.000Z'
      })
    );

    dispose();
    window.dispatchEvent(
      new CustomEvent(ASYNC_PAGE_LOAD_FAILED_EVENT, {
        detail: {
          attempts: 9,
          errorMessage: 'should not report',
          timestamp: '2026-02-26T00:00:00.000Z'
        }
      })
    );
    expect(reporter).toHaveBeenCalledTimes(1);
  });
});
