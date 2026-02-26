import { describe, expect, it } from 'vitest';
import { clearToken, getToken, setToken } from './auth';

describe('auth token helpers', () => {
  it('can persist token into localStorage', () => {
    clearToken();
    setToken('abc123');
    expect(getToken()).toBe('abc123');
    clearToken();
  });
});
