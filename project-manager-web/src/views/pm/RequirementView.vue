<template>
  <el-space direction="vertical" fill>
    <el-card>
      <template #header>产品创建</template>
      <el-space>
        <el-input v-model="productName" placeholder="产品名称" style="width: 280px" />
        <el-button @click="createProductAction">创建产品</el-button>
      </el-space>
      <div v-if="currentProduct" class="tip">当前产品：{{ currentProduct.id }} - {{ currentProduct.name }}</div>
    </el-card>

    <el-card>
      <template #header>需求流转</template>
      <el-space wrap>
        <el-input-number v-model="productId" :min="1" />
        <el-input v-model="requirementTitle" placeholder="需求标题" style="width: 280px" />
        <el-button type="primary" @click="createRequirementAction">创建需求</el-button>
        <el-button :disabled="!currentRequirement" @click="reviewRequirementAction">评审通过</el-button>
        <el-button :disabled="!currentRequirement" @click="closeRequirementAction">关闭需求</el-button>
        <el-button :disabled="!currentRequirement" @click="refreshRequirement">刷新</el-button>
      </el-space>

      <el-descriptions v-if="currentRequirement" :column="2" border class="desc">
        <el-descriptions-item label="ID">{{ currentRequirement.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentRequirement.status }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentRequirement.title }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </el-space>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  closeRequirement,
  createProduct,
  createRequirement,
  getRequirement,
  reviewRequirement,
  type Product,
  type Requirement
} from '@/api/pm';

const productName = ref('');
const productId = ref(101);
const requirementTitle = ref('');

const currentProduct = ref<Product>();
const currentRequirement = ref<Requirement>();

const createProductAction = async () => {
  if (!productName.value.trim()) {
    ElMessage.warning('请输入产品名称');
    return;
  }
  try {
    const product = await createProduct(productName.value);
    currentProduct.value = product;
    productId.value = product.id;
    productName.value = '';
    ElMessage.success('产品创建成功');
  } catch {
    ElMessage.error('产品创建失败');
  }
};

const createRequirementAction = async () => {
  if (!requirementTitle.value.trim()) {
    ElMessage.warning('请输入需求标题');
    return;
  }
  try {
    const requirement = await createRequirement(productId.value, requirementTitle.value);
    currentRequirement.value = requirement;
    requirementTitle.value = '';
    ElMessage.success('需求创建成功');
  } catch {
    ElMessage.error('需求创建失败');
  }
};

const reviewRequirementAction = async () => {
  if (!currentRequirement.value) {
    return;
  }
  try {
    currentRequirement.value = await reviewRequirement(currentRequirement.value.id);
    ElMessage.success('需求评审通过');
  } catch {
    ElMessage.error('需求评审失败');
  }
};

const closeRequirementAction = async () => {
  if (!currentRequirement.value) {
    return;
  }
  try {
    currentRequirement.value = await closeRequirement(currentRequirement.value.id);
    ElMessage.success('需求已关闭');
  } catch {
    ElMessage.error('关闭需求失败');
  }
};

const refreshRequirement = async () => {
  if (!currentRequirement.value) {
    return;
  }
  try {
    currentRequirement.value = await getRequirement(currentRequirement.value.id);
  } catch {
    ElMessage.error('刷新需求失败');
  }
};
</script>

<style scoped>
.tip {
  margin-top: 12px;
  color: #666;
}

.desc {
  margin-top: 16px;
}
</style>
