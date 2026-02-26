import http from './http';

export interface Product {
  id: number;
  name: string;
}

export interface Requirement {
  id: number;
  productId: number;
  title: string;
  status: string;
}

export interface Project {
  id: number;
  name: string;
}

export interface Task {
  id: number;
  projectId: number;
  title: string;
  status: string;
  effectiveHours: number;
}

export interface Worklog {
  id: number;
  taskId: number;
  hours: number;
}

export interface Bug {
  id: number;
  title: string;
  severity: string;
  taskId?: number;
  assignee?: string;
  status: string;
}

export const createProduct = async (name: string): Promise<Product> => {
  const response = await http.post<Product>('/api/pm/products', { name });
  return response.data;
};

export const createRequirement = async (productId: number, title: string): Promise<Requirement> => {
  const response = await http.post<Requirement>('/api/pm/requirements', { productId, title });
  return response.data;
};

export const reviewRequirement = async (id: number): Promise<Requirement> => {
  const response = await http.put<Requirement>(`/api/pm/requirements/${id}/review`);
  return response.data;
};

export const closeRequirement = async (id: number): Promise<Requirement> => {
  const response = await http.put<Requirement>(`/api/pm/requirements/${id}/close`);
  return response.data;
};

export const getRequirement = async (id: number): Promise<Requirement> => {
  const response = await http.get<Requirement>(`/api/pm/requirements/${id}`);
  return response.data;
};

export const createProject = async (name: string): Promise<Project> => {
  const response = await http.post<Project>('/api/pm/projects', { name });
  return response.data;
};

export const createTask = async (projectId: number, title: string): Promise<Task> => {
  const response = await http.post<Task>('/api/pm/tasks', { projectId, title });
  return response.data;
};

export const startTask = async (id: number): Promise<Task> => {
  const response = await http.put<Task>(`/api/pm/tasks/${id}/start`);
  return response.data;
};

export const doneTask = async (id: number): Promise<Task> => {
  const response = await http.put<Task>(`/api/pm/tasks/${id}/done`);
  return response.data;
};

export const getTask = async (id: number): Promise<Task> => {
  const response = await http.get<Task>(`/api/pm/tasks/${id}`);
  return response.data;
};

export const addWorklog = async (taskId: number, hours: number): Promise<Worklog> => {
  const response = await http.post<Worklog>('/api/pm/worklogs', { taskId, hours });
  return response.data;
};

export const createBug = async (title: string, severity: string, taskId?: number): Promise<Bug> => {
  const response = await http.post<Bug>('/api/pm/bugs', { title, severity, taskId });
  return response.data;
};

export const assignBug = async (id: number, assignee: string): Promise<Bug> => {
  const response = await http.put<Bug>(`/api/pm/bugs/${id}/assign`, { assignee });
  return response.data;
};

export const resolveBug = async (id: number): Promise<Bug> => {
  const response = await http.put<Bug>(`/api/pm/bugs/${id}/resolve`);
  return response.data;
};

export const verifyBug = async (id: number): Promise<Bug> => {
  const response = await http.put<Bug>(`/api/pm/bugs/${id}/verify`);
  return response.data;
};

export const closeBug = async (id: number): Promise<Bug> => {
  const response = await http.put<Bug>(`/api/pm/bugs/${id}/close`);
  return response.data;
};

export const getBug = async (id: number): Promise<Bug> => {
  const response = await http.get<Bug>(`/api/pm/bugs/${id}`);
  return response.data;
};
