import { describe, expect, it } from 'vitest';
import router from './index';

describe('route lazy-load', () => {
  it('uses defineAsyncComponent for dashboard and pm pages', () => {
    const asyncRoutes = ['dashboard', 'pm-requirements', 'pm-tasks', 'pm-bugs'];
    for (const name of asyncRoutes) {
      const route = router.getRoutes().find((item) => item.name === name);
      const component = route?.components?.default as Record<string, unknown> | undefined;
      expect(typeof component).toBe('object');
      expect(typeof component?.__asyncLoader).toBe('function');
    }
  });

  it('keeps route lazy loader functions for other pages', () => {
    const functionRoutes = ['org', 'docs', 'integrations'];
    for (const name of functionRoutes) {
      const route = router.getRoutes().find((item) => item.name === name);
      const component = route?.components?.default;
      expect(typeof component).toBe('function');
    }
  });
});
