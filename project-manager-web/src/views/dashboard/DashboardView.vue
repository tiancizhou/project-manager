<template>
  <el-space direction="vertical" fill>
    <el-card>
      <el-space>
        <el-date-picker v-model="dateValue" type="date" value-format="YYYY-MM-DD" />
        <el-button :loading="loading" @click="loadSummary">查询</el-button>
        <el-button type="primary" :loading="building" @click="rebuildSnapshot">重建快照</el-button>
      </el-space>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card>
          <div class="label">任务总数</div>
          <div class="value">{{ summary.totalTaskCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="label">已完成任务</div>
          <div class="value">{{ summary.completedTaskCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div class="label">有效工时</div>
          <div class="value">{{ summary.effectiveHours }}</div>
        </el-card>
      </el-col>
    </el-row>
  </el-space>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { buildSnapshot, getDashboardSummary } from '@/api/dashboard';

const dateValue = ref(new Date().toISOString().slice(0, 10));
const loading = ref(false);
const building = ref(false);

const summary = reactive({
  totalTaskCount: 0,
  completedTaskCount: 0,
  effectiveHours: 0
});

const loadSummary = async () => {
  loading.value = true;
  try {
    const data = await getDashboardSummary(dateValue.value);
    summary.totalTaskCount = data.totalTaskCount;
    summary.completedTaskCount = data.completedTaskCount;
    summary.effectiveHours = data.effectiveHours;
  } catch {
    ElMessage.error('加载看板失败');
  } finally {
    loading.value = false;
  }
};

const rebuildSnapshot = async () => {
  building.value = true;
  try {
    await buildSnapshot(dateValue.value);
    ElMessage.success('快照重建成功');
    await loadSummary();
  } catch {
    ElMessage.error('快照重建失败');
  } finally {
    building.value = false;
  }
};

void loadSummary();
</script>

<style scoped>
.label {
  color: #777;
  margin-bottom: 8px;
}

.value {
  font-size: 28px;
  font-weight: 700;
}
</style>
