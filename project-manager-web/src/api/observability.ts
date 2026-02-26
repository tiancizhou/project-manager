import http from './http';

export type FrontendEventLevel = 'INFO' | 'WARN' | 'ERROR';

export interface FrontendObservabilityEvent {
  eventType: string;
  level: FrontendEventLevel;
  occurredAt: string;
  payload: Record<string, unknown>;
}

export const reportFrontendEvent = async (event: FrontendObservabilityEvent): Promise<void> => {
  await http.post('/api/v1/observability/frontend-events', event);
};
