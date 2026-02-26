import http from './http';

export interface SyncLog {
  id: number;
  systemCode: string;
  status: string;
  pulledCount: number;
  pushedCount: number;
  runAt: string;
}

export const runSync = async (systemCode: string): Promise<SyncLog> => {
  const response = await http.post<SyncLog>('/api/integrations/sync/run', null, {
    params: { systemCode }
  });
  return response.data;
};

export const listSyncLogs = async (): Promise<SyncLog[]> => {
  const response = await http.get<SyncLog[]>('/api/integrations/sync/logs');
  return response.data;
};
