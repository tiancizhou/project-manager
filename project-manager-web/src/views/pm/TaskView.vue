<template>
  <el-space direction="vertical" fill>
    <el-card>
      <template #header>项目与任务</template>
      <el-space wrap>
        <el-input v-model="projectName" placeholder="项目名称" style="width: 220px" />
        <el-button @click="createProjectAction">创建项目</el-button>
        <el-input-number v-model="projectId" :min="1" />
        <el-input v-model="taskTitle" placeholder="任务标题" style="width: 220px" />
        <el-button type="primary" @click="createTaskAction">创建任务</el-button>
      </el-space>
    </el-card>

    <el-card>
      <template #header>工时与状态流转</template>
      <el-space wrap>
        <el-input-number v-model="taskId" :min="1" />
        <el-input-number v-model="hours" :min="0.5" :step="0.5" />
        <el-button @click="addWorklogAction">登记工时</el-button>
        <el-button @click="startTaskAction">开始任务</el-button>
        <el-button @click="doneTaskAction">完成任务</el-button>
        <el-button @click="refreshTask">刷新任务</el-button>
      </el-space>

      <el-descriptions v-if="currentTask" :column="2" border class="desc">
        <el-descriptions-item label="ID">{{ currentTask.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentTask.status }}</el-descriptions-item>
        <el-descriptions-item label="有效工时">{{ currentTask.effectiveHours }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ currentTask.title }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </el-space>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  addWorklog,
  createProject,
  createTask,
  doneTask,
  getTask,
  startTask,
  type Task
} from '@/api/pm';

const projectName = ref('');
const projectId = ref(201);
const taskTitle = ref('');
const taskId = ref(3001);
const hours = ref(1);

const currentTask = ref<Task>();

const createProjectAction = async () => {
  if (!projectName.value.trim()) {
    ElMessage.warning('请输入项目名称');
    return;
  }
  try {
    const project = await createProject(projectName.value);
    projectId.value = project.id;
    projectName.value = '';
    ElMessage.success('项目创建成功');
  } catch {
    ElMessage.error('项目创建失败');
  }
};

const createTaskAction = async () => {
  if (!taskTitle.value.trim()) {
    ElMessage.warning('请输入任务标题');
    return;
  }
  try {
    const task = await createTask(projectId.value, taskTitle.value);
    currentTask.value = task;
    taskId.value = task.id;
    taskTitle.value = '';
    ElMessage.success('任务创建成功');
  } catch {
    ElMessage.error('任务创建失败');
  }
};

const addWorklogAction = async () => {
  try {
    await addWorklog(taskId.value, hours.value);
    ElMessage.success('工时登记成功');
  } catch {
    ElMessage.error('工时登记失败');
  }
};

const startTaskAction = async () => {
  try {
    currentTask.value = await startTask(taskId.value);
    ElMessage.success('任务已开始');
  } catch {
    ElMessage.error('开始任务失败');
  }
};

const doneTaskAction = async () => {
  try {
    currentTask.value = await doneTask(taskId.value);
    ElMessage.success('任务已完成');
  } catch {
    ElMessage.error('完成任务失败');
  }
};

const refreshTask = async () => {
  try {
    currentTask.value = await getTask(taskId.value);
  } catch {
    ElMessage.error('刷新任务失败');
  }
};
</script>

<style scoped>
.desc {
  margin-top: 16px;
}
</style>
