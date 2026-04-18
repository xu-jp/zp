<template>
  <div class="admin-center">
    <div v-if="isDashboard" class="admin-dashboard">
      <el-row :gutter="16" class="stat-cards">
        <el-col :span="4" v-for="card in statCards" :key="card.key">
          <el-card shadow="hover" class="stat-card" :style="{ borderTop: `3px solid ${card.color}` }">
            <div class="stat-card-body">
              <div class="stat-icon" :style="{ backgroundColor: card.bgColor }">
                <el-icon :size="24" :style="{ color: card.color }">
                  <component :is="card.icon" />
                </el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
                <div class="stat-label">{{ card.label }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div class="section-title">快捷操作</div>
      <el-row :gutter="16" class="quick-actions">
        <el-col :span="6" v-for="(action, index) in quickActions" :key="action.path + index">
          <el-card shadow="hover" class="action-card" @click="router.push(action.path)">
            <div class="action-body">
              <div class="action-icon" :style="{ backgroundColor: action.bgColor }">
                <el-icon :size="28" :style="{ color: action.color }">
                  <component :is="action.icon" />
                </el-icon>
              </div>
              <div class="action-text">{{ action.label }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="bottom-section">
        <el-col :span="12">
          <el-card shadow="hover" class="todo-card">
            <template #header>
              <div class="card-title">
                <el-icon><Bell /></el-icon>
                <span>待办事项</span>
              </div>
            </template>
            <el-timeline v-if="todoItems.length > 0">
              <el-timeline-item
                v-for="item in todoItems"
                :key="item.key"
                :color="item.color"
                :timestamp="item.tag"
                placement="top"
              >
                <div class="todo-item" @click="router.push(item.path)">
                  <span class="todo-text">{{ item.text }}</span>
                  <el-icon class="todo-arrow"><ArrowRight /></el-icon>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无待办事项" :image-size="80" />
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover" class="log-card">
            <template #header>
              <div class="card-title">
                <el-icon><Document /></el-icon>
                <span>最近操作</span>
                <el-button type="primary" link class="view-more" @click="router.push('/admin/logs')">
                  查看更多 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </template>
            <el-timeline v-if="recentLogs.length > 0">
              <el-timeline-item
                v-for="log in recentLogs"
                :key="log.id"
                color="#409eff"
                :timestamp="formatRelativeTime(log.createTime)"
                placement="top"
              >
                <div class="log-item">
                  <el-tag size="small" :type="getLogTagType(log.type)" class="log-tag">{{ log.type }}</el-tag>
                  <span class="log-content">{{ log.content }}</span>
                </div>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无操作记录" :image-size="80" />
          </el-card>
        </el-col>
      </el-row>
    </div>
    <router-view v-else />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getDashboardData } from '@/api/dashboard'
import { getCompanyList } from '@/api/adminCompany'
import { getJobList } from '@/api/adminJob'
import { getUserList } from '@/api/adminUser'
import { getLogList } from '@/api/operationLog'
import {
  User, OfficeBuilding, Briefcase, Warning, Promotion, ChatDotRound,
  Bell, Document, ArrowRight, Setting, List, DataAnalysis, Search
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isDashboard = computed(() => route.path === '/admin')

const dashboardData = ref({})
const pendingCompanyCount = ref(0)
const pendingJobCount = ref(0)
const bannedUserCount = ref(0)
const recentLogs = ref([])

const statCards = computed(() => [
  {
    key: 'user',
    label: '注册用户总数',
    value: dashboardData.value.userCount || 0,
    icon: User,
    color: '#409eff',
    bgColor: '#ecf5ff'
  },
  {
    key: 'company',
    label: '企业总数',
    value: dashboardData.value.companyCount || 0,
    icon: OfficeBuilding,
    color: '#67c23a',
    bgColor: '#f0f9eb'
  },
  {
    key: 'job',
    label: '职位总数',
    value: dashboardData.value.jobCount || 0,
    icon: Briefcase,
    color: '#e6a23c',
    bgColor: '#fdf6ec'
  },
  {
    key: 'pending',
    label: '待审核事项',
    value: pendingCompanyCount.value + pendingJobCount.value,
    icon: Warning,
    color: '#f56c6c',
    bgColor: '#fef0f0'
  },
  {
    key: 'todayApply',
    label: '今日投递数',
    value: dashboardData.value.todayApplicationCount || 0,
    icon: Promotion,
    color: '#9b59b6',
    bgColor: '#f5eef8'
  },
  {
    key: 'interview',
    label: '待处理面试',
    value: dashboardData.value.pendingInterviewCount || 0,
    icon: ChatDotRound,
    color: '#00bcd4',
    bgColor: '#e0f7fa'
  }
])

const quickActions = [
  { label: '企业审核', path: '/admin/companies', icon: OfficeBuilding, color: '#409eff', bgColor: '#ecf5ff' },
  { label: '职位审核', path: '/admin/jobs', icon: Briefcase, color: '#67c23a', bgColor: '#f0f9eb' },
  { label: '用户管理', path: '/admin/users', icon: User, color: '#e6a23c', bgColor: '#fdf6ec' },
  { label: '数据统计', path: '/admin/statistics', icon: DataAnalysis, color: '#f56c6c', bgColor: '#fef0f0' },
  { label: '系统设置', path: '/admin/settings', icon: Setting, color: '#9b59b6', bgColor: '#f5eef8' },
  { label: '操作日志', path: '/admin/logs', icon: List, color: '#00bcd4', bgColor: '#e0f7fa' },
  { label: '企业管理', path: '/admin/companies', icon: Search, color: '#ff9800', bgColor: '#fff8e1' },
  { label: '投递统计', path: '/admin/statistics', icon: Promotion, color: '#795548', bgColor: '#efebe9' }
]

const todoItems = computed(() => {
  const items = []
  if (pendingCompanyCount.value > 0) {
    items.push({
      key: 'pendingCompany',
      text: `${pendingCompanyCount.value} 家企业待审核`,
      path: '/admin/companies',
      color: '#f56c6c',
      tag: '高优先级'
    })
  }
  if (pendingJobCount.value > 0) {
    items.push({
      key: 'pendingJob',
      text: `${pendingJobCount.value} 个职位待审核`,
      path: '/admin/jobs',
      color: '#e6a23c',
      tag: '中优先级'
    })
  }
  if (bannedUserCount.value > 0) {
    items.push({
      key: 'bannedUser',
      text: `${bannedUserCount.value} 个用户已被封禁`,
      path: '/admin/users',
      color: '#67c23a',
      tag: '低优先级'
    })
  }
  return items
})

const fetchDashboardData = async () => {
  try {
    const res = await getDashboardData()
    dashboardData.value = res.data || {}
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
}

const fetchPendingCompanies = async () => {
  try {
    const res = await getCompanyList({ auditStatus: 0, pageNum: 1, pageSize: 1 })
    pendingCompanyCount.value = res.data?.total || 0
  } catch (error) {
    console.error('获取待审核企业失败:', error)
  }
}

const fetchPendingJobs = async () => {
  try {
    const res = await getJobList({ auditStatus: 0, pageNum: 1, pageSize: 1 })
    pendingJobCount.value = res.data?.total || 0
  } catch (error) {
    console.error('获取待审核职位失败:', error)
  }
}

const fetchBannedUsers = async () => {
  try {
    const res = await getUserList({ status: 1, pageNum: 1, pageSize: 1 })
    bannedUserCount.value = res.data?.total || 0
  } catch (error) {
    console.error('获取封禁用户失败:', error)
  }
}

const fetchRecentLogs = async () => {
  try {
    const res = await getLogList({ pageNum: 1, pageSize: 5 })
    recentLogs.value = res.data?.records || []
  } catch (error) {
    console.error('获取操作日志失败:', error)
  }
}

const formatRelativeTime = (date) => {
  if (!date) return ''
  const now = new Date()
  const target = new Date(date)
  const diff = now - target
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  return target.toLocaleDateString('zh-CN')
}

const getLogTagType = (type) => {
  switch (type) {
    case '新增': return 'success'
    case '修改': return 'warning'
    case '删除': return 'danger'
    case '审核': return 'primary'
    default: return 'info'
  }
}

const loadAllData = () => {
  fetchDashboardData()
  fetchPendingCompanies()
  fetchPendingJobs()
  fetchBannedUsers()
  fetchRecentLogs()
}

watch(isDashboard, (val) => {
  if (val) loadAllData()
})

onMounted(() => {
  if (isDashboard.value) loadAllData()
})
</script>

<style lang="scss" scoped>
.admin-dashboard {
  .stat-cards {
    margin-bottom: 20px;

    .stat-card {
      cursor: default;
      transition: all 0.3s;
      border-radius: 8px;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
      }

      :deep(.el-card__body) {
        padding: 16px;
      }

      .stat-card-body {
        display: flex;
        align-items: center;
        gap: 14px;

        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }

        .stat-info {
          min-width: 0;

          .stat-value {
            font-size: 26px;
            font-weight: 700;
            line-height: 1.2;
          }

          .stat-label {
            font-size: 13px;
            color: #909399;
            margin-top: 4px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }
    }
  }

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 14px;
    padding-left: 10px;
    border-left: 3px solid #409eff;
  }

  .quick-actions {
    margin-bottom: 20px;

    .action-card {
      cursor: pointer;
      transition: all 0.3s;
      border-radius: 8px;
      text-align: center;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      :deep(.el-card__body) {
        padding: 20px 16px;
      }

      .action-body {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 10px;

        .action-icon {
          width: 52px;
          height: 52px;
          border-radius: 14px;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: transform 0.3s;
        }

        .action-text {
          font-size: 14px;
          color: #606266;
          font-weight: 500;
        }
      }

      &:hover .action-icon {
        transform: scale(1.1);
      }
    }
  }

  .bottom-section {
    .todo-card,
    .log-card {
      border-radius: 8px;
      height: 100%;

      :deep(.el-card__header) {
        padding: 14px 20px;
        border-bottom: 1px solid #f0f0f0;
      }

      .card-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 15px;
        font-weight: 600;
        color: #303133;

        .el-icon {
          font-size: 18px;
          color: #409eff;
        }

        .view-more {
          margin-left: auto;
          font-size: 13px;
          display: flex;
          align-items: center;
          gap: 2px;
        }
      }

      .todo-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        cursor: pointer;
        padding: 6px 10px;
        border-radius: 6px;
        transition: background-color 0.2s;

        &:hover {
          background-color: #f5f7fa;
        }

        .todo-text {
          font-size: 14px;
          color: #303133;
        }

        .todo-arrow {
          color: #c0c4cc;
          font-size: 14px;
        }
      }

      .log-item {
        display: flex;
        align-items: center;
        gap: 8px;

        .log-tag {
          flex-shrink: 0;
        }

        .log-content {
          font-size: 13px;
          color: #606266;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      :deep(.el-timeline) {
        padding-left: 2px;
      }

      :deep(.el-timeline-item__timestamp) {
        font-size: 12px;
        color: #909399;
      }
    }
  }
}
</style>
