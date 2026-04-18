<template>
  <div class="job-detail-page" v-loading="loading">
    <div v-if="job" class="detail-container">
      <el-card class="job-card">
        <div class="job-header">
          <div class="job-title-section">
            <h1 class="job-title">{{ job.title }}</h1>
            <span class="salary">{{ job.salaryRange }}</span>
          </div>
          <div class="job-status">
            <el-tag v-if="job.auditStatus !== 1" type="warning">
              待审核
            </el-tag>
            <el-tag v-else-if="job.status === 1" type="success">
              招聘中
            </el-tag>
            <el-tag v-else type="info">
              已下架
            </el-tag>
          </div>
          <div class="job-tags">
            <el-tag type="info">{{ job.location }}</el-tag>
            <el-tag type="info">{{ job.experienceName }}</el-tag>
            <el-tag type="info">{{ job.education }}</el-tag>
            <el-tag type="info">{{ job.category }}</el-tag>
          </div>
          <div class="job-stats">
            <span><el-icon><View /></el-icon> {{ job.viewCount }} 次浏览</span>
            <span><el-icon><Document /></el-icon> {{ job.applicationCount }} 人投递</span>
          </div>
        </div>
        <div class="action-buttons">
          <el-button 
            type="primary" 
            size="large" 
            @click="handleApply"
            :disabled="hasApplied"
          >
            <el-icon><Position /></el-icon>
            {{ hasApplied ? '已投递' : '立即投递' }}
          </el-button>
          <el-button 
            size="large" 
            @click="handleFavorite"
            :type="isFavorited ? 'warning' : 'default'"
          >
            <el-icon><Star /></el-icon>
            {{ isFavorited ? '已收藏' : '收藏职位' }}
          </el-button>
        </div>
      </el-card>

      <el-card class="company-card">
        <template #header>
          <span>公司信息</span>
        </template>
        <div class="company-info">
          <el-avatar :size="64" :src="job.companyLogo || defaultLogo">
            {{ job.companyName?.charAt(0) }}
          </el-avatar>
          <div class="company-detail">
            <h3 class="company-name">{{ job.companyName }}</h3>
            <div class="company-meta">
              <span><el-icon><OfficeBuilding /></el-icon> {{ job.companyIndustry }}</span>
              <span><el-icon><User /></el-icon> {{ job.companyScale }}</span>
              <span><el-icon><Location /></el-icon> {{ job.location }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="description-card">
        <template #header>
          <span>职位描述</span>
        </template>
        <div class="description-content">
          <pre>{{ job.description }}</pre>
        </div>
      </el-card>

      <el-card class="requirement-card">
        <template #header>
          <span>任职要求</span>
        </template>
        <div class="requirement-content">
          <pre>{{ job.requirement }}</pre>
        </div>
      </el-card>
    </div>

    <el-empty v-else-if="!loading" description="职位不存在或已下线" />

    <el-dialog v-model="applyDialogVisible" title="投递简历" width="500px">
      <el-form :model="applyForm" label-width="80px">
        <el-form-item label="选择简历" required>
          <el-select v-model="applyForm.resumeId" placeholder="请选择简历" style="width: 100%">
            <el-option 
              v-for="resume in resumeList" 
              :key="resume.id" 
              :label="resume.title || '默认简历'" 
              :value="resume.id"
            >
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>{{ resume.title || '默认简历' }}</span>
                <el-tag v-if="resume.isDefault" type="success" size="small">默认</el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input 
            v-model="applyForm.remark" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注信息（选填）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="applyLoading">确认投递</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getJobDetail } from '@/api/job'
import { getResumeList } from '@/api/resume'
import { applyJob, checkApplied } from '@/api/application'
import { addFavorite, removeFavorite, checkFavorited } from '@/api/favorite'
import { ElMessage } from 'element-plus'
import { View, Document, Position, Star, OfficeBuilding, User, Location } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const job = ref(null)
const defaultLogo = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const hasApplied = ref(false)
const isFavorited = ref(false)

const applyDialogVisible = ref(false)
const applyLoading = ref(false)
const resumeList = ref([])
const applyForm = reactive({
  resumeId: null,
  remark: ''
})

const fetchJobDetail = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const res = await getJobDetail(id)
    job.value = res.data

    if (userStore.isLoggedIn && userStore.userType === 1) {
      const [appliedRes, favoritedRes] = await Promise.all([
        checkApplied(id),
        checkFavorited(id)
      ])
      hasApplied.value = appliedRes.data
      isFavorited.value = favoritedRes.data
    }
  } catch (error) {
    console.error('Failed to fetch job detail:', error)
  } finally {
    loading.value = false
  }
}

const handleApply = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (userStore.userType !== 1) {
    ElMessage.warning('仅求职者可投递职位')
    return
  }

  if (hasApplied.value) {
    ElMessage.info('您已投递过该职位')
    return
  }

  try {
    const res = await getResumeList()
    resumeList.value = res.data
    
    if (resumeList.value.length === 0) {
      ElMessage.warning('请先创建简历')
      router.push('/user/resume')
      return
    }

    const defaultResume = resumeList.value.find(r => r.isDefault === 1)
    applyForm.resumeId = defaultResume ? defaultResume.id : resumeList.value[0].id
    applyForm.remark = ''
    applyDialogVisible.value = true
  } catch (error) {
    console.error('获取简历列表失败:', error)
  }
}

const submitApply = async () => {
  if (!applyForm.resumeId) {
    ElMessage.warning('请选择简历')
    return
  }

  applyLoading.value = true
  try {
    await applyJob({
      jobId: job.value.id,
      resumeId: applyForm.resumeId,
      remark: applyForm.remark
    })
    ElMessage.success('投递成功')
    hasApplied.value = true
    applyDialogVisible.value = false
    if (job.value) {
      job.value.applicationCount = (job.value.applicationCount || 0) + 1
    }
  } catch (error) {
    console.error('投递失败:', error)
  } finally {
    applyLoading.value = false
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (userStore.userType !== 1) {
    ElMessage.warning('仅求职者可收藏职位')
    return
  }

  try {
    if (isFavorited.value) {
      await removeFavorite(job.value.id)
      ElMessage.success('已取消收藏')
      isFavorited.value = false
    } else {
      await addFavorite({ jobId: job.value.id })
      ElMessage.success('收藏成功')
      isFavorited.value = true
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

onMounted(() => {
  fetchJobDetail()
})
</script>

<style lang="scss" scoped>
.job-detail-page {
  max-width: 900px;
  margin: 0 auto;

  .detail-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .job-card {
    .job-header {
        .job-title-section {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 16px;

          .job-title {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            margin: 0;
          }

          .salary {
            font-size: 24px;
            color: #ff6b00;
            font-weight: 600;
          }
        }

        .job-status {
          display: flex;
          align-items: center;
          gap: 8px;
        }

        .job-tags {
          display: flex;
          gap: 8px;
          margin-bottom: 16px;
        }

        .job-stats {
          display: flex;
          gap: 20px;
          color: #999;
          font-size: 14px;

          span {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }

    .action-buttons {
      display: flex;
      gap: 12px;
      margin-top: 24px;
      padding-top: 24px;
      border-top: 1px solid #f0f0f0;
    }
  }

  .company-card {
    .company-info {
      display: flex;
      align-items: center;
      gap: 20px;

      .company-detail {
        .company-name {
          font-size: 18px;
          font-weight: 600;
          color: #333;
          margin: 0 0 12px 0;
        }

        .company-meta {
          display: flex;
          gap: 20px;
          color: #666;
          font-size: 14px;

          span {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
    }
  }

  .description-card,
  .requirement-card {
    pre {
      white-space: pre-wrap;
      word-wrap: break-word;
      font-family: inherit;
      font-size: 14px;
      line-height: 1.8;
      color: #333;
      margin: 0;
    }
  }
}
</style>
