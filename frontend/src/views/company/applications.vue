<template>
  <div class="company-applications-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>收到的投递</span>
          <div class="header-actions">
            <el-button type="primary" @click="openAIScreenDialog">
              <el-icon><MagicStick /></el-icon>
              AI智能筛选
            </el-button>
            <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="handleSearch" style="width: 150px">
              <el-option label="待查看" :value="0" />
              <el-option label="已查看" :value="1" />
              <el-option label="待面试" :value="2" />
              <el-option label="已录用" :value="3" />
              <el-option label="已拒绝" :value="4" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table :data="applicationList" v-loading="loading" style="width: 100%">
        <el-table-column label="求职者" min-width="150">
          <template #default="{ row }">
            <div class="user-info">
              <span class="name">{{ row.resumeRealName || row.userName || '-' }}</span>
              <span class="phone" v-if="row.userPhone">{{ row.userPhone }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="应聘职位" prop="jobTitle" min-width="150" />
        <el-table-column label="学历" prop="resumeEducation" width="100" />
        <el-table-column label="学校" prop="resumeSchool" width="150" />
        <el-table-column label="AI匹配度" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.aiMatchLevel" :type="getMatchLevelType(row.aiMatchLevel)">
              {{ row.aiMatchLevel }} ({{ row.aiMatchScore || 0 }}分)
            </el-tag>
            <span v-else class="text-muted">未分析</span>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看简历</el-button>
            <el-button type="warning" link @click="openSingleAIScreen(row)">AI分析</el-button>
            <el-button 
              v-if="row.status <= 1" 
              type="success" 
              link 
              @click="handleStatus(row, 2)"
            >邀请面试</el-button>
            <el-button 
              v-if="row.status <= 1" 
              type="danger" 
              link 
              @click="handleStatus(row, 4)"
            >拒绝</el-button>
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
          @size-change="fetchApplications"
          @current-change="fetchApplications"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="简历详情" width="800px">
      <div class="resume-detail" v-if="currentApplication">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ currentApplication.resumeRealName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentApplication.userPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentApplication.userEmail || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学历">{{ currentApplication.resumeEducation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学校">{{ currentApplication.resumeSchool || '-' }}</el-descriptions-item>
          <el-descriptions-item label="应聘职位">{{ currentApplication.jobTitle }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">技能特长</el-divider>
        <div class="content-box">{{ currentApplication.resumeSkills || '暂无' }}</div>

        <el-divider content-position="left">工作经历</el-divider>
        <div class="content-box">{{ currentApplication.resumeWorkExperience || '暂无' }}</div>

        <el-divider content-position="left">自我介绍</el-divider>
        <div class="content-box">{{ currentApplication.resumeSelfIntroduction || '暂无' }}</div>

        <el-divider content-position="left">备注</el-divider>
        <el-input
          v-model="remarkForm.remark"
          type="textarea"
          :rows="3"
          placeholder="添加备注信息"
          maxlength="200"
          show-word-limit
        />
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="saveRemark" :loading="remarkLoading">保存备注</el-button>
        <el-button type="warning" @click="openSingleAIScreen(currentApplication)">AI分析</el-button>
        <el-button 
          v-if="currentApplication?.status === 1 || currentApplication?.status === 2" 
          type="success" 
          @click="openInterviewDialog"
        >安排面试</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="interviewDialogVisible" title="安排面试" width="500px">
      <el-form ref="interviewFormRef" :model="interviewForm" :rules="interviewRules" label-width="100px">
        <el-form-item label="面试时间" prop="interviewTime">
          <el-date-picker
            v-model="interviewForm.interviewTime"
            type="datetime"
            placeholder="选择面试时间"
            style="width: 100%"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="面试方式" prop="interviewType">
          <el-radio-group v-model="interviewForm.interviewType">
            <el-radio :label="0">现场面试</el-radio>
            <el-radio :label="1">线上面试</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="interviewForm.interviewType === 0" label="面试地点" prop="location">
          <el-input v-model="interviewForm.location" placeholder="请输入面试地点" />
        </el-form-item>
        <el-form-item v-else label="面试链接" prop="onlineLink">
          <el-input v-model="interviewForm.onlineLink" placeholder="请输入线上面试链接" />
        </el-form-item>
        <el-form-item label="面试官" prop="interviewerName">
          <el-input v-model="interviewForm.interviewerName" placeholder="请输入面试官姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="interviewerPhone">
          <el-input v-model="interviewForm.interviewerPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="面试要求">
          <el-input
            v-model="interviewForm.requirement"
            type="textarea"
            :rows="3"
            placeholder="请输入面试要求（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="interviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitInterview" :loading="interviewLoading">确认安排</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="aiScreenDialogVisible" title="AI智能筛选设置" width="600px">
      <el-form :model="aiScreenForm" label-width="120px">
        <el-form-item label="筛选简历">
          <el-radio-group v-model="aiScreenForm.screenMode">
            <el-radio label="single">单个简历</el-radio>
            <el-radio label="batch">批量筛选</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="aiScreenForm.screenMode === 'batch'" label="选择职位">
          <el-select v-model="aiScreenForm.jobId" placeholder="请选择职位" style="width: 100%">
            <el-option v-for="job in jobList" :key="job.id" :label="job.title" :value="job.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键技能">
          <el-select 
            v-model="aiScreenForm.skillKeywords" 
            multiple 
            filterable 
            allow-create
            placeholder="输入技能关键词后回车"
            style="width: 100%"
          >
            <el-option label="Java" value="Java" />
            <el-option label="Python" value="Python" />
            <el-option label="JavaScript" value="JavaScript" />
            <el-option label="Vue" value="Vue" />
            <el-option label="React" value="React" />
            <el-option label="Spring Boot" value="Spring Boot" />
            <el-option label="MySQL" value="MySQL" />
            <el-option label="Redis" value="Redis" />
          </el-select>
        </el-form-item>
        <el-form-item label="最低工作年限">
          <el-input-number v-model="aiScreenForm.minExperience" :min="0" :max="20" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最低学历">
          <el-select v-model="aiScreenForm.minEducation" placeholder="请选择" style="width: 100%">
            <el-option label="不限" value="" />
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
        </el-form-item>
        <el-form-item label="额外要求">
          <el-input 
            v-model="aiScreenForm.jobRequirements" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入其他筛选要求（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="aiScreenDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="executeAIScreen" :loading="aiScreenLoading">开始分析</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="aiResultDialogVisible" title="AI分析结果" width="700px">
      <div class="ai-result" v-if="aiResult">
        <div class="result-header">
          <div class="candidate-name">{{ aiResult.candidateName }}</div>
          <el-tag :type="getMatchLevelType(aiResult.matchLevel)" size="large">
            {{ aiResult.matchLevel }}匹配 ({{ aiResult.matchScore }}分)
          </el-tag>
        </div>

        <el-divider content-position="left">核心亮点</el-divider>
        <div class="highlight-list">
          <el-tag v-for="(h, i) in aiResult.highlights" :key="i" type="success" class="highlight-tag">
            {{ h }}
          </el-tag>
        </div>

        <el-row :gutter="20" class="match-section">
          <el-col :span="12">
            <div class="match-box">
              <div class="match-title">匹配技能</div>
              <div class="skill-list">
                <el-tag v-for="(s, i) in aiResult.matchedSkills" :key="i" type="success" size="small" class="skill-tag">
                  {{ s }}
                </el-tag>
                <span v-if="!aiResult.matchedSkills?.length" class="text-muted">无</span>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="match-box">
              <div class="match-title">缺失技能</div>
              <div class="skill-list">
                <el-tag v-for="(s, i) in aiResult.missingSkills" :key="i" type="danger" size="small" class="skill-tag">
                  {{ s }}
                </el-tag>
                <span v-if="!aiResult.missingSkills?.length" class="text-muted">无</span>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-divider content-position="left">详细分析</el-divider>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="经验匹配">{{ aiResult.experienceMatch }}</el-descriptions-item>
          <el-descriptions-item label="学历匹配">{{ aiResult.educationMatch }}</el-descriptions-item>
          <el-descriptions-item label="综合评价">{{ aiResult.summary }}</el-descriptions-item>
          <el-descriptions-item label="录用建议">
            <el-tag :type="aiResult.recommendation?.includes('建议') ? 'success' : 'warning'">
              {{ aiResult.recommendation }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="aiResultDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="handleStatusFromAI(2)">邀请面试</el-button>
        <el-button type="danger" @click="handleStatusFromAI(4)">拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import { getApplicationList, getApplicationDetail, handleApplication, createInterview } from '@/api/companyApplication'
import { screenResume, batchScreenResumes } from '@/api/aiScreen'
import { getCompanyJobs } from '@/api/companyJob'

const loading = ref(false)
const remarkLoading = ref(false)
const interviewLoading = ref(false)
const aiScreenLoading = ref(false)
const applicationList = ref([])
const jobList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)
const detailVisible = ref(false)
const interviewDialogVisible = ref(false)
const aiScreenDialogVisible = ref(false)
const aiResultDialogVisible = ref(false)
const currentApplication = ref(null)
const aiResult = ref(null)
const interviewFormRef = ref(null)

const remarkForm = ref({
  id: null,
  remark: ''
})

const interviewForm = ref({
  applicationId: null,
  interviewTime: null,
  location: '',
  interviewType: 0,
  onlineLink: '',
  interviewerName: '',
  interviewerPhone: '',
  requirement: ''
})

const aiScreenForm = ref({
  applicationId: null,
  jobId: null,
  screenMode: 'single',
  skillKeywords: [],
  minExperience: null,
  minEducation: '',
  jobRequirements: ''
})

const interviewRules = {
  interviewTime: [{ required: true, message: '请选择面试时间', trigger: 'change' }],
  interviewType: [{ required: true, message: '请选择面试方式', trigger: 'change' }],
  location: [{ required: true, message: '请输入面试地点', trigger: 'blur' }],
  onlineLink: [{ required: true, message: '请输入面试链接', trigger: 'blur' }],
  interviewerName: [{ required: true, message: '请输入面试官姓名', trigger: 'blur' }],
  interviewerPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const fetchApplications = async () => {
  loading.value = true
  try {
    const res = await getApplicationList({
      status: statusFilter.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    applicationList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('获取投递列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchJobList = async () => {
  try {
    const res = await getCompanyJobs({ pageNum: 1, pageSize: 100 })
    jobList.value = res.data?.records || []
  } catch (error) {
    console.error('获取职位列表失败:', error)
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchApplications()
}

const viewDetail = async (row) => {
  try {
    const res = await getApplicationDetail(row.id)
    currentApplication.value = res.data
    remarkForm.value = {
      id: row.id,
      remark: res.data.remark || ''
    }
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleStatus = async (row, status) => {
  if (status === 2) {
    currentApplication.value = row
    interviewForm.value = {
      applicationId: row.id,
      interviewTime: null,
      location: '',
      interviewType: 0,
      onlineLink: '',
      interviewerName: '',
      interviewerPhone: '',
      requirement: ''
    }
    interviewDialogVisible.value = true
    return
  }

  const statusText = status === 3 ? '录用' : '拒绝'
  try {
    await ElMessageBox.confirm(`确认${statusText}该求职者吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await handleApplication({ id: row.id, status })
    ElMessage.success('操作成功')
    fetchApplications()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const saveRemark = async () => {
  remarkLoading.value = true
  try {
    await handleApplication(remarkForm.value)
    ElMessage.success('备注保存成功')
    fetchApplications()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    remarkLoading.value = false
  }
}

const openInterviewDialog = () => {
  interviewForm.value = {
    applicationId: currentApplication.value.id,
    interviewTime: null,
    location: '',
    interviewType: 0,
    onlineLink: '',
    interviewerName: '',
    interviewerPhone: '',
    requirement: ''
  }
  interviewDialogVisible.value = true
}

const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const submitInterview = async () => {
  try {
    await interviewFormRef.value.validate()
  } catch {
    return
  }

  interviewLoading.value = true
  try {
    await createInterview(interviewForm.value)
    ElMessage.success('面试安排成功')
    interviewDialogVisible.value = false
    detailVisible.value = false
    fetchApplications()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '安排失败')
  } finally {
    interviewLoading.value = false
  }
}

const openAIScreenDialog = () => {
  aiScreenForm.value = {
    applicationId: null,
    jobId: null,
    screenMode: 'single',
    skillKeywords: [],
    minExperience: null,
    minEducation: '',
    jobRequirements: ''
  }
  fetchJobList()
  aiScreenDialogVisible.value = true
}

const openSingleAIScreen = (row) => {
  aiScreenForm.value = {
    applicationId: row.id,
    jobId: null,
    screenMode: 'single',
    skillKeywords: [],
    minExperience: null,
    minEducation: '',
    jobRequirements: ''
  }
  currentApplication.value = row
  aiScreenDialogVisible.value = true
}

const executeAIScreen = async () => {
  aiScreenLoading.value = true
  try {
    if (aiScreenForm.value.screenMode === 'single') {
      if (!aiScreenForm.value.applicationId && currentApplication.value) {
        aiScreenForm.value.applicationId = currentApplication.value.id
      }
      const res = await screenResume(aiScreenForm.value)
      aiResult.value = res.data
      aiResultDialogVisible.value = true
      aiScreenDialogVisible.value = false
      detailVisible.value = false
    } else {
      if (!aiScreenForm.value.jobId) {
        ElMessage.warning('请选择要筛选的职位')
        return
      }
      await batchScreenResumes(aiScreenForm.value.jobId, aiScreenForm.value)
      ElMessage.success('批量筛选已启动，请稍后刷新查看结果')
      aiScreenDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || 'AI分析失败')
  } finally {
    aiScreenLoading.value = false
  }
}

const handleStatusFromAI = async (status) => {
  if (!currentApplication.value) return
  await handleStatus(currentApplication.value, status)
  aiResultDialogVisible.value = false
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'info'
    case 1: return 'warning'
    case 2: return 'primary'
    case 3: return 'success'
    case 4: return 'danger'
    default: return 'info'
  }
}

const getMatchLevelType = (level) => {
  switch (level) {
    case '高': return 'success'
    case '中': return 'warning'
    case '低': return 'danger'
    default: return 'info'
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchApplications()
})
</script>

<style lang="scss" scoped>
.company-applications-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-actions {
      display: flex;
      gap: 12px;
    }
  }

  .user-info {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .name {
      font-weight: 600;
      color: #333;
    }

    .phone {
      font-size: 12px;
      color: #999;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .resume-detail {
    .content-box {
      padding: 12px;
      background: #f5f7fa;
      border-radius: 4px;
      white-space: pre-wrap;
      line-height: 1.6;
    }
  }

  .text-muted {
    color: #999;
    font-size: 12px;
  }

  .ai-result {
    .result-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .candidate-name {
        font-size: 20px;
        font-weight: 600;
        color: #333;
      }
    }

    .highlight-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .highlight-tag {
        margin: 0;
      }
    }

    .match-section {
      margin: 20px 0;

      .match-box {
        padding: 16px;
        background: #f5f7fa;
        border-radius: 8px;

        .match-title {
          font-weight: 600;
          margin-bottom: 12px;
          color: #333;
        }

        .skill-list {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;

          .skill-tag {
            margin: 0;
          }
        }
      }
    }
  }
}
</style>
