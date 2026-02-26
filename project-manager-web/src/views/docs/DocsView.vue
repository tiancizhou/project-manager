<template>
  <el-space direction="vertical" fill>
    <el-card>
      <template #header>知识库空间</template>
      <el-space>
        <el-input v-model="spaceName" placeholder="空间名称" style="width: 260px" />
        <el-button @click="createSpaceAction">创建空间</el-button>
      </el-space>
      <div v-if="currentSpace" class="tip">当前空间：{{ currentSpace.id }} - {{ currentSpace.name }}</div>
    </el-card>

    <el-card>
      <template #header>文档页面与修订</template>
      <el-space direction="vertical" fill>
        <el-space wrap>
          <el-input-number v-model="spaceId" :min="1" />
          <el-input v-model="pageTitle" placeholder="页面标题" style="width: 220px" />
          <el-input v-model="content" placeholder="内容" style="width: 320px" />
          <el-button type="primary" @click="createPageAction">创建页面</el-button>
        </el-space>

        <el-space wrap>
          <el-input-number v-model="pageId" :min="1" />
          <el-input v-model="newContent" placeholder="新内容" style="width: 320px" />
          <el-button @click="updatePageAction">更新页面</el-button>
          <el-button @click="loadRevisions">加载修订</el-button>
        </el-space>

        <el-table :data="revisions" border>
          <el-table-column prop="id" label="ID" width="90" />
          <el-table-column prop="version" label="版本" width="90" />
          <el-table-column prop="content" label="内容" />
        </el-table>
      </el-space>
    </el-card>
  </el-space>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { createPage, createSpace, listRevisions, updatePage, type DocRevision, type DocSpace } from '@/api/docs';

const spaceName = ref('');
const spaceId = ref(101);
const pageTitle = ref('');
const content = ref('');
const pageId = ref(1001);
const newContent = ref('');

const currentSpace = ref<DocSpace>();
const revisions = ref<DocRevision[]>([]);

const createSpaceAction = async () => {
  if (!spaceName.value.trim()) {
    ElMessage.warning('请输入空间名称');
    return;
  }
  try {
    const space = await createSpace(spaceName.value);
    currentSpace.value = space;
    spaceId.value = space.id;
    spaceName.value = '';
    ElMessage.success('空间创建成功');
  } catch {
    ElMessage.error('创建空间失败');
  }
};

const createPageAction = async () => {
  if (!pageTitle.value.trim() || !content.value.trim()) {
    ElMessage.warning('请填写页面标题和内容');
    return;
  }
  try {
    const page = await createPage(spaceId.value, pageTitle.value, content.value);
    pageId.value = page.id;
    pageTitle.value = '';
    content.value = '';
    ElMessage.success('页面创建成功');
  } catch {
    ElMessage.error('页面创建失败');
  }
};

const updatePageAction = async () => {
  if (!newContent.value.trim()) {
    ElMessage.warning('请输入新内容');
    return;
  }
  try {
    await updatePage(pageId.value, newContent.value);
    ElMessage.success('页面更新成功');
    newContent.value = '';
    await loadRevisions();
  } catch {
    ElMessage.error('页面更新失败');
  }
};

const loadRevisions = async () => {
  try {
    revisions.value = await listRevisions(pageId.value);
  } catch {
    ElMessage.error('加载修订失败');
  }
};
</script>

<style scoped>
.tip {
  margin-top: 10px;
  color: #666;
}
</style>
