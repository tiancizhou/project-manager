export const classifyManualChunk = (id: string): string | undefined => {
  if (id.includes('node_modules')) {
    if (id.includes('element-plus')) {
      return 'element-plus';
    }
    if (id.includes('vue')) {
      return 'vue-vendor';
    }
    if (id.includes('axios')) {
      return 'http-vendor';
    }
    return 'vendor';
  }

  if (id.includes('/src/views/pm/RequirementView.vue')) {
    return 'pm-requirements-page';
  }
  if (id.includes('/src/views/pm/TaskView.vue')) {
    return 'pm-tasks-page';
  }
  if (id.includes('/src/views/pm/BugView.vue')) {
    return 'pm-bugs-page';
  }
  if (id.includes('/src/views/docs/')) {
    return 'docs-pages';
  }
  if (id.includes('/src/views/integration/')) {
    return 'integration-pages';
  }

  return undefined;
};
