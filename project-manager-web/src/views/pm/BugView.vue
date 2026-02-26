<template>
  <el-space direction="vertical" fill>
    <el-card>
      <template #header>Bug 创建与流转</template>
      <el-space wrap>
        <el-input v-model="title" placeholder="Bug 标题" style="width: 240px" />
        <el-select v-model="severity" style="width: 120px">
          <el-option label="HIGH" value="HIGH" />
          <el-option label="MEDIUM" value="MEDIUM" />
          <el-option label="LOW" value="LOW" />
        </el-select>
        <el-input-number v-model="taskId" :min="1" />
        <el-button type="primary" @click="createBugAction">创建 Bug</el-button>
      </el-space>
    </el-card>

    <el-card>
      <template #header>状态操作</template>
      <el-space wrap>
        <el-input-number v-model="bugId" :min="1" />
        <el-input v-model="assignee" placeholder="指派人" style="width: 180px" />
        <el-button @click="assignBugAction">指派</el-button>
        <el-button @click="resolveBugAction">解决</el-button>
        <el-button @click="verifyBugAction">验证</el-button>
        <el-button @click="closeBugAction">关闭</el-button>
        <el-button @click="refreshBug">刷新</el-button>
      </el-space>

      <el-descriptions v-if="currentBug" :column="2" border class="desc">
        <el-descriptions-item label="ID">{{ currentBug.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentBug.status }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentBug.title }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </el-space>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  assignBug,
  closeBug,
  createBug,
  getBug,
  resolveBug,
  verifyBug,
  type Bug
} from '@/api/pm';

const title = ref('');
const severity = ref('HIGH');
const taskId = ref(3001);
const bugId = ref(9001);
const assignee = ref('tom');

const currentBug = ref<Bug>();

const createBugAction = async () => {
  if (!title.value.trim()) {
    ElMessage.warning('请输入 Bug 标题');
    return;
  }
  try {
    const bug = await createBug(title.value, severity.value, taskId.value);
    currentBug.value = bug;
    bugId.value = bug.id;
    title.value = '';
    ElMessage.success('Bug 创建成功');
  } catch {
    ElMessage.error('创建 Bug 失败');
  }
};

const assignBugAction = async () => {
  try {
    currentBug.value = await assignBug(bugId.value, assignee.value);
    ElMessage.success('Bug 已指派');
  } catch {
    ElMessage.error('指派失败');
  }
};

const resolveBugAction = async () => {
  try {
    currentBug.value = await resolveBug(bugId.value);
    ElMessage.success('Bug 已解决');
  } catch {
    ElMessage.error('解决失败');
  }
};

const verifyBugAction = async () => {
  try {
    currentBug.value = await verifyBug(bugId.value);
    ElMessage.success('Bug 已验证');
  } catch {
    ElMessage.error('验证失败');
  }
};

const closeBugAction = async () => {
  try {
    currentBug.value = await closeBug(bugId.value);
    ElMessage.success('Bug 已关闭');
  } catch {
    ElMessage.error('关闭失败');
  }
};

const refreshBug = async () => {
  try {
    currentBug.value = await getBug(bugId.value);
  } catch {
    ElMessage.error('刷新 Bug 失败');
  }
};
</script>

<style scoped>
.desc {
  margin-top: 16px;
}
</style>
