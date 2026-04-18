<template>
  <div class="company-interviews-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>面试安排</span>
          <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="handleSearch" style="width: 150px">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已通过" :value="4" />
            <el-option label="未通过" :value="5" />
          </el-select>
        </div>
      </template>

      <div class="interview-list" v-loading="loading">
        <div 
          v-for="item in interviewList" 
          :key="item.id" 
          class="interview-item"
        >
          <div class="interview-main">
            <div class="interview-header">
              <div class="candidate-info">
                <el-avatar :size="40">{{ item.candidateName?.charAt(0) }}</el-avatar>
                <div class="candidate-detail">
                  <div class="candidate-name">{{ item.candidateName }}</div>
                  <div class="job-title">{{ item.jobTitle }}</div>
                </div>
              </div>
              <el-tag :type="getStatusType(item.status)" size="large">{{ item.statusText }}</el-tag>
            </div>

            <div class="interview-info">
              <div class="info-item">
                <el-icon><Clock /></el-icon>
                <span>面试时间：{{ formatDate(item.interviewTime) }}</span>
              </div>
              <div class="info-item">
                <el-icon><Location /></el-icon>
                <span>面试方式：{{ item.interviewType === 0 ? '现场面试' : '线上面试' }}</span>
              </div>
              <div class="info-item">
                <el-icon><MapLocation /></el-icon>
                <span>
                  {{ item.interviewType === 0 ? '面试地点' : '面试链接' }}：
                  {{ item.interviewType === 0 ? item.location : item.onlineLink }}
                </span>
              </div>
              <div class="info-item" v-if="item.interviewerName">
                <el-icon><User /></el-icon>
                <span>面试官：{{ item.interviewerName }} {{ item.interviewerPhone }}</span>
              </div>
            </div>

            <div class="interview-requirement" v-if="item.requirement">
              <span class="label">面试要求：</span>
              {{ item.requirement }}
            </div>

            <div class="interview-result" v-if="item.result">
              <span class="label">面试评价：</span>
              {{ item.result }}
            </div>
          </div>

          <div class="interview-actions" v-if="item.status === 0">
            <el-button type="primary" @click="openEditDialog(item)">编辑</el-button>
            <el-button type="warning" @click="handleCancel(item)">取消</el-button>
          </div>

          <div class="interview-actions" v-if="item.status === 1">
            <el-button type="success" @click="handleComplete(item)">标记面试完成</el-button>
            <el-button type="warning" @click="handleCancel(item)">取消</el-button>
          </div>

          <div class="interview-actions" v-if="item.status === 3">
            <el-button type="success" @click="handleResult(item, 4)">通过</el-button>
            <el-button type="danger" @click="handleResult(item, 5)">不通过</el-button>
          </div>
        </div>

        <el-empty v-if="!loading && interviewList.length === 0" description="暂无面试安排" />
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchInterviews"
          @current-change="fetchInterviews"
        />
      </div>
    </el-card>

    <el-dialog v-model="resultDialogVisible" title="面试结果" width="400px">
      <el-form :model="resultForm" label-width="80px">
        <el-form-item label="面试结果">
          <el-radio-group v-model="resultForm.status">
            <el-radio :label="4">通过</el-radio>
            <el-radio :label="5">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="评价">
          <el-input 
            v-model="resultForm.result" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入面试评价"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resultDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResult" :loading="resultLoading">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑面试安排" width="500px">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px">
        <el-form-item label="面试时间" prop="interviewTime">
          <el-date-picker
            v-model="editForm.interviewTime"
            type="datetime"
            placeholder="选择面试时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="面试方式" prop="interviewType">
          <el-radio-group v-model="editForm.interviewType">
            <el-radio :label="0">现场面试</el-radio>
            <el-radio :label="1">线上面试</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="editForm.interviewType === 0" label="面试地点" prop="location">
          <el-input v-model="editForm.location" placeholder="请输入面试地点" />
        </el-form-item>
        <el-form-item v-else label="面试链接" prop="onlineLink">
          <el-input v-model="editForm.onlineLink" placeholder="请输入线上面试链接" />
        </el-form-item>
        <el-form-item label="面试官" prop="interviewerName">
          <el-input v-model="editForm.interviewerName" placeholder="请输入面试官姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="interviewerPhone">
          <el-input v-model="editForm.interviewerPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="面试要求">
          <el-input
            v-model="editForm.requirement"
            type="textarea"
            :rows="3"
            placeholder="请输入面试要求（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="editLoading">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Location, MapLocation, User } from '@element-plus/icons-vue'
import { getCompanyInterviewList, setInterviewResult, cancelInterview, updateCompanyInterview, completeInterview } from '@/api/companyInterview'

const loading = ref(false)
const resultLoading = ref(false)
const editLoading = ref(false)
const interviewList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)
const resultDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentInterview = ref(null)
const editFormRef = ref(null)

const resultForm = ref({
  id: null,
  status: 4,
  result: ''
})

const editForm = ref({
  id: null,
  interviewTime: null,
  location: '',
  interviewType: 0,
  onlineLink: '',
  interviewerName: '',
  interviewerPhone: '',
  requirement: ''
})

const editRules = {
  interviewTime: [{ required: true, message: '请选择面试时间', trigger: 'change' }],
  interviewType: [{ required: true, message: '请选择面试方式', trigger: 'change' }],
  location: [{ required: true, message: '请输入面试地点', trigger: 'blur' }],
  onlineLink: [{ required: true, message: '请输入面试链接', trigger: 'blur' }]
}

const fetchInterviews = async () => {
  loading.value = true
  try {
    const res = await getCompanyInterviewList({
      status: statusFilter.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    interviewList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取面试列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchInterviews()
}

const handleComplete = async (item) => {
  try {
    await ElMessageBox.confirm('确认该面试已完成吗？完成后可录入面试结果', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
  } catch {
    return
  }

  try {
    await completeInterview(item.id)
    ElMessage.success('已标记完成')
    fetchInterviews()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleResult = (item, status) => {
  currentInterview.value = item
  resultForm.value = {
    id: item.id,
    status,
    result: ''
  }
  resultDialogVisible.value = true
}

const submitResult = async () => {
  resultLoading.value = true
  try {
    await setInterviewResult(resultForm.value)
    ElMessage.success('操作成功')
    resultDialogVisible.value = false
    fetchInterviews()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    resultLoading.value = false
  }
}

const openEditDialog = (item) => {
  editForm.value = {
    id: item.id,
    interviewTime: item.interviewTime ? new Date(item.interviewTime) : null,
    location: item.location || '',
    interviewType: item.interviewType ?? 0,
    onlineLink: item.onlineLink || '',
    interviewerName: item.interviewerName || '',
    interviewerPhone: item.interviewerPhone || '',
    requirement: item.requirement || ''
  }
  editDialogVisible.value = true
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const submitEdit = async () => {
  try {
    await editFormRef.value.validate()
  } catch {
    return
  }

  editLoading.value = true
  try {
    await updateCompanyInterview(editForm.value)
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    fetchInterviews()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '修改失败')
  } finally {
    editLoading.value = false
  }
}

const handleCancel = async (item) => {
  try {
    await ElMessageBox.confirm('确认取消该面试吗？取消后投递状态将恢复为已查看', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await cancelInterview(item.id)
    ElMessage.success('已取消')
    fetchInterviews()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'primary'
    case 1: return 'success'
    case 2: return 'info'
    case 3: return 'warning'
    case 4: return 'success'
    case 5: return 'danger'
    default: return 'info'
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchInterviews()
})
</script>

<style lang="scss" scoped>
.company-interviews-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .interview-list {
    .interview-item {
      padding: 20px;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .interview-main {
        .interview-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;

          .candidate-info {
            display: flex;
            align-items: center;
            gap: 12px;

            .candidate-detail {
              .candidate-name {
                font-size: 16px;
                font-weight: 600;
                color: #333;
              }

              .job-title {
                font-size: 14px;
                color: #666;
              }
            }
          }
        }

        .interview-info {
          display: flex;
          flex-wrap: wrap;
          gap: 16px;
          margin-bottom: 12px;

          .info-item {
            display: flex;
            align-items: center;
            gap: 6px;
            color: #666;
            font-size: 14px;
          }
        }

        .interview-requirement,
        .interview-result {
          padding: 12px;
          background: #f5f7fa;
          border-radius: 4px;
          margin-bottom: 8px;
          font-size: 14px;

          .label {
            color: #666;
          }
        }
      }

      .interview-actions {
        margin-top: 16px;
        display: flex;
        gap: 12px;
      }
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
