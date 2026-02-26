<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <span>项目管理系统登录</span>
      </template>

      <el-form :model="form" label-width="70px" @submit.prevent>
        <el-form-item label="账号">
          <el-input v-model="form.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="onSubmit">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const router = useRouter();
const authStore = useAuthStore();
const submitting = ref(false);

const form = reactive({
  username: 'admin',
  password: 'admin123'
});

const onSubmit = async () => {
  if (!form.username.trim() || !form.password.trim()) {
    ElMessage.warning('请输入账号与密码');
    return;
  }
  submitting.value = true;
  try {
    await authStore.login(form);
    ElMessage.success('登录成功');
    await router.replace('/dashboard');
  } catch (error) {
    ElMessage.error('登录失败，请检查账号密码');
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f6fa;
}

.login-card {
  width: 400px;
}
</style>
