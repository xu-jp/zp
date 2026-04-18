<template>
  <div class="company-jobs-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>职位管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon>
            发布职位
          </el-button>
        </div>
      </template>

      <el-table :data="jobList" v-loading="loading" style="width: 100%">
        <el-table-column label="职位名称" prop="title" min-width="150" />
        <el-table-column label="薪资范围" width="120">
          <template #default="{ row }">
            {{ formatSalary(row.salaryMin, row.salaryMax) }}
          </template>
        </el-table-column>
        <el-table-column label="工作地点" prop="location" width="120" />
        <el-table-column label="经验要求" width="100">
          <template #default="{ row }">
            {{ getExperienceText(row.experience) }}
          </template>
        </el-table-column>
        <el-table-column label="浏览/投递" width="100">
          <template #default="{ row }">
            {{ row.viewCount }} / {{ row.applicationCount }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.auditStatus !== 1" type="warning">
              待审核
            </el-tag>
            <el-tag v-else-if="row.status === 1" type="success">
              招聘中
            </el-tag>
            <el-tag v-else type="info">
              已下架
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" prop="createTime" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button 
              v-if="row.auditStatus === 1"
              :type="row.status === 1 ? 'warning' : 'success'" 
              link 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button 
              v-else
              type="primary" 
              link 
              disabled
            >
              待审核
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑职位' : '发布职位'" 
      width="700px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位名称" prop="title">
              <el-input v-model="form.title" placeholder="请输入职位名称" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位类别" prop="category">
              <el-select v-model="form.category" placeholder="请选择类别" style="width: 100%">
                <el-option label="技术开发" value="技术开发" />
                <el-option label="产品设计" value="产品设计" />
                <el-option label="运营推广" value="运营推广" />
                <el-option label="市场销售" value="市场销售" />
                <el-option label="职能支持" value="职能支持" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="最低薪资" prop="salaryMin">
              <el-input-number v-model="form.salaryMin" :min="1" :max="999" placeholder="K" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高薪资" prop="salaryMax">
              <el-input-number v-model="form.salaryMax" :min="1" :max="999" placeholder="K" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="工作地点" prop="location">
              <el-input v-model="form.location" placeholder="请输入工作地点" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经验要求" prop="experience">
              <el-select v-model="form.experience" placeholder="请选择" style="width: 100%">
                <el-option label="不限" :value="0" />
                <el-option label="1年以下" :value="1" />
                <el-option label="1-3年" :value="2" />
                <el-option label="3-5年" :value="3" />
                <el-option label="5-10年" :value="4" />
                <el-option label="10年以上" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="学历要求" prop="education">
          <el-select v-model="form.education" placeholder="请选择学历要求" style="width: 100%">
            <el-option label="不限" value="不限" />
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>

        <el-form-item label="职位描述" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入职位描述"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="任职要求" prop="requirement">
          <el-input 
            v-model="form.requirement" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入任职要求"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '保存' : '发布' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCompanyJobs, publishJob, updateJob, deleteJob, toggleJobStatus } from '@/api/companyJob'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const jobList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const formRef = ref(null)

const form = ref({
  id: null,
  title: '',
  category: '',
  description: '',
  requirement: '',
  location: '',
  salaryMin: null,
  salaryMax: null,
  experience: 0,
  education: '不限'
})

const rules = {
  title: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择职位类别', trigger: 'change' }],
  location: [{ required: true, message: '请输入工作地点', trigger: 'blur' }],
  salaryMin: [{ required: true, message: '请输入最低薪资', trigger: 'blur' }],
  salaryMax: [{ required: true, message: '请输入最高薪资', trigger: 'blur' }]
}

const fetchJobs = async () => {
  loading.value = true
  try {
    const res = await getCompanyJobs({
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

const openDialog = (row = null) => {
  isEdit.value = !!row
  if (row) {
    form.value = {
      id: row.id,
      title: row.title,
      category: row.category,
      description: row.description,
      requirement: row.requirement,
      location: row.location,
      salaryMin: row.salaryMin,
      salaryMax: row.salaryMax,
      experience: row.experience,
      education: row.education
    }
  } else {
    resetForm()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  form.value = {
    id: null,
    title: '',
    category: '',
    description: '',
    requirement: '',
    location: '',
    salaryMin: null,
    salaryMax: null,
    experience: 0,
    education: '不限'
  }
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateJob(form.value)
      ElMessage.success('修改成功')
    } else {
      await publishJob(form.value)
      ElMessage.success('发布成功，请等待审核')
    }
    dialogVisible.value = false
    fetchJobs()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleToggleStatus = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确认${row.status === 1 ? '下架' : '上架'}该职位吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
  } catch {
    return
  }

  try {
    await toggleJobStatus(row.id)
    ElMessage.success('操作成功')
    fetchJobs()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该职位吗？删除后无法恢复', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await deleteJob(row.id)
    ElMessage.success('删除成功')
    fetchJobs()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

const formatSalary = (min, max) => {
  if (!min && !max) return '面议'
  if (!min) return max + 'K以下'
  if (!max) return min + 'K以上'
  return min + 'K-' + max + 'K'
}

const getExperienceText = (exp) => {
  const map = {
    0: '不限',
    1: '1年以下',
    2: '1-3年',
    3: '3-5年',
    4: '5-10年',
    5: '10年以上'
  }
  return map[exp] || '不限'
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
.company-jobs-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
