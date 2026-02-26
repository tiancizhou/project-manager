<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">Project Manager</div>
      <el-menu :default-active="activePath" router>
        <el-menu-item index="/dashboard">领导看板</el-menu-item>
        <el-menu-item index="/org">组织权限</el-menu-item>
        <el-sub-menu index="/pm">
          <template #title>项目管理</template>
          <el-menu-item index="/pm/requirements">需求管理</el-menu-item>
          <el-menu-item index="/pm/tasks">任务管理</el-menu-item>
          <el-menu-item index="/pm/bugs">Bug 管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/docs">知识库</el-menu-item>
        <el-menu-item index="/integrations">对接日志</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span>{{ title }}</span>
        <el-button type="danger" link @click="onLogout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const activePath = computed(() => route.path);
const title = computed(() => String(route.meta.title || 'Project Manager'));

const onLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.aside {
  border-right: 1px solid #efefef;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.header {
  border-bottom: 1px solid #efefef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
