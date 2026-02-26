export const shouldRedirectToLogin = (requiresAuth: boolean, token: string): boolean => {
  if (!requiresAuth) {
    return false;
  }
  return token.trim().length === 0;
};
