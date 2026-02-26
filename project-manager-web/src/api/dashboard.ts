import http from './http';

export interface DashboardSummary {
  date: string;
  totalTaskCount: number;
  completedTaskCount: number;
  effectiveHours: number;
}

export const buildSnapshot = async (date: string): Promise<DashboardSummary> => {
  const response = await http.post<DashboardSummary>('/api/dashboard/snapshots/build', null, {
    params: { date }
  });
  return response.data;
};

export const getDashboardSummary = async (date: string): Promise<DashboardSummary> => {
  const response = await http.get<DashboardSummary>('/api/dashboard/summary', {
    params: { date }
  });
  return response.data;
};
