<template>
  <div class="admin-logs-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <div class="filters">
            <el-select v-model="moduleFilter" placeholder="操作模块" clearable @change="handleSearch" style="width: 120px; margin-right: 10px">
              <el-option label="用户管理" value="用户管理" />
              <el-option label="企业管理" value="企业管理" />
              <el-option label="职位管理" value="职位管理" />
              <el-option label="系统设置" value="系统设置" />
            </el-select>
            <el-select v-model="typeFilter" placeholder="操作类型" clearable @change="handleSearch" style="width: 120px; margin-right: 10px">
              <el-option label="新增" value="新增" />
              <el-option label="修改" value="修改" />
              <el-option label="删除" value="删除" />
              <el-option label="审核" value="审核" />
            </el-select>
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
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="module" label="操作模块" width="100" />
        <el-table-column prop="type" label="操作类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="操作内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="100" />
        <el-table-column prop="ip" label="IP地址" width="120" />
        <el-table-column prop="createTime" label="操作时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="80">
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

    <el-dialog v-model="detailDialogVisible" title="日志详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ currentLog.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getTypeTagType(currentLog.type)">{{ currentLog.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作内容">{{ currentLog.content }}</el-descriptions-item>
        <el-descriptions-item label="操作人ID">{{ currentLog.operatorId }}</el-descriptions-item>
        <el-descriptions-item label="操作人姓名">{{ currentLog.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ formatDate(currentLog.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" v-if="currentLog.requestParams">
          <pre style="max-height: 150px; overflow-y: auto; white-space: pre-wrap">{{ currentLog.requestParams }}</pre>
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
const moduleFilter = ref('')
const typeFilter = ref('')
const dateRange = ref([])
const detailDialogVisible = ref(false)
const currentLog = ref({})

const fetchLogs = async () => {
  loading.value = true
  try {
    const params = {
      module: moduleFilter.value,
      type: typeFilter.value,
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

const getTypeTagType = (type) => {
  switch (type) {
    case '新增': return 'success'
    case '修改': return 'warning'
    case '删除': return 'danger'
    case '审核': return 'primary'
    default: return 'info'
  }
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
