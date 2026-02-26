import { defineStore } from 'pinia';
import { loginApi, type LoginRequest } from '@/api/auth';
import { clearToken, getToken, setToken } from '@/utils/auth';

interface AuthState {
  token: string;
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: getToken()
  }),
  getters: {
    isAuthenticated: (state) => state.token.trim().length > 0
  },
  actions: {
    async login(payload: LoginRequest) {
      const result = await loginApi(payload);
      this.token = result.token;
      setToken(result.token);
    },
    logout() {
      this.token = '';
      clearToken();
    }
  }
});
