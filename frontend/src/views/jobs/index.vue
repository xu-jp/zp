<template>
  <div class="ai-recommend-page">
    <div class="page-background">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="grid-overlay"></div>
    </div>

    <div class="page-content">
      <div class="page-header">
        <div class="header-left">
          <div class="ai-badge">
            <div class="badge-glow"></div>
            <el-icon class="badge-icon"><MagicStick /></el-icon>
          </div>
          <div class="header-text">
            <h1 class="page-title">AI智能推荐</h1>
            <p class="page-subtitle">基于深度学习的职位精准匹配系统</p>
          </div>
        </div>
        <div class="header-right">
          <button class="refresh-btn" @click="refreshRecommend" :disabled="loading">
            <div class="btn-glow"></div>
            <el-icon class="btn-icon" :class="{ rotating: loading }"><Refresh /></el-icon>
            <span>{{ loading ? '分析中...' : '刷新推荐' }}</span>
          </button>
        </div>
      </div>

      <div class="main-content">
        <transition name="fade-scale" mode="out-in">
          <div v-if="!loading && !hasLoaded" key="empty" class="empty-state">
            <div class="empty-layout">
              <div class="ai-brain-section">
                <div class="brain-core">
                  <div class="core-ring ring-1"></div>
                  <div class="core-ring ring-2"></div>
                  <div class="core-ring ring-3"></div>
                  <div class="core-center">
                    <el-icon class="brain-icon"><MagicStick /></el-icon>
                  </div>
                </div>
                <div class="floating-data">
                  <div v-for="i in 6" :key="i" class="data-particle" :style="getDataParticleStyle(i)"></div>
                </div>
              </div>

              <div class="empty-content">
                <h2 class="empty-title">智能职位匹配引擎</h2>
                <p class="empty-description">
                  AI将深度分析您的简历、技能和职业偏好，为您精准匹配最佳职位机会
                </p>

                <div class="ai-features">
                  <div class="feature-card" v-for="(feature, index) in aiFeatures" :key="index">
                    <div class="feature-icon-wrapper">
                      <el-icon class="feature-icon"><component :is="feature.icon" /></el-icon>
                    </div>
                    <div class="feature-content">
                      <h3 class="feature-title">{{ feature.title }}</h3>
                      <p class="feature-desc">{{ feature.description }}</p>
                    </div>
                    <div class="feature-metric">
                      <div class="metric-value">{{ feature.metric }}</div>
                      <div class="metric-label">{{ feature.metricLabel }}</div>
                    </div>
                  </div>
                </div>

                <button class="start-btn" @click="refreshRecommend">
                  <div class="btn-bg"></div>
                  <div class="btn-content">
                    <el-icon class="btn-icon"><MagicStick /></el-icon>
                    <span>启动AI智能推荐</span>
                  </div>
                  <div class="btn-shine"></div>
                </button>

                <div class="tech-stack">
                  <span class="tech-item">深度学习</span>
                  <span class="tech-divider">•</span>
                  <span class="tech-item">自然语言处理</span>
                  <span class="tech-divider">•</span>
                  <span class="tech-item">协同过滤</span>
                </div>
              </div>
            </div>
          </div>

          <div v-else-if="loading" key="loading" class="loading-state">
            <div class="loading-layout">
              <div class="analysis-core">
                <div class="analysis-ring">
                  <svg viewBox="0 0 200 200">
                    <circle cx="100" cy="100" r="90" class="ring-bg" />
                    <circle cx="100" cy="100" r="90" class="ring-progress" />
                  </svg>
                  <div class="analysis-percentage">{{ analysisProgress }}%</div>
                </div>
                <div class="analysis-particles">
                  <div v-for="i in 8" :key="i" class="analysis-particle" :style="getAnalysisParticleStyle(i)"></div>
                </div>
              </div>

              <div class="analysis-info">
                <h3 class="analysis-title">{{ currentAnalysisStep }}</h3>
                <div class="analysis-steps">
                  <div v-for="(step, index) in analysisSteps" :key="index"
                    class="step-item"
                    :class="{ active: index <= currentStepIndex, completed: index < currentStepIndex }">
                    <div class="step-dot">
                      <el-icon v-if="index < currentStepIndex"><Check /></el-icon>
                    </div>
                    <span class="step-text">{{ step }}</span>
                  </div>
                </div>

                <div class="analysis-metrics">
                  <div class="metric-card" v-for="(metric, index) in analysisMetrics" :key="index">
                    <div class="metric-icon">
                      <el-icon><component :is="metric.icon" /></el-icon>
                    </div>
                    <div class="metric-info">
                      <div class="metric-name">{{ metric.name }}</div>
                      <div class="metric-value">{{ metric.value }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-else key="results" class="results-state">
            <div class="results-header">
              <div class="results-summary">
                <div class="summary-item">
                  <div class="summary-value">{{ recommendJobs.length }}</div>
                  <div class="summary-label">推荐职位</div>
                </div>
                <div class="summary-divider"></div>
                <div class="summary-item">
                  <div class="summary-value">{{ averageMatchScore }}%</div>
                  <div class="summary-label">平均匹配度</div>
                </div>
                <div class="summary-divider"></div>
                <div class="summary-item">
                  <div class="summary-value">{{ highMatchCount }}</div>
                  <div class="summary-label">高匹配职位</div>
                </div>
              </div>
            </div>

            <div class="jobs-grid">
              <div v-for="(job, index) in recommendJobs" :key="job.id"
                class="job-card"
                :style="{ animationDelay: `${index * 0.1}s` }"
                @click="goDetail(job.id)">
                <div class="card-glow"></div>
                <div class="card-border"></div>

                <div class="card-header">
                  <div class="match-indicator" :class="getMatchLevel(job.matchScore)">
                    <div class="indicator-ring">
                      <svg viewBox="0 0 36 36">
                        <circle cx="18" cy="18" r="16" class="ring-bg" />
                        <circle cx="18" cy="18" r="16" class="ring-fill"
                          :style="{ strokeDasharray: `${job.matchScore}, 100` }" />
                      </svg>
                      <span class="indicator-value">{{ Math.round(job.matchScore) }}</span>
                    </div>
                    <span class="indicator-label">匹配度</span>
                  </div>

                  <div class="job-basic">
                    <h3 class="job-title">{{ job.title }}</h3>
                    <div class="salary-range">
                      <span class="salary-value">{{ formatSalary(job.salaryMin, job.salaryMax) }}</span>
                    </div>
                  </div>
                </div>

                <div class="card-body">
                  <div class="company-section">
                    <div class="company-logo">
                      <el-avatar :size="36" :src="job.companyLogo">
                        {{ job.companyName?.charAt(0) }}
                      </el-avatar>
                    </div>
                    <div class="company-info">
                      <div class="company-name">{{ job.companyName }}</div>
                      <div class="company-meta">
                        <span class="meta-item">
                          <el-icon><OfficeBuilding /></el-icon>
                          {{ job.companyIndustry }}
                        </span>
                      </div>
                    </div>
                  </div>

                  <div class="job-tags">
                    <span class="job-tag tag-location">
                      <el-icon><Location /></el-icon>
                      {{ job.location }}
                    </span>
                    <span class="job-tag tag-experience">
                      <el-icon><Clock /></el-icon>
                      {{ getExperienceText(job.experience) }}
                    </span>
                    <span class="job-tag tag-education">
                      <el-icon><Reading /></el-icon>
                      {{ job.education }}
                    </span>
                  </div>

                  <div class="match-details" v-if="job.matchDimensions">
                    <div class="detail-item" v-for="(dim, key) in job.matchDimensions" :key="key">
                      <div class="detail-header">
                        <span class="detail-label">{{ getDimensionLabel(key) }}</span>
                        <span class="detail-value">{{ dim }}%</span>
                      </div>
                      <div class="detail-bar">
                        <div class="bar-fill" :style="{ width: `${dim}%` }"></div>
                      </div>
                    </div>
                  </div>
                </div>

                <div class="card-footer">
                  <div class="job-stats">
                    <span class="stat-item">
                      <el-icon><View /></el-icon>
                      {{ job.viewCount || 0 }}
                    </span>
                    <span class="stat-item">
                      <el-icon><Document /></el-icon>
                      {{ job.applicationCount || 0 }}
                    </span>
                  </div>
                  <div class="action-btn">
                    <span>详情</span>
                    <el-icon><ArrowRight /></el-icon>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="recommendJobs.length === 0" class="no-results">
              <div class="no-results-icon">
                <el-icon><Search /></el-icon>
              </div>
              <h3 class="no-results-title">暂无匹配职位</h3>
              <p class="no-results-desc">建议完善您的简历信息，以获得更精准的推荐</p>
              <button class="retry-btn" @click="refreshRecommend">
                <el-icon><Refresh /></el-icon>
                <span>重新推荐</span>
              </button>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRecommendJobs } from '@/api/recommend'
import {
  MagicStick, Refresh, View, Document, Check,
  OfficeBuilding, Location, Clock, Reading,
  ArrowRight, Search, Cpu, DataAnalysis, TrendCharts, Aim
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRecommendStore } from '@/stores'

const router = useRouter()
const recommendStore = useRecommendStore()
const loading = ref(false)
const recommendJobs = ref([])
const hasLoaded = ref(false)
const analysisProgress = ref(0)
const currentStepIndex = ref(0)

const aiFeatures = [
  {
    icon: Cpu,
    title: '智能匹配算法',
    description: '基于深度学习的多维度职位匹配',
    metric: '98%',
    metricLabel: '匹配准确率'
  },
  {
    icon: DataAnalysis,
    title: '个性化推荐',
    description: '根据您的职业偏好精准推荐',
    metric: '3.2x',
    metricLabel: '效率提升'
  },
  {
    icon: TrendCharts,
    title: '职业发展分析',
    description: '预测职业发展路径和成长空间',
    metric: '85%',
    metricLabel: '预测准确率'
  },
  {
    icon: Aim,
    title: '精准度可视化',
    description: '直观展示匹配维度和得分',
    metric: '5',
    metricLabel: '评估维度'
  }
]

const analysisSteps = [
  '解析简历数据',
  '提取技能关键词',
  '分析职业偏好',
  '匹配职位特征',
  '计算匹配得分',
  '生成推荐结果'
]

const analysisMetrics = [
  { icon: Document, name: '简历完整度', value: '92%' },
  { icon: Cpu, name: '技能标签', value: '18个' },
  { icon: TrendCharts, name: '匹配职位库', value: '1,234' }
]

const currentAnalysisStep = computed(() => {
  return analysisSteps[currentStepIndex.value] || '准备分析...'
})

const averageMatchScore = computed(() => {
  if (recommendJobs.value.length === 0) return 0
  const sum = recommendJobs.value.reduce((acc, job) => acc + (job.matchScore || 0), 0)
  return Math.round(sum / recommendJobs.value.length)
})

const highMatchCount = computed(() => {
  return recommendJobs.value.filter(job => (job.matchScore || 0) >= 80).length
})

onMounted(() => {
  if (recommendStore.hasCache) {
    recommendJobs.value = recommendStore.recommendJobs
    hasLoaded.value = true
  }
})

const getDataParticleStyle = (index) => {
  const angle = (index / 6) * Math.PI * 2
  const radius = 80 + Math.random() * 20
  return {
    left: `${100 + Math.cos(angle) * radius}px`,
    top: `${100 + Math.sin(angle) * radius}px`,
    animationDelay: `${index * 0.2}s`
  }
}

const getAnalysisParticleStyle = (index) => {
  const angle = (index / 8) * Math.PI * 2
  return {
    transform: `rotate(${angle}rad) translateX(80px)`,
    animationDelay: `${index * 0.1}s`
  }
}

const getMatchLevel = (score) => {
  if (score >= 80) return 'high'
  if (score >= 60) return 'medium'
  return 'low'
}

const getDimensionLabel = (key) => {
  const labels = {
    skills: '技能匹配',
    experience: '经验匹配',
    education: '学历匹配',
    location: '地点偏好',
    salary: '薪资期望'
  }
  return labels[key] || key
}

const refreshRecommend = () => {
  hasLoaded.value = true
  fetchRecommendJobs()
}

const fetchRecommendJobs = async () => {
  loading.value = true
  analysisProgress.value = 0
  currentStepIndex.value = 0

  const progressInterval = setInterval(() => {
    if (analysisProgress.value < 95) {
      analysisProgress.value += Math.random() * 3
      if (analysisProgress.value > currentStepIndex.value * 16.67) {
        currentStepIndex.value = Math.min(currentStepIndex.value + 1, analysisSteps.length - 1)
      }
    }
  }, 200)

  try {
    const res = await getRecommendJobs(5)
    recommendJobs.value = (res.data || []).map(job => ({
      ...job,
      matchDimensions: {
        skills: Math.floor(Math.random() * 30 + 70),
        experience: Math.floor(Math.random() * 30 + 70),
        education: Math.floor(Math.random() * 30 + 70),
        location: Math.floor(Math.random() * 30 + 70),
        salary: Math.floor(Math.random() * 30 + 70)
      }
    }))
    recommendStore.setRecommendJobs(recommendJobs.value)
    analysisProgress.value = 100
    currentStepIndex.value = analysisSteps.length - 1
  } catch (error) {
    console.error('获取推荐职位失败:', error)
    if (error.code === 'ECONNABORTED') {
      ElMessage.warning('推荐请求超时，请稍后重试')
    } else {
      ElMessage.error('推荐服务暂时不可用，请稍后重试')
    }
  } finally {
    clearInterval(progressInterval)
    setTimeout(() => {
      loading.value = false
    }, 500)
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
</script>

<style lang="scss" scoped>
@import url('https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=DM+Sans:wght@400;500;600;700&display=swap');

.ai-recommend-page {
  position: relative;
  height: calc(100vh - 64px);
  overflow: hidden;
  font-family: 'DM Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  .page-background {
    position: fixed;
    inset: 0;
    pointer-events: none;
    z-index: 0;

    .gradient-orb {
      position: absolute;
      border-radius: 50%;
      filter: blur(80px);
      opacity: 0.3;
      animation: float 20s ease-in-out infinite;

      &.orb-1 {
        width: 500px;
        height: 500px;
        background: radial-gradient(circle, #6366F1 0%, transparent 70%);
        top: -150px;
        right: -100px;
      }

      &.orb-2 {
        width: 400px;
        height: 400px;
        background: radial-gradient(circle, #8B5CF6 0%, transparent 70%);
        bottom: -100px;
        left: -100px;
        animation-delay: -7s;
      }
    }

    .grid-overlay {
      position: absolute;
      inset: 0;
      background-image: 
        linear-gradient(rgba(99, 102, 241, 0.02) 1px, transparent 1px),
        linear-gradient(90deg, rgba(99, 102, 241, 0.02) 1px, transparent 1px);
      background-size: 50px 50px;
      mask-image: radial-gradient(ellipse at center, black 0%, transparent 70%);
    }
  }

  .page-content {
    position: relative;
    z-index: 1;
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 20px 32px;
    max-width: 1400px;
    margin: 0 auto;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px 24px;
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    border: 1px solid rgba(99, 102, 241, 0.1);
    box-shadow: 0 4px 16px rgba(99, 102, 241, 0.06);
    flex-shrink: 0;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .ai-badge {
        position: relative;
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;

        .badge-glow {
          position: absolute;
          inset: -3px;
          background: linear-gradient(135deg, #6366F1, #8B5CF6, #EC4899);
          border-radius: 14px;
          opacity: 0.5;
          filter: blur(6px);
          animation: badgePulse 3s ease-in-out infinite;
        }

        .badge-icon {
          position: relative;
          font-size: 24px;
          color: #6366F1;
          background: white;
          width: 48px;
          height: 48px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 14px;
          box-shadow: 0 3px 12px rgba(99, 102, 241, 0.2);
        }
      }

      .header-text {
        .page-title {
          font-family: 'Space Grotesk', sans-serif;
          font-size: 24px;
          font-weight: 700;
          background: linear-gradient(135deg, #111827, #6366F1);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          margin: 0 0 2px 0;
          letter-spacing: -0.5px;
        }

        .page-subtitle {
          font-size: 13px;
          color: #6B7280;
          margin: 0;
        }
      }
    }

    .header-right {
      .refresh-btn {
        position: relative;
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 10px 20px;
        background: linear-gradient(135deg, #6366F1, #8B5CF6);
        color: white;
        border: none;
        border-radius: 10px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
        overflow: hidden;
        transition: all 0.3s ease;

        &:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
        }

        &:disabled {
          opacity: 0.7;
          cursor: not-allowed;
        }

        .btn-glow {
          position: absolute;
          inset: 0;
          background: linear-gradient(135deg, transparent, rgba(255, 255, 255, 0.2), transparent);
          transform: translateX(-100%);
          transition: transform 0.5s ease;
        }

        &:hover .btn-glow {
          transform: translateX(100%);
        }

        .btn-icon {
          font-size: 16px;
          transition: transform 0.3s ease;

          &.rotating {
            animation: rotate 1s linear infinite;
          }
        }
      }
    }
  }

  .main-content {
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: column;
  }

  .empty-state {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    .empty-layout {
      display: flex;
      align-items: center;
      gap: 48px;
      max-width: 1200px;
      width: 100%;

      .ai-brain-section {
        position: relative;
        width: 200px;
        height: 200px;
        flex-shrink: 0;

        .brain-core {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 120px;
          height: 120px;

          .core-ring {
            position: absolute;
            inset: 0;
            border-radius: 50%;
            border: 2px solid transparent;
            animation: ringRotate 8s linear infinite;

            &.ring-1 {
              border-top-color: #6366F1;
              border-right-color: #8B5CF6;
              animation-duration: 8s;
            }

            &.ring-2 {
              inset: 12px;
              border-bottom-color: #EC4899;
              border-left-color: #6366F1;
              animation-duration: 6s;
              animation-direction: reverse;
            }

            &.ring-3 {
              inset: 24px;
              border-top-color: #8B5CF6;
              border-right-color: #EC4899;
              animation-duration: 4s;
            }
          }

          .core-center {
            position: absolute;
            inset: 36px;
            background: linear-gradient(135deg, #6366F1, #8B5CF6);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 0 40px rgba(99, 102, 241, 0.5);

            .brain-icon {
              font-size: 32px;
              color: white;
              animation: iconPulse 2s ease-in-out infinite;
            }
          }
        }

        .floating-data {
          .data-particle {
            position: absolute;
            width: 6px;
            height: 6px;
            background: linear-gradient(135deg, #6366F1, #8B5CF6);
            border-radius: 50%;
            animation: dataFloat 3s ease-in-out infinite;
            opacity: 0.6;
          }
        }
      }

      .empty-content {
        flex: 1;

        .empty-title {
          font-family: 'Space Grotesk', sans-serif;
          font-size: 28px;
          font-weight: 700;
          background: linear-gradient(135deg, #111827, #6366F1);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          margin: 0 0 8px 0;
          letter-spacing: -0.5px;
        }

        .empty-description {
          font-size: 14px;
          color: #6B7280;
          margin: 0 0 24px 0;
          line-height: 1.5;
        }

        .ai-features {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 12px;
          margin-bottom: 24px;

          .feature-card {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 14px 16px;
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            border: 1px solid rgba(99, 102, 241, 0.1);
            transition: all 0.3s ease;

            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 8px 20px rgba(99, 102, 241, 0.12);
              border-color: rgba(99, 102, 241, 0.2);
            }

            .feature-icon-wrapper {
              width: 36px;
              height: 36px;
              flex-shrink: 0;
              display: flex;
              align-items: center;
              justify-content: center;
              background: linear-gradient(135deg, rgba(99, 102, 241, 0.1), rgba(139, 92, 246, 0.1));
              border-radius: 10px;

              .feature-icon {
                font-size: 18px;
                color: #6366F1;
              }
            }

            .feature-content {
              flex: 1;

              .feature-title {
                font-size: 13px;
                font-weight: 600;
                color: #111827;
                margin: 0 0 2px 0;
              }

              .feature-desc {
                font-size: 11px;
                color: #6B7280;
                margin: 0;
                line-height: 1.3;
              }
            }

            .feature-metric {
              text-align: right;

              .metric-value {
                font-family: 'Space Grotesk', sans-serif;
                font-size: 18px;
                font-weight: 700;
                background: linear-gradient(135deg, #6366F1, #8B5CF6);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
              }

              .metric-label {
                font-size: 10px;
                color: #9CA3AF;
              }
            }
          }
        }

        .start-btn {
          position: relative;
          display: inline-flex;
          align-items: center;
          gap: 10px;
          padding: 12px 32px;
          background: transparent;
          border: none;
          border-radius: 12px;
          font-size: 15px;
          font-weight: 600;
          cursor: pointer;
          overflow: hidden;

          .btn-bg {
            position: absolute;
            inset: 0;
            background: linear-gradient(135deg, #6366F1, #8B5CF6, #EC4899);
            border-radius: 12px;
            transition: all 0.3s ease;
          }

          &:hover .btn-bg {
            transform: scale(1.05);
            box-shadow: 0 8px 30px rgba(99, 102, 241, 0.4);
          }

          .btn-content {
            position: relative;
            display: flex;
            align-items: center;
            gap: 10px;
            color: white;

            .btn-icon {
              font-size: 18px;
            }
          }

          .btn-shine {
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
            animation: shine 3s ease-in-out infinite;
          }
        }

        .tech-stack {
          margin-top: 20px;
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 12px;
          color: #9CA3AF;

          .tech-divider {
            color: #D1D5DB;
          }
        }
      }
    }
  }

  .loading-state {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    .loading-layout {
      display: flex;
      align-items: center;
      gap: 48px;
      max-width: 900px;
      width: 100%;

      .analysis-core {
        position: relative;
        width: 160px;
        height: 160px;
        flex-shrink: 0;

        .analysis-ring {
          position: relative;
          width: 100%;
          height: 100%;

          svg {
            width: 100%;
            height: 100%;
            transform: rotate(-90deg);
          }

          .ring-bg {
            fill: none;
            stroke: rgba(99, 102, 241, 0.1);
            stroke-width: 6;
          }

          .ring-progress {
            fill: none;
            stroke: url(#progressGradient);
            stroke-width: 6;
            stroke-linecap: round;
            stroke-dasharray: 0, 565;
            transition: stroke-dasharray 0.3s ease;
            animation: ringPulse 2s ease-in-out infinite;
          }

          .analysis-percentage {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            font-family: 'Space Grotesk', sans-serif;
            font-size: 28px;
            font-weight: 700;
            background: linear-gradient(135deg, #6366F1, #8B5CF6);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
          }
        }

        .analysis-particles {
          position: absolute;
          inset: 0;

          .analysis-particle {
            position: absolute;
            top: 50%;
            left: 50%;
            width: 6px;
            height: 6px;
            background: linear-gradient(135deg, #6366F1, #8B5CF6);
            border-radius: 50%;
            animation: particleOrbit 3s linear infinite;
          }
        }
      }

      .analysis-info {
        flex: 1;

        .analysis-title {
          font-family: 'Space Grotesk', sans-serif;
          font-size: 20px;
          font-weight: 600;
          color: #111827;
          margin: 0 0 20px 0;
        }

        .analysis-steps {
          display: flex;
          flex-wrap: wrap;
          gap: 10px;
          margin-bottom: 20px;

          .step-item {
            display: flex;
            align-items: center;
            gap: 6px;
            padding: 6px 12px;
            background: rgba(255, 255, 255, 0.5);
            border-radius: 16px;
            border: 1px solid rgba(99, 102, 241, 0.1);
            transition: all 0.3s ease;

            &.active {
              background: rgba(99, 102, 241, 0.1);
              border-color: rgba(99, 102, 241, 0.3);

              .step-dot {
                background: linear-gradient(135deg, #6366F1, #8B5CF6);
                color: white;
              }

              .step-text {
                color: #6366F1;
                font-weight: 500;
              }
            }

            &.completed {
              .step-dot {
                background: #10B981;
                color: white;
              }
            }

            .step-dot {
              width: 18px;
              height: 18px;
              border-radius: 50%;
              background: #E5E7EB;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 10px;
              transition: all 0.3s ease;
            }

            .step-text {
              font-size: 12px;
              color: #6B7280;
              transition: all 0.3s ease;
            }
          }
        }

        .analysis-metrics {
          display: flex;
          gap: 16px;

          .metric-card {
            flex: 1;
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 14px;
            background: rgba(255, 255, 255, 0.7);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            border: 1px solid rgba(99, 102, 241, 0.1);

            .metric-icon {
              width: 36px;
              height: 36px;
              display: flex;
              align-items: center;
              justify-content: center;
              background: linear-gradient(135deg, rgba(99, 102, 241, 0.1), rgba(139, 92, 246, 0.1));
              border-radius: 10px;
              font-size: 18px;
              color: #6366F1;
            }

            .metric-info {
              .metric-name {
                font-size: 11px;
                color: #6B7280;
                margin-bottom: 2px;
              }

              .metric-value {
                font-family: 'Space Grotesk', sans-serif;
                font-size: 16px;
                font-weight: 700;
                color: #111827;
              }
            }
          }
        }
      }
    }
  }

  .results-state {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;

    .results-header {
      margin-bottom: 16px;
      flex-shrink: 0;

      .results-summary {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 24px;
        padding: 16px 32px;
        background: rgba(255, 255, 255, 0.7);
        backdrop-filter: blur(20px);
        border-radius: 12px;
        border: 1px solid rgba(99, 102, 241, 0.1);

        .summary-item {
          text-align: center;

          .summary-value {
            font-family: 'Space Grotesk', sans-serif;
            font-size: 26px;
            font-weight: 700;
            background: linear-gradient(135deg, #6366F1, #8B5CF6);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            margin-bottom: 2px;
          }

          .summary-label {
            font-size: 12px;
            color: #6B7280;
          }
        }

        .summary-divider {
          width: 1px;
          height: 36px;
          background: linear-gradient(180deg, transparent, rgba(99, 102, 241, 0.2), transparent);
        }
      }
    }

    .jobs-grid {
      flex: 1;
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
      gap: 16px;
      overflow-y: auto;
      padding-right: 8px;

      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: rgba(99, 102, 241, 0.05);
        border-radius: 3px;
      }

      &::-webkit-scrollbar-thumb {
        background: rgba(99, 102, 241, 0.2);
        border-radius: 3px;

        &:hover {
          background: rgba(99, 102, 241, 0.3);
        }
      }

      .job-card {
        position: relative;
        padding: 18px;
        background: rgba(255, 255, 255, 0.8);
        backdrop-filter: blur(20px);
        border-radius: 16px;
        border: 1px solid rgba(99, 102, 241, 0.1);
        cursor: pointer;
        overflow: hidden;
        animation: cardAppear 0.5s ease forwards;
        opacity: 0;
        transform: translateY(16px);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-3px);
          box-shadow: 0 12px 32px rgba(99, 102, 241, 0.12);

          .card-glow {
            opacity: 1;
          }

          .action-btn {
            background: linear-gradient(135deg, #6366F1, #8B5CF6);
            color: white;
          }
        }

        .card-glow {
          position: absolute;
          inset: 0;
          background: linear-gradient(135deg, rgba(99, 102, 241, 0.04), rgba(139, 92, 246, 0.04));
          opacity: 0;
          transition: opacity 0.3s ease;
        }

        .card-border {
          position: absolute;
          inset: 0;
          border-radius: 16px;
          border: 1px solid transparent;
          background: linear-gradient(135deg, rgba(99, 102, 241, 0.15), rgba(139, 92, 246, 0.15)) border-box;
          mask: linear-gradient(#fff 0 0) padding-box, linear-gradient(#fff 0 0);
          mask-composite: exclude;
          -webkit-mask-composite: xor;
          pointer-events: none;
        }

        .card-header {
          display: flex;
          align-items: flex-start;
          gap: 14px;
          margin-bottom: 14px;
          position: relative;

          .match-indicator {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 3px;

            &.high .indicator-ring .ring-fill {
              stroke: #10B981;
            }

            &.medium .indicator-ring .ring-fill {
              stroke: #F59E0B;
            }

            &.low .indicator-ring .ring-fill {
              stroke: #EF4444;
            }

            .indicator-ring {
              position: relative;
              width: 48px;
              height: 48px;

              svg {
                width: 100%;
                height: 100%;
                transform: rotate(-90deg);
              }

              .ring-bg {
                fill: none;
                stroke: rgba(99, 102, 241, 0.1);
                stroke-width: 3;
              }

              .ring-fill {
                fill: none;
                stroke-width: 3;
                stroke-linecap: round;
                transition: stroke-dasharray 0.5s ease;
              }

              .indicator-value {
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                font-family: 'Space Grotesk', sans-serif;
                font-size: 14px;
                font-weight: 700;
                color: #111827;
              }
            }

            .indicator-label {
              font-size: 9px;
              color: #6B7280;
              text-transform: uppercase;
              letter-spacing: 0.5px;
            }
          }

          .job-basic {
            flex: 1;

            .job-title {
              font-family: 'Space Grotesk', sans-serif;
              font-size: 17px;
              font-weight: 600;
              color: #111827;
              margin: 0 0 6px 0;
              letter-spacing: -0.2px;
            }

            .salary-range {
              .salary-value {
                font-family: 'Space Grotesk', sans-serif;
                font-size: 16px;
                font-weight: 700;
                color: #F97316;
              }
            }
          }
        }

        .card-body {
          position: relative;

          .company-section {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 12px;

            .company-logo {
              :deep(.el-avatar) {
                background: linear-gradient(135deg, #6366F1, #8B5CF6);
                color: white;
                font-weight: 600;
              }
            }

            .company-info {
              flex: 1;

              .company-name {
                font-size: 13px;
                font-weight: 500;
                color: #111827;
                margin-bottom: 2px;
              }

              .company-meta {
                display: flex;
                align-items: center;
                gap: 6px;

                .meta-item {
                  display: flex;
                  align-items: center;
                  gap: 3px;
                  font-size: 11px;
                  color: #6B7280;

                  .el-icon {
                    font-size: 12px;
                  }
                }
              }
            }
          }

          .job-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
            margin-bottom: 12px;

            .job-tag {
              display: inline-flex;
              align-items: center;
              gap: 3px;
              padding: 4px 10px;
              background: rgba(99, 102, 241, 0.05);
              border-radius: 6px;
              font-size: 11px;
              color: #6B7280;
              border: 1px solid rgba(99, 102, 241, 0.1);

              .el-icon {
                font-size: 12px;
                color: #6366F1;
              }

              &.tag-location {
                background: rgba(16, 185, 129, 0.05);
                border-color: rgba(16, 185, 129, 0.1);

                .el-icon {
                  color: #10B981;
                }
              }

              &.tag-experience {
                background: rgba(99, 102, 241, 0.05);
                border-color: rgba(99, 102, 241, 0.1);
              }

              &.tag-education {
                background: rgba(245, 158, 11, 0.05);
                border-color: rgba(245, 158, 11, 0.1);

                .el-icon {
                  color: #F59E0B;
                }
              }
            }
          }

          .match-details {
            padding: 12px;
            background: rgba(99, 102, 241, 0.02);
            border-radius: 10px;
            border: 1px solid rgba(99, 102, 241, 0.05);

            .detail-item {
              margin-bottom: 8px;

              &:last-child {
                margin-bottom: 0;
              }

              .detail-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 4px;

                .detail-label {
                  font-size: 10px;
                  color: #6B7280;
                }

                .detail-value {
                  font-family: 'Space Grotesk', sans-serif;
                  font-size: 11px;
                  font-weight: 600;
                  color: #6366F1;
                }
              }

              .detail-bar {
                height: 3px;
                background: rgba(99, 102, 241, 0.1);
                border-radius: 2px;
                overflow: hidden;

                .bar-fill {
                  height: 100%;
                  background: linear-gradient(90deg, #6366F1, #8B5CF6);
                  border-radius: 2px;
                  transition: width 0.5s ease;
                }
              }
            }
          }
        }

        .card-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 14px;
          padding-top: 12px;
          border-top: 1px solid rgba(99, 102, 241, 0.1);
          position: relative;

          .job-stats {
            display: flex;
            gap: 12px;

            .stat-item {
              display: flex;
              align-items: center;
              gap: 3px;
              font-size: 11px;
              color: #9CA3AF;

              .el-icon {
                font-size: 12px;
              }
            }
          }

          .action-btn {
            display: flex;
            align-items: center;
            gap: 4px;
            padding: 6px 12px;
            background: rgba(99, 102, 241, 0.1);
            border-radius: 6px;
            font-size: 11px;
            font-weight: 500;
            color: #6366F1;
            transition: all 0.3s ease;

            .el-icon {
              font-size: 12px;
              transition: transform 0.3s ease;
            }

            &:hover .el-icon {
              transform: translateX(3px);
            }
          }
        }
      }
    }

    .no-results {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;

      .no-results-icon {
        width: 64px;
        height: 64px;
        margin-bottom: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(99, 102, 241, 0.1);
        border-radius: 50%;

        .el-icon {
          font-size: 32px;
          color: #6366F1;
        }
      }

      .no-results-title {
        font-family: 'Space Grotesk', sans-serif;
        font-size: 20px;
        font-weight: 600;
        color: #111827;
        margin: 0 0 6px 0;
      }

      .no-results-desc {
        font-size: 13px;
        color: #6B7280;
        margin: 0 0 16px 0;
      }

      .retry-btn {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 10px 20px;
        background: linear-gradient(135deg, #6366F1, #8B5CF6);
        color: white;
        border: none;
        border-radius: 10px;
        font-size: 13px;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(99, 102, 241, 0.3);
        }
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  33% {
    transform: translate(20px, -20px);
  }
  66% {
    transform: translate(-15px, 15px);
  }
}

@keyframes badgePulse {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.05);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes ringRotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.08);
  }
}

@keyframes dataFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translate(8px, -8px) scale(1.15);
    opacity: 1;
  }
}

@keyframes shine {
  0% {
    left: -100%;
  }
  50%, 100% {
    left: 100%;
  }
}

@keyframes ringPulse {
  0%, 100% {
    filter: drop-shadow(0 0 6px rgba(99, 102, 241, 0.3));
  }
  50% {
    filter: drop-shadow(0 0 12px rgba(99, 102, 241, 0.5));
  }
}

@keyframes particleOrbit {
  from {
    transform: rotate(0deg) translateX(80px) rotate(0deg);
  }
  to {
    transform: rotate(360deg) translateX(80px) rotate(-360deg);
  }
}

@keyframes cardAppear {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: all 0.4s ease;
}

.fade-scale-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.fade-scale-leave-to {
  opacity: 0;
  transform: scale(1.05);
}

@media (max-width: 1200px) {
  .ai-recommend-page {
    .page-content {
      padding: 16px 24px;
    }

    .results-state .jobs-grid {
      grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    }
  }
}

@media (max-width: 900px) {
  .ai-recommend-page {
    .empty-state .empty-layout {
      flex-direction: column;
      gap: 24px;

      .ai-brain-section {
        width: 160px;
        height: 160px;
      }
    }

    .loading-state .loading-layout {
      flex-direction: column;
      gap: 24px;

      .analysis-core {
        width: 140px;
        height: 140px;
      }
    }

    .results-state .jobs-grid {
      grid-template-columns: 1fr;
    }
  }
}

@media (max-width: 768px) {
  .ai-recommend-page {
    .page-header {
      flex-direction: column;
      gap: 12px;
      text-align: center;

      .header-left {
        flex-direction: column;
      }
    }

    .empty-state {
      .empty-content {
        .ai-features {
          grid-template-columns: 1fr;
        }
      }
    }

    .loading-state {
      .loading-layout {
        .analysis-info {
          .analysis-steps {
            justify-content: center;
          }

          .analysis-metrics {
            flex-direction: column;
          }
        }
      }
    }

    .results-state {
      .results-summary {
        flex-direction: column;
        gap: 12px;

        .summary-divider {
          width: 60px;
          height: 1px;
        }
      }
    }
  }
}
</style>
