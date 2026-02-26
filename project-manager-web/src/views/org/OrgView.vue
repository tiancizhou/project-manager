<template>
  <el-row :gutter="16">
    <el-col :span="10">
      <el-card>
        <template #header>创建用户</template>
        <el-form :model="form" label-width="80px">
          <el-form-item label="姓名">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="部门">
            <el-select v-model="form.deptId" style="width: 100%">
              <el-option v-for="dept in depts" :key="dept.id" :label="dept.name" :value="dept.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="form.roleIds" multiple style="width: 100%">
              <el-option v-for="role in roles" :key="role.id" :label="role.name" :value="role.id" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="creating" @click="submit">创建</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-col>

    <el-col :span="14">
      <el-card>
        <template #header>用户列表</template>
        <el-table :data="users" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="deptId" label="部门ID" width="120" />
          <el-table-column label="角色ID">
            <template #default="scope">
              {{ scope.row.roleIds.join(',') }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { createUser, listDepts, listRoles, listUsers, type Dept, type Role, type User } from '@/api/org';

const depts = ref<Dept[]>([]);
const roles = ref<Role[]>([]);
const users = ref<User[]>([]);
const creating = ref(false);

const form = reactive({
  name: '',
  deptId: 0,
  roleIds: [] as number[]
});

const load = async () => {
  try {
    const [deptList, roleList, userList] = await Promise.all([listDepts(), listRoles(), listUsers()]);
    depts.value = deptList;
    roles.value = roleList;
    users.value = userList;
    if (!form.deptId && deptList.length > 0) {
      form.deptId = deptList[0].id;
    }
  } catch {
    ElMessage.error('加载组织数据失败');
  }
};

const submit = async () => {
  if (!form.name.trim() || !form.deptId || form.roleIds.length === 0) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  creating.value = true;
  try {
    await createUser({
      name: form.name,
      deptId: form.deptId,
      roleIds: form.roleIds
    });
    ElMessage.success('用户创建成功');
    form.name = '';
    form.roleIds = [];
    await load();
  } catch {
    ElMessage.error('创建用户失败');
  } finally {
    creating.value = false;
  }
};

onMounted(() => {
  void load();
});
</script>
