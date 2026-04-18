<template>
  <div class="admin-users-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="filters">
            <el-select v-model="userTypeFilter" placeholder="用户类型" clearable @change="handleSearch" style="width: 120px; margin-right: 10px">
              <el-option label="求职者" :value="1" />
              <el-option label="招聘者" :value="2" />
            </el-select>
            <el-select v-model="statusFilter" placeholder="状态" clearable @change="handleSearch" style="width: 100px; margin-right: 10px">
              <el-option label="正常" :value="0" />
              <el-option label="禁用" :value="1" />
            </el-select>
            <el-input v-model="keyword" placeholder="搜索用户名/姓名/手机号" clearable @keyup.enter="handleSearch" style="width: 200px" />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="userTypeText" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.userType === 1 ? 'primary' : 'success'">{{ row.userTypeText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="companyName" label="所属企业" width="150" />
        <el-table-column prop="statusText" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetailDialog(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0" 
              type="danger" 
              link 
              @click="handleStatusChange(row, 1)"
            >封禁</el-button>
            <el-button 
              v-if="row.status === 1" 
              type="success" 
              link 
              @click="handleStatusChange(row, 0)"
            >解封</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchUsers"
          @current-change="fetchUsers"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="用户详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentUser.realName }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">{{ currentUser.userTypeText }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="所属企业">{{ currentUser.companyName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentUser.statusText }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentUser.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ formatDate(currentUser.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, updateUserStatus } from '@/api/adminUser'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const userTypeFilter = ref(null)
const statusFilter = ref(null)
const keyword = ref('')
const detailDialogVisible = ref(false)
const currentUser = ref({})

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      userType: userTypeFilter.value,
      status: statusFilter.value,
      keyword: keyword.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    userList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchUsers()
}

const openDetailDialog = (user) => {
  currentUser.value = user
  detailDialogVisible.value = true
}

const handleStatusChange = async (user, status) => {
  const action = status === 1 ? '封禁' : '解封'
  try {
    await ElMessageBox.confirm(`确定要${action}用户 "${user.username}" 吗？`, '提示', {
      type: 'warning'
    })
    await updateUserStatus({
      userId: user.id,
      status: status,
      remark: status === 1 ? '管理员封禁' : '管理员解封'
    })
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.admin-users-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .filters {
      display: flex;
      align-items: center;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
