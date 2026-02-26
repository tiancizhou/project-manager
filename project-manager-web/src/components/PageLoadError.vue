<template>
  <div class="page-load-error">
    <p class="message">页面加载失败，请重试</p>
    <button class="retry-button" type="button" @click="reloadPage">重新加载</button>
  </div>
</template>

<script setup lang="ts">
const reloadPage = () => {
  window.location.reload();
};
</script>

<style scoped>
.page-load-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 12px;
  min-height: 280px;
}

.message {
  margin: 0;
  color: #606266;
}

.retry-button {
  border: none;
  border-radius: 4px;
  background: #409eff;
  color: #fff;
  padding: 8px 14px;
  cursor: pointer;
}

.retry-button:hover {
  background: #66b1ff;
}
</style>
