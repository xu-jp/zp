<template>
  <div class="admin-logs-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <div class="filters">
            <el-input v-model="operationFilter" placeholder="操作描述" clearable style="width: 180px; margin-right: 10px" @keyup.enter="handleSearch" />
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="handleSearch"
              style="margin-right: 10px"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="logList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="operation" label="操作描述" min-width="120" show-overflow-tooltip />
        <el-table-column prop="method" label="操作方法" min-width="180" show-overflow-tooltip />
        <el-table-column prop="params" label="请求参数" min-width="200" show-overflow-tooltip />
        <el-table-column prop="username" label="操作人" min-width="130">
          <template #default="{ row }">
            {{ row.username || '-' }} (ID: {{ row.userId }})
          </template>
        </el-table-column>
        <el-table-column prop="status" label="操作状态" min-width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" min-width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="70">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetailDialog(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchLogs"
          @current-change="fetchLogs"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="日志详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ currentLog.operation }}</el-descriptions-item>
        <el-descriptions-item label="操作方法">{{ currentLog.method }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.username }} (ID: {{ currentLog.userId }})</el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="currentLog.status === 0 ? 'success' : 'danger'">{{ currentLog.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatDate(currentLog.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" v-if="currentLog.params">
          <pre style="max-height: 200px; overflow-y: auto; white-space: pre-wrap; background: #f5f7fa; padding: 10px; border-radius: 4px">{{ currentLog.params }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息" v-if="currentLog.errorMsg">
          <span style="color: #f56c6c">{{ currentLog.errorMsg }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogList } from '@/api/operationLog'

const loading = ref(false)
const logList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const operationFilter = ref('')
const dateRange = ref([])
const detailDialogVisible = ref(false)
const currentLog = ref({})

const fetchLogs = async () => {
  loading.value = true
  try {
    const params = {
      operation: operationFilter.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString().split('T')[0]
      params.endDate = dateRange.value[1].toISOString().split('T')[0]
    }
    const res = await getLogList(params)
    logList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取日志列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchLogs()
}

const openDetailDialog = (log) => {
  currentLog.value = log
  detailDialogVisible.value = true
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchLogs()
})
</script>

<style lang="scss" scoped>
.admin-logs-page {
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
