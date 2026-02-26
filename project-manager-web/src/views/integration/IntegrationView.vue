<template>
  <el-space direction="vertical" fill>
    <el-card>
      <template #header>外部对接同步</template>
      <el-space>
        <el-select v-model="systemCode" style="width: 200px">
          <el-option label="demo" value="demo" />
          <el-option label="vendor" value="vendor" />
        </el-select>
        <el-button type="primary" :loading="running" @click="runSyncAction">执行同步</el-button>
        <el-button @click="loadLogs">刷新日志</el-button>
      </el-space>
    </el-card>

    <el-card>
      <template #header>同步日志</template>
      <el-table :data="logs" border>
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="systemCode" label="系统" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="pulledCount" label="拉取" width="100" />
        <el-table-column prop="pushedCount" label="推送" width="100" />
        <el-table-column prop="runAt" label="执行时间" />
      </el-table>
    </el-card>
  </el-space>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { listSyncLogs, runSync, type SyncLog } from '@/api/integration';

const systemCode = ref('demo');
const logs = ref<SyncLog[]>([]);
const running = ref(false);

const loadLogs = async () => {
  try {
    logs.value = await listSyncLogs();
  } catch {
    ElMessage.error('加载同步日志失败');
  }
};

const runSyncAction = async () => {
  running.value = true;
  try {
    await runSync(systemCode.value);
    ElMessage.success('同步任务已执行');
    await loadLogs();
  } catch {
    ElMessage.error('执行同步失败');
  } finally {
    running.value = false;
  }
};

onMounted(() => {
  void loadLogs();
});
</script>
