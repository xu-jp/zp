<template>
  <div class="recommend-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><MagicStick /></el-icon>
            AI智能推荐
          </span>
          <el-button type="primary" @click="refreshRecommend" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新推荐
          </el-button>
        </div>
      </template>

      <!-- 空状态 -->
      <div class="empty-state" v-if="!loading && !hasLoaded">
        <div class="empty-illustration">
          <div class="illustration-circle">
            <div class="circle-inner"></div>
          </div>
          <div class="illustration-icon">
            <el-icon><MagicStick /></el-icon>
          </div>
        </div>
        <div class="empty-title">AI智能职位推荐</div>
        <div class="empty-subtitle">点击下方按钮，AI将为您智能匹配最佳职位</div>
        <div class="recommend-principles">
          <div class="principles-title">
            <el-icon><Trophy /></el-icon>
            推荐原则
          </div>
          <div class="principles-list">
            <div class="principle-item">
              <el-icon><Check /></el-icon>
              <div class="principle-content">
                <span>职位匹配度分析</span>
                <span class="detail">根据您的技能、经验与职位要求进行智能匹配</span>
              </div>
            </div>
            <div class="principle-item">
              <el-icon><Check /></el-icon>
              <div class="principle-content">
                <span>职业发展评估</span>
                <span class="detail">综合考虑您的职业路径和发展潜力</span>
              </div>
            </div>
            <div class="principle-item">
              <el-icon><Check /></el-icon>
              <div class="principle-content">
                <span>公司匹配度</span>
                <span class="detail">分析公司文化、规模与您的契合度</span>
              </div>
            </div>
            <div class="principle-item">
              <el-icon><Check /></el-icon>
              <div class="principle-content">
                <span>薪资竞争力</span>
                <span class="detail">确保推荐职位薪资具有市场竞争力</span>
              </div>
            </div>
          </div>
        </div>
        <el-button type="primary" size="large" @click="refreshRecommend" :loading="loading">
          <el-icon><MagicStick /></el-icon>
          开始智能推荐
        </el-button>
      </div>

      <!-- 等待中状态 -->
      <div class="loading-state" v-if="loading">
        <div class="loading-spinner">
          <div class="spinner-circle"></div>
          <div class="spinner-circle"></div>
          <div class="spinner-circle"></div>
        </div>
        <div class="loading-text">
          <div class="main-text">正在为您智能匹配职位...</div>
          <div class="sub-text">AI正在分析您的简历，预计需要10-30秒</div>
        </div>
      </div>

      <!-- 推荐结果 -->
      <div v-else>
        <div class="job-list">
          <div 
            v-for="job in recommendJobs" 
            :key="job.id" 
            class="job-item"
            @click="goDetail(job.id)"
          >
            <div class="job-main">
              <div class="job-header">
                <span class="job-title">{{ job.title }}</span>
                <span class="salary">{{ formatSalary(job.salaryMin, job.salaryMax) }}</span>
              </div>
              <div class="company-info">
                <el-avatar :size="32" :src="job.companyLogo">
                  {{ job.companyName?.charAt(0) }}
                </el-avatar>
                <span class="company-name">{{ job.companyName }}</span>
                <el-tag size="small" type="info">{{ job.companyIndustry }}</el-tag>
              </div>
              <div class="job-tags">
                <el-tag size="small" type="info">{{ job.location }}</el-tag>
                <el-tag size="small">{{ getExperienceText(job.experience) }}</el-tag>
                <el-tag size="small" type="warning">{{ job.education }}</el-tag>
                <el-tag size="small" type="success">{{ job.category }}</el-tag>
              </div>
              <div class="match-score" v-if="job.matchScore">
                <el-progress 
                  :percentage="Math.round(job.matchScore)" 
                  :color="getScoreColor(job.matchScore)"
                  :stroke-width="16"
                  show-text
                />
              </div>
            </div>
            <div class="job-stats">
              <span><el-icon><View /></el-icon> {{ job.viewCount }}</span>
              <span><el-icon><Document /></el-icon> {{ job.applicationCount }}</span>
            </div>
          </div>

          <el-empty v-if="recommendJobs.length === 0" description="暂无推荐职位，请先完善简历" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRecommendJobs } from '@/api/recommend'
import { MagicStick, Refresh, View, Document, Trophy, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRecommendStore } from '@/stores'

const router = useRouter()
const recommendStore = useRecommendStore()
const loading = ref(false)
const recommendJobs = ref([])
const hasLoaded = ref(false)

onMounted(() => {
  if (recommendStore.hasCache) {
    recommendJobs.value = recommendStore.recommendJobs
    hasLoaded.value = true
  }
})

const refreshRecommend = () => {
  hasLoaded.value = true
  fetchRecommendJobs()
}

const fetchRecommendJobs = async () => {
  loading.value = true
  try {
    const res = await getRecommendJobs(5)
    recommendJobs.value = res.data || []
    recommendStore.setRecommendJobs(recommendJobs.value)
  } catch (error) {
    console.error('获取推荐职位失败:', error)
    if (error.code === 'ECONNABORTED') {
      ElMessage.warning('推荐请求超时，请稍后重试')
    } else {
      ElMessage.error('推荐服务暂时不可用，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

const goDetail = (jobId) => {
  router.push(`/jobs/${jobId}`)
}

const formatSalary = (min, max) => {
  if (!min && !max) return '面议'
  if (!min) return max + 'K以下'
  if (!max) return min + 'K以上'
  return min + 'K-' + max + 'K'
}

const getExperienceText = (exp) => {
  const map = {
    0: '经验不限',
    1: '1年以下',
    2: '1-3年',
    3: '3-5年',
    4: '5-10年',
    5: '10年以上'
  }
  return map[exp] || '经验不限'
}

const getScoreColor = (score) => {
  if (score >= 80) return '#67c23a'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}
</script>

<style lang="scss" scoped>
.recommend-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    span {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 600;
    }
  }

  .recommend-tip {
    margin-bottom: 20px;
  }

  // 等待中状态样式
  .loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    min-height: 400px;

    .loading-spinner {
      display: flex;
      gap: 12px;
      margin-bottom: 30px;

      .spinner-circle {
        width: 20px;
        height: 20px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        animation: bounce 1.4s infinite ease-in-out both;
      }

      .spinner-circle:nth-child(1) {
        animation-delay: -0.32s;
      }

      .spinner-circle:nth-child(2) {
        animation-delay: -0.16s;
      }
    }

    .loading-text {
      text-align: center;
      margin-bottom: 30px;

      .main-text {
        font-size: 18px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
      }

      .sub-text {
        font-size: 14px;
        color: #999;
      }
    }

    .recommend-principles {
      width: 100%;
      max-width: 600px;
      background: #f8f9fa;
      border-radius: 12px;
      padding: 24px;
      border: 1px solid #e9ecef;

      .principles-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 2px solid #e9ecef;

        .el-icon {
          color: #667eea;
        }
      }

      .principles-list {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .principle-item {
          display: flex;
          align-items: flex-start;
          gap: 12px;
          padding: 12px 16px;
          background: #fff;
          border-radius: 8px;
          border: 1px solid #e9ecef;
          transition: all 0.3s;

          &:hover {
            border-color: #667eea;
            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
          }

          .el-icon {
            color: #67c23a;
            margin-top: 2px;
            flex-shrink: 0;
          }

          span {
            font-size: 14px;
            color: #555;
          }

          .detail {
            color: #999;
            font-size: 13px;
            margin-top: 4px;
            display: block;
            width: 100%;
          }
        }
      }
    }
  }

  @keyframes bounce {
    0%, 80%, 100% {
      transform: scale(0);
    }
    40% {
      transform: scale(1);
    }
  }

  // 职位列表样式
  .job-list {
    .job-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #eee;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: #f9fafc;
        transform: translateX(4px);
      }

      &:last-child {
        border-bottom: none;
      }

      .job-main {
        flex: 1;

        .job-header {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 12px;

          .job-title {
            font-size: 18px;
            font-weight: 600;
            color: #333;
          }

          .salary {
            font-size: 18px;
            color: #ff6b00;
            font-weight: 600;
          }
        }

        .company-info {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;

          .company-name {
            color: #666;
            font-size: 14px;
          }
        }

        .job-tags {
          display: flex;
          gap: 8px;
          margin-bottom: 12px;
        }

        .match-score {
          margin-top: 12px;
        }
      }

      .job-stats {
        display: flex;
        flex-direction: column;
        gap: 8px;
        color: #999;
        font-size: 13px;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }

  // 空状态样式
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 60px 20px;
    min-height: 500px;

    .empty-illustration {
      position: relative;
      width: 120px;
      height: 120px;
      margin-bottom: 30px;

      .illustration-circle {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        animation: pulse 2s infinite;

        .circle-inner {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 80px;
          height: 80px;
          background: #fff;
          border-radius: 50%;
        }
      }

      .illustration-icon {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 60px;
        height: 60px;
        background: #fff;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);

        .el-icon {
          font-size: 32px;
          color: #667eea;
        }
      }
    }

    .empty-title {
      font-size: 24px;
      font-weight: 600;
      color: #333;
      margin-bottom: 12px;
    }

    .empty-subtitle {
      font-size: 14px;
      color: #999;
      margin-bottom: 30px;
    }

    .recommend-principles {
      width: 100%;
      max-width: 600px;
      background: #f8f9fa;
      border-radius: 12px;
      padding: 24px;
      margin-bottom: 30px;
      border: 1px solid #e9ecef;

      .principles-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin-bottom: 16px;
        padding-bottom: 12px;
        border-bottom: 2px solid #e9ecef;

        .el-icon {
          color: #667eea;
        }
      }

      .principles-list {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .principle-item {
          display: flex;
          align-items: flex-start;
          gap: 12px;
          padding: 12px 16px;
          background: #fff;
          border-radius: 8px;
          border: 1px solid #e9ecef;
          transition: all 0.3s;

          &:hover {
            border-color: #667eea;
            box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
            transform: translateX(4px);
          }

          .el-icon {
            color: #67c23a;
            margin-top: 2px;
            flex-shrink: 0;
          }

          .principle-content {
            flex: 1;

            span {
              font-size: 14px;
              color: #555;
              display: block;
            }

            .detail {
              color: #999;
              font-size: 13px;
              margin-top: 4px;
              display: block;
            }
          }
        }
      }
    }
  }

  @keyframes pulse {
    0%, 100% {
      transform: scale(1);
      opacity: 1;
    }
    50% {
      transform: scale(1.1);
      opacity: 0.8;
    }
  }
}
</style>
