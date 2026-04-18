<template>
  <div class="applications-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的投递</span>
          <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="handleSearch" style="width: 150px">
            <el-option label="待查看" :value="0" />
            <el-option label="已查看" :value="1" />
            <el-option label="待面试" :value="2" />
            <el-option label="已录用" :value="3" />
            <el-option label="已拒绝" :value="4" />
          </el-select>
        </div>
      </template>

      <el-table :data="applicationList" v-loading="loading" style="width: 100%">
        <el-table-column label="职位信息" min-width="250">
          <template #default="{ row }">
            <div class="job-info">
              <div class="job-title" @click="goJobDetail(row.jobId)">{{ row.jobTitle }}</div>
              <div class="company-name">{{ row.companyName }}</div>
              <div class="job-tags">
                <el-tag size="small" type="info">{{ row.location }}</el-tag>
                <el-tag size="small" type="success">{{ row.salaryRange }}</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="简历" prop="resumeTitle" width="150" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="投递时间" prop="createTime" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status <= 1" 
              type="danger" 
              link 
              @click="handleCancel(row)"
            >取消</el-button>
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
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="投递详情" width="600px">
      <el-descriptions :column="2" border v-if="currentApplication">
        <el-descriptions-item label="职位名称">{{ currentApplication.jobTitle }}</el-descriptions-item>
        <el-descriptions-item label="公司名称">{{ currentApplication.companyName }}</el-descriptions-item>
        <el-descriptions-item label="工作地点">{{ currentApplication.location }}</el-descriptions-item>
        <el-descriptions-item label="薪资范围">{{ currentApplication.salaryRange }}</el-descriptions-item>
        <el-descriptions-item label="投递简历">{{ currentApplication.resumeTitle }}</el-descriptions-item>
        <el-descriptions-item label="投递状态">
          <el-tag :type="getStatusType(currentApplication.status)">{{ currentApplication.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投递时间">{{ formatDate(currentApplication.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="查看时间">{{ currentApplication.viewTime ? formatDate(currentApplication.viewTime) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentApplication.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getApplicationList, cancelApplication } from '@/api/application'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const applicationList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)
const detailVisible = ref(false)
const currentApplication = ref(null)

const fetchApplications = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (statusFilter.value !== null) {
      params.status = statusFilter.value
    }
    const res = await getApplicationList(params)
    applicationList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取投递列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchApplications()
}

const viewDetail = (row) => {
  currentApplication.value = row
  detailVisible.value = true
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该投递吗？', '提示', {
      type: 'warning'
    })
    await cancelApplication(row.id)
    ElMessage.success('取消成功')
    fetchApplications()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消投递失败:', error)
    }
  }
}

const goJobDetail = (jobId) => {
  router.push(`/jobs/${jobId}`)
}

const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return types[status] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchApplications()
})
</script>

<style lang="scss" scoped>
.applications-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .job-info {
    .job-title {
      font-weight: 600;
      color: #333;
      cursor: pointer;
      
      &:hover {
        color: #409eff;
      }
    }

    .company-name {
      color: #666;
      font-size: 13px;
      margin: 4px 0;
    }

    .job-tags {
      display: flex;
      gap: 8px;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
