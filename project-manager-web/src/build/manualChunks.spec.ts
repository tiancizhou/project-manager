import { describe, expect, it } from 'vitest';
import { classifyManualChunk } from './manualChunks';

describe('manual chunk classifier', () => {
  it('splits pm pages into independent chunks', () => {
    expect(classifyManualChunk('/repo/src/views/pm/RequirementView.vue')).toBe('pm-requirements-page');
    expect(classifyManualChunk('/repo/src/views/pm/TaskView.vue')).toBe('pm-tasks-page');
    expect(classifyManualChunk('/repo/src/views/pm/BugView.vue')).toBe('pm-bugs-page');
  });

  it('classifies vendor dependencies', () => {
    expect(classifyManualChunk('/repo/node_modules/element-plus/index.js')).toBe('element-plus');
    expect(classifyManualChunk('/repo/node_modules/vue/dist/vue.runtime.esm-bundler.js')).toBe('vue-vendor');
    expect(classifyManualChunk('/repo/node_modules/axios/index.js')).toBe('http-vendor');
  });
});
