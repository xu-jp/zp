<template>
  <div class="job-detail-page" v-loading="loading">
    <div v-if="job" class="detail-container">
      <div class="job-hero">
        <div class="job-main-info">
          <div class="job-header">
            <div class="title-section">
              <h1 class="job-title">{{ job.title }}</h1>
              <span class="salary">{{ job.salaryRange }}</span>
            </div>
            <div class="job-badges">
              <el-tag v-if="job.auditStatus !== 1" type="warning" effect="dark" class="status-badge">
                待审核
              </el-tag>
              <el-tag v-else-if="job.status === 1" type="success" effect="dark" class="status-badge">
                招聘中
              </el-tag>
              <el-tag v-else type="info" effect="dark" class="status-badge">
                已下架
              </el-tag>
            </div>
          </div>
          
          <div class="job-meta">
            <div class="meta-item">
              <el-icon><Location /></el-icon>
              <span>{{ job.location }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>{{ job.experienceName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Reading /></el-icon>
              <span>{{ job.education }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Briefcase /></el-icon>
              <span>{{ job.category }}</span>
            </div>
          </div>

          <div class="job-stats">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <span class="stat-value">{{ job.viewCount }}</span>
              <span class="stat-label">次浏览</span>
            </div>
            <div class="stat-item">
              <el-icon><Document /></el-icon>
              <span class="stat-value">{{ job.applicationCount }}</span>
              <span class="stat-label">人投递</span>
            </div>
          </div>
        </div>

        <div class="job-actions">
          <el-button 
            type="primary" 
            size="large" 
            @click="handleApply"
            :disabled="hasApplied"
            class="apply-button"
          >
            <el-icon><Position /></el-icon>
            {{ hasApplied ? '已投递' : '立即投递简历' }}
          </el-button>
          <el-button 
            size="large" 
            @click="handleFavorite"
            :type="isFavorited ? 'warning' : 'default'"
            class="favorite-button"
          >
            <el-icon><Star /></el-icon>
            {{ isFavorited ? '已收藏' : '收藏职位' }}
          </el-button>
        </div>
      </div>

      <div class="content-grid">
        <div class="main-content">
          <div class="section-card description-section">
            <div class="section-header">
              <h2 class="section-title">
                <el-icon><Document /></el-icon>
                职位描述
              </h2>
            </div>
            <div class="section-content">
              <pre>{{ job.description }}</pre>
            </div>
          </div>

          <div class="section-card requirement-section">
            <div class="section-header">
              <h2 class="section-title">
                <el-icon><List /></el-icon>
                任职要求
              </h2>
            </div>
            <div class="section-content">
              <pre>{{ job.requirement }}</pre>
            </div>
          </div>
        </div>

        <div class="side-content">
          <div class="company-card">
            <div class="company-header">
              <el-avatar :size="64" :src="job.companyLogo || defaultLogo" class="company-avatar">
                {{ job.companyName?.charAt(0) }}
              </el-avatar>
              <div class="company-info">
                <h3 class="company-name">{{ job.companyName }}</h3>
                <div class="company-industry">{{ job.companyIndustry }}</div>
              </div>
            </div>
            <div class="company-details">
              <div class="detail-item">
                <el-icon><OfficeBuilding /></el-icon>
                <span>{{ job.companyScale || '暂无规模信息' }}</span>
              </div>
              <div class="detail-item">
                <el-icon><Location /></el-icon>
                <span>{{ job.location }}</span>
              </div>
            </div>
          </div>

          <div class="tips-card">
            <h4 class="tips-title">
              <el-icon><InfoFilled /></el-icon>
              投递提示
            </h4>
            <ul class="tips-list">
              <li>完善简历可提高面试机会</li>
              <li>投递后可在"我的投递"查看进度</li>
              <li>保持联系方式畅通</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else-if="!loading" description="职位不存在或已下线" />

    <el-dialog v-model="applyDialogVisible" title="投递简历" width="500px" class="apply-dialog">
      <el-form :model="applyForm" label-width="80px" class="apply-form">
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
import { View, Document, Position, Star, OfficeBuilding, Location, Clock, Reading, Briefcase, List, InfoFilled } from '@element-plus/icons-vue'
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
  max-width: 1200px;
  margin: 0 auto;

  .detail-container {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  .job-hero {
    background: white;
    border-radius: var(--radius-2xl);
    padding: 32px;
    box-shadow: var(--shadow-md);
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 32px;
    animation: slideUp var(--transition-slow) ease-out;

    .job-main-info {
      flex: 1;

      .job-header {
        margin-bottom: 20px;

        .title-section {
          display: flex;
          align-items: flex-start;
          gap: 20px;
          margin-bottom: 12px;

          .job-title {
            font-size: 28px;
            font-weight: var(--font-weight-bold);
            color: var(--text-primary);
            margin: 0;
            line-height: 1.3;
          }

          .salary {
            font-size: 28px;
            font-weight: var(--font-weight-bold);
            color: var(--accent-600);
            white-space: nowrap;
          }
        }

        .job-badges {
          display: flex;
          gap: 8px;

          .status-badge {
            font-weight: var(--font-weight-medium);
          }
        }
      }

      .job-meta {
        display: flex;
        flex-wrap: wrap;
        gap: 24px;
        margin-bottom: 24px;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 8px;
          color: var(--text-secondary);
          font-size: var(--font-size-base);

          .el-icon {
            font-size: 18px;
            color: var(--primary-500);
          }
        }
      }

      .job-stats {
        display: flex;
        gap: 32px;
        padding: 16px 24px;
        background: var(--gray-50);
        border-radius: var(--radius-xl);

        .stat-item {
          display: flex;
          align-items: center;
          gap: 8px;

          .el-icon {
            font-size: 20px;
            color: var(--text-tertiary);
          }

          .stat-value {
            font-size: var(--font-size-xl);
            font-weight: var(--font-weight-bold);
            color: var(--primary-600);
          }

          .stat-label {
            font-size: var(--font-size-sm);
            color: var(--text-tertiary);
          }
        }
      }
    }

    .job-actions {
      display: flex;
      flex-direction: column;
      gap: 12px;
      min-width: 200px;

      .apply-button,
      .favorite-button {
        height: 48px;
        border-radius: var(--radius-xl);
        font-weight: var(--font-weight-semibold);
        font-size: var(--font-size-base);
        
        .el-icon {
          font-size: 18px;
        }
      }

      .apply-button {
        background: linear-gradient(135deg, var(--primary-600), var(--primary-500));
        border: none;
        
        &:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
        }
      }
    }
  }

  .content-grid {
    display: grid;
    grid-template-columns: 1fr 360px;
    gap: 24px;

    .main-content {
      display: flex;
      flex-direction: column;
      gap: 24px;

      .section-card {
        background: white;
        border-radius: var(--radius-2xl);
        box-shadow: var(--shadow-sm);
        overflow: hidden;
        animation: slideUp var(--transition-slow) ease-out;
        animation-fill-mode: backwards;

        &.description-section {
          animation-delay: 100ms;
        }

        &.requirement-section {
          animation-delay: 200ms;
        }

        .section-header {
          padding: 20px 24px;
          border-bottom: 1px solid var(--gray-100);

          .section-title {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: var(--font-size-lg);
            font-weight: var(--font-weight-semibold);
            color: var(--text-primary);
            margin: 0;

            .el-icon {
              font-size: 20px;
              color: var(--primary-500);
            }
          }
        }

        .section-content {
          padding: 24px;

          pre {
            white-space: pre-wrap;
            word-wrap: break-word;
            font-family: inherit;
            font-size: var(--font-size-base);
            line-height: 1.8;
            color: var(--text-secondary);
            margin: 0;
          }
        }
      }
    }

    .side-content {
      display: flex;
      flex-direction: column;
      gap: 24px;

      .company-card {
        background: white;
        border-radius: var(--radius-2xl);
        padding: 24px;
        box-shadow: var(--shadow-sm);
        animation: slideUp var(--transition-slow) ease-out 150ms backwards;

        .company-header {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 20px;
          padding-bottom: 20px;
          border-bottom: 1px solid var(--gray-100);

          .company-avatar {
            background: linear-gradient(135deg, var(--primary-100), var(--primary-200));
            color: var(--primary-700);
            font-weight: var(--font-weight-bold);
            font-size: var(--font-size-xl);
          }

          .company-info {
            flex: 1;

            .company-name {
              font-size: var(--font-size-lg);
              font-weight: var(--font-weight-semibold);
              color: var(--text-primary);
              margin: 0 0 4px 0;
            }

            .company-industry {
              font-size: var(--font-size-sm);
              color: var(--text-tertiary);
            }
          }
        }

        .company-details {
          display: flex;
          flex-direction: column;
          gap: 12px;

          .detail-item {
            display: flex;
            align-items: center;
            gap: 10px;
            color: var(--text-secondary);
            font-size: var(--font-size-sm);

            .el-icon {
              font-size: 16px;
              color: var(--primary-500);
            }
          }
        }
      }

      .tips-card {
        background: linear-gradient(135deg, var(--primary-50), var(--primary-100));
        border-radius: var(--radius-2xl);
        padding: 20px;
        animation: slideUp var(--transition-slow) ease-out 250ms backwards;

        .tips-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: var(--font-size-base);
          font-weight: var(--font-weight-semibold);
          color: var(--primary-700);
          margin: 0 0 12px 0;

          .el-icon {
            font-size: 18px;
          }
        }

        .tips-list {
          margin: 0;
          padding: 0;
          list-style: none;

          li {
            position: relative;
            padding-left: 16px;
            margin-bottom: 8px;
            font-size: var(--font-size-sm);
            color: var(--text-secondary);
            line-height: 1.6;

            &::before {
              content: '';
              position: absolute;
              left: 0;
              top: 8px;
              width: 6px;
              height: 6px;
              border-radius: 50%;
              background: var(--primary-400);
            }

            &:last-child {
              margin-bottom: 0;
            }
          }
        }
      }
    }
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1024px) {
  .job-detail-page {
    .job-hero {
      flex-direction: column;
      align-items: stretch;

      .job-actions {
        flex-direction: row;
        min-width: auto;

        .apply-button,
        .favorite-button {
          flex: 1;
        }
      }
    }

    .content-grid {
      grid-template-columns: 1fr;

      .side-content {
        order: -1;
      }
    }
  }
}
</style>
