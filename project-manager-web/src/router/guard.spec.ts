import { describe, expect, it } from 'vitest';
import { shouldRedirectToLogin } from './guard';

describe('router guard helper', () => {
  it('requires login for protected route', () => {
    expect(shouldRedirectToLogin(true, '')).toBe(true);
    expect(shouldRedirectToLogin(true, 'token')).toBe(false);
    expect(shouldRedirectToLogin(false, '')).toBe(false);
  });
});
