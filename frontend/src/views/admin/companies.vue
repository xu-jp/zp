<template>
  <div class="admin-companies-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业管理</span>
          <div class="filters">
            <el-select v-model="auditStatusFilter" placeholder="审核状态" clearable @change="handleSearch" style="width: 120px; margin-right: 10px">
              <el-option label="待审核" :value="0" />
              <el-option label="审核通过" :value="1" />
              <el-option label="审核拒绝" :value="2" />
            </el-select>
            <el-input v-model="keyword" placeholder="搜索企业名称" clearable @keyup.enter="handleSearch" style="width: 200px" />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
        </div>
      </template>

      <el-table :data="companyList" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="企业名称" width="180" />
        <el-table-column prop="industry" label="行业" width="100" />
        <el-table-column prop="scale" label="规模" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="120" />
        <el-table-column prop="recruiterCount" label="招聘者数" width="90" />
        <el-table-column prop="jobCount" label="职位数" width="80" />
        <el-table-column prop="auditStatusText" label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getAuditStatusType(row.auditStatus)">{{ row.auditStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetailDialog(row)">详情</el-button>
            <el-button 
              v-if="row.auditStatus === 0" 
              type="success" 
              link 
              @click="openAuditDialog(row)"
            >审核</el-button>
            <el-button 
              v-if="row.auditStatus === 1" 
              type="warning" 
              link 
              @click="handleTogglePermission(row)"
            >{{ row.recruiterCount > 0 ? '禁用招聘' : '启用招聘' }}</el-button>
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
          @size-change="fetchCompanies"
          @current-change="fetchCompanies"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="企业详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业ID">{{ currentCompany.id }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ currentCompany.name }}</el-descriptions-item>
        <el-descriptions-item label="行业">{{ currentCompany.industry }}</el-descriptions-item>
        <el-descriptions-item label="规模">{{ currentCompany.scale }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ currentCompany.address }}</el-descriptions-item>
        <el-descriptions-item label="法人代表">{{ currentCompany.legalPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentCompany.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系邮箱" :span="2">{{ currentCompany.contactEmail }}</el-descriptions-item>
        <el-descriptions-item label="企业简介" :span="2">{{ currentCompany.description }}</el-descriptions-item>
        <el-descriptions-item label="营业执照">
          <el-image 
            v-if="currentCompany.businessLicense" 
            :src="currentCompany.businessLicense" 
            style="width: 100px"
            :preview-src-list="[currentCompany.businessLicense]"
          />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getAuditStatusType(currentCompany.auditStatus)">{{ currentCompany.auditStatusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核人">{{ currentCompany.auditorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ formatDate(currentCompany.auditTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ currentCompany.auditRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="auditDialogVisible" title="审核企业" width="400px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input 
            v-model="auditForm.auditRemark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAudit" :loading="auditLoading">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCompanyList, auditCompany, toggleRecruitPermission } from '@/api/adminCompany'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const auditLoading = ref(false)
const companyList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const auditStatusFilter = ref(null)
const keyword = ref('')
const detailDialogVisible = ref(false)
const auditDialogVisible = ref(false)
const currentCompany = ref({})
const auditForm = ref({
  companyId: null,
  auditStatus: 1,
  auditRemark: ''
})

const fetchCompanies = async () => {
  loading.value = true
  try {
    const res = await getCompanyList({
      auditStatus: auditStatusFilter.value,
      keyword: keyword.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    companyList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取企业列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchCompanies()
}

const openDetailDialog = (company) => {
  currentCompany.value = company
  detailDialogVisible.value = true
}

const openAuditDialog = (company) => {
  currentCompany.value = company
  auditForm.value = {
    companyId: company.id,
    auditStatus: 1,
    auditRemark: ''
  }
  auditDialogVisible.value = true
}

const handleAudit = async () => {
  auditLoading.value = true
  try {
    await auditCompany(auditForm.value)
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    fetchCompanies()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    auditLoading.value = false
  }
}

const handleTogglePermission = async (company) => {
  const action = company.recruiterCount > 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该企业的招聘权限吗？`, '提示', {
      type: 'warning'
    })
    await toggleRecruitPermission(company.id, company.recruiterCount > 0 ? 0 : 1)
    ElMessage.success(`${action}成功`)
    fetchCompanies()
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
  fetchCompanies()
})
</script>

<style lang="scss" scoped>
.admin-companies-page {
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
