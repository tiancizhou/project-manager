import http from './http';

export interface Dept {
  id: number;
  name: string;
  parentId?: number | null;
}

export interface Role {
  id: number;
  code: string;
  name: string;
}

export interface User {
  id: number;
  name: string;
  deptId: number;
  roleIds: number[];
}

export interface CreateUserRequest {
  name: string;
  deptId: number;
  roleIds: number[];
}

export const listDepts = async (): Promise<Dept[]> => {
  const response = await http.get<Dept[]>('/api/org/depts');
  return response.data;
};

export const listRoles = async (): Promise<Role[]> => {
  const response = await http.get<Role[]>('/api/org/roles');
  return response.data;
};

export const listUsers = async (): Promise<User[]> => {
  const response = await http.get<User[]>('/api/org/users');
  return response.data;
};

export const createUser = async (payload: CreateUserRequest): Promise<User> => {
  const response = await http.post<User>('/api/org/users', payload);
  return response.data;
};
