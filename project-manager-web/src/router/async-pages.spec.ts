import { describe, expect, it, vi } from 'vitest';
import { getRetryDelayMs, handleAsyncPageLoadError, resolveAsyncLoadAction } from './async-pages';

describe('async page load retry policy', () => {
  it('retries when attempts are below max', () => {
    expect(resolveAsyncLoadAction(1, 3)).toBe('retry');
    expect(resolveAsyncLoadAction(2, 3)).toBe('retry');
  });

  it('fails when attempts reach max', () => {
    expect(resolveAsyncLoadAction(3, 3)).toBe('fail');
    expect(resolveAsyncLoadAction(4, 3)).toBe('fail');
  });

  it('uses exponential retry delay with max cap', () => {
    expect(getRetryDelayMs(1, 200, 2000)).toBe(200);
    expect(getRetryDelayMs(2, 200, 2000)).toBe(400);
    expect(getRetryDelayMs(3, 200, 2000)).toBe(800);
    expect(getRetryDelayMs(5, 200, 2000)).toBe(2000);
  });

  it('schedules retry with calculated delay before max attempts', () => {
    const retry = vi.fn();
    const fail = vi.fn();
    const reportFailure = vi.fn();
    const scheduleRetry = vi.fn();

    handleAsyncPageLoadError(new Error('temporary'), retry, fail, 2, 3, {
      reportFailure,
      scheduleRetry,
      now: () => '2026-02-26T00:00:00.000Z'
    });

    expect(scheduleRetry).toHaveBeenCalledTimes(1);
    expect(scheduleRetry).toHaveBeenCalledWith(retry, 600);
    expect(reportFailure).not.toHaveBeenCalled();
    expect(fail).not.toHaveBeenCalled();
  });

  it('reports load failure and stops retry at max attempts', () => {
    const retry = vi.fn();
    const fail = vi.fn();
    const reportFailure = vi.fn();
    const scheduleRetry = vi.fn();

    handleAsyncPageLoadError(new Error('network down'), retry, fail, 3, 3, {
      reportFailure,
      scheduleRetry,
      now: () => '2026-02-26T00:00:00.000Z'
    });

    expect(scheduleRetry).not.toHaveBeenCalled();
    expect(fail).toHaveBeenCalledTimes(1);
    expect(reportFailure).toHaveBeenCalledWith(
      expect.objectContaining({
        attempts: 3,
        errorMessage: 'network down',
        timestamp: '2026-02-26T00:00:00.000Z'
      })
    );
  });
});
