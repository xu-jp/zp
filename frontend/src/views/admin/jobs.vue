<template>
  <div class="admin-jobs-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>职位审核</span>
          <div class="filters">
            <el-select v-model="auditStatusFilter" placeholder="审核状态" clearable @change="handleSearch" style="width: 120px; margin-right: 10px">
              <el-option label="待审核" :value="0" />
              <el-option label="审核通过" :value="1" />
              <el-option label="审核拒绝" :value="2" />
            </el-select>
            <el-select v-model="statusFilter" placeholder="上架状态" clearable @change="handleSearch" style="width: 100px; margin-right: 10px">
              <el-option label="已上架" :value="1" />
              <el-option label="已下架" :value="0" />
            </el-select>
            <el-input v-model="keyword" placeholder="搜索职位名称" clearable @keyup.enter="handleSearch" style="width: 200px" />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <div class="batch-actions" v-if="selectedJobs.length > 0">
        <el-button type="success" @click="handleBatchAudit(1)">批量通过</el-button>
        <el-button type="danger" @click="handleBatchAudit(2)">批量拒绝</el-button>
        <span style="margin-left: 10px; color: #666">已选择 {{ selectedJobs.length }} 个职位</span>
      </div>

      <el-table 
        :data="jobList" 
        v-loading="loading" 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="职位名称" width="150" />
        <el-table-column prop="companyName" label="所属企业" width="150" />
        <el-table-column prop="category" label="职位类别" width="100" />
        <el-table-column prop="location" label="工作地点" width="120" />
        <el-table-column prop="salaryMin" label="薪资范围" width="120">
          <template #default="{ row }">
            {{ row.salaryMin }}-{{ row.salaryMax }}K
          </template>
        </el-table-column>
        <el-table-column prop="auditStatusText" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">{{ row.auditStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statusText" label="上架状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetailDialog(row)">详情</el-button>
            <el-button 
              v-if="row.auditStatus === 0" 
              type="success" 
              link 
              @click="handleAudit(row, 1)"
            >通过</el-button>
            <el-button 
              v-if="row.auditStatus === 0" 
              type="danger" 
              link 
              @click="handleAudit(row, 2)"
            >拒绝</el-button>
            <el-button 
              v-if="row.status === 1" 
              type="warning" 
              link 
              @click="handleOffline(row)"
            >下架</el-button>
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
          @size-change="fetchJobs"
          @current-change="fetchJobs"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="职位详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="职位ID">{{ currentJob.id }}</el-descriptions-item>
        <el-descriptions-item label="职位名称">{{ currentJob.title }}</el-descriptions-item>
        <el-descriptions-item label="所属企业">{{ currentJob.companyName }}</el-descriptions-item>
        <el-descriptions-item label="职位类别">{{ currentJob.category }}</el-descriptions-item>
        <el-descriptions-item label="工作地点">{{ currentJob.location }}</el-descriptions-item>
        <el-descriptions-item label="薪资范围">{{ currentJob.salaryMin }}-{{ currentJob.salaryMax }}K</el-descriptions-item>
        <el-descriptions-item label="经验要求">{{ currentJob.experienceText }}</el-descriptions-item>
        <el-descriptions-item label="学历要求">{{ currentJob.education }}</el-descriptions-item>
        <el-descriptions-item label="职位描述" :span="2">
          <div v-html="currentJob.description" style="max-height: 150px; overflow-y: auto"></div>
        </el-descriptions-item>
        <el-descriptions-item label="任职要求" :span="2">
          <div v-html="currentJob.requirement" style="max-height: 150px; overflow-y: auto"></div>
        </el-descriptions-item>
        <el-descriptions-item label="浏览次数">{{ currentJob.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="投递次数">{{ currentJob.applicationCount }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getAuditStatusType(currentJob.auditStatus)">{{ currentJob.auditStatusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="上架状态">
          <el-tag :type="currentJob.status === 1 ? 'success' : 'info'">{{ currentJob.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核人">{{ currentJob.auditorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ formatDate(currentJob.auditTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ currentJob.auditRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getJobList, auditJobs, offlineJob } from '@/api/adminJob'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const jobList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const auditStatusFilter = ref(null)
const statusFilter = ref(null)
const keyword = ref('')
const selectedJobs = ref([])
const detailDialogVisible = ref(false)
const currentJob = ref({})

const fetchJobs = async () => {
  loading.value = true
  try {
    const res = await getJobList({
      auditStatus: auditStatusFilter.value,
      status: statusFilter.value,
      keyword: keyword.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    jobList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取职位列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchJobs()
}

const handleSelectionChange = (selection) => {
  selectedJobs.value = selection
}

const openDetailDialog = (job) => {
  currentJob.value = job
  detailDialogVisible.value = true
}

const handleAudit = async (job, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    const { value } = await ElMessageBox.prompt(`确定要${action}该职位吗？`, '审核备注', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入审核备注（选填）'
    })
    await auditJobs({
      jobIds: [job.id],
      auditStatus: status,
      auditRemark: value || ''
    })
    ElMessage.success(`${action}成功`)
    fetchJobs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleBatchAudit = async (status) => {
  const action = status === 1 ? '通过' : '拒绝'
  const pendingJobs = selectedJobs.value.filter(j => j.auditStatus === 0)
  if (pendingJobs.length === 0) {
    ElMessage.warning('请选择待审核的职位')
    return
  }
  try {
    const { value } = await ElMessageBox.prompt(`确定要批量${action} ${pendingJobs.length} 个职位吗？`, '审核备注', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入审核备注（选填）'
    })
    await auditJobs({
      jobIds: pendingJobs.map(j => j.id),
      auditStatus: status,
      auditRemark: value || ''
    })
    ElMessage.success(`批量${action}成功`)
    fetchJobs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleOffline = async (job) => {
  try {
    await ElMessageBox.confirm('确定要下架该职位吗？', '提示', {
      type: 'warning'
    })
    await offlineJob(job.id)
    ElMessage.success('下架成功')
    fetchJobs()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const getAuditStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchJobs()
})
</script>

<style lang="scss" scoped>
.admin-jobs-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .filters {
      display: flex;
      align-items: center;
    }
  }

  .batch-actions {
    margin-bottom: 15px;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 4px;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
