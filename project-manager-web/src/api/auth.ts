import http from './http';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
}

export const loginApi = async (payload: LoginRequest): Promise<LoginResponse> => {
  const response = await http.post<LoginResponse>('/api/auth/login', payload);
  return response.data;
};
