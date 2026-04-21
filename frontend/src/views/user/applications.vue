<template>
  <div class="applications-page">
    <div class="page-header">
      <h1 class="page-title">我的投递</h1>
      <p class="page-subtitle">追踪你的求职进度</p>
    </div>

    <div class="stats-overview">
      <div class="stat-card" v-for="(stat, index) in statusStats" :key="index" @click="filterByStatus(stat.value)">
        <div class="stat-number" :style="{ color: stat.color }">{{ stat.count }}</div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>

    <div class="filter-bar">
      <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="handleSearch" class="status-select">
        <el-option label="全部状态" :value="null" />
        <el-option label="待查看" :value="0" />
        <el-option label="已查看" :value="1" />
        <el-option label="待面试" :value="2" />
        <el-option label="已录用" :value="3" />
        <el-option label="已拒绝" :value="4" />
      </el-select>
    </div>

    <div v-if="applicationList.length === 0 && !loading" class="empty-state">
      <div class="empty-icon">
        <el-icon><Document /></el-icon>
      </div>
      <h3>暂无投递记录</h3>
      <p>快去投递心仪的职位吧</p>
      <el-button type="primary" @click="$router.push('/jobs')">浏览职位</el-button>
    </div>

    <div v-else class="application-list">
      <div 
        v-for="(item, index) in applicationList" 
        :key="item.id" 
        class="application-card"
        :style="{ animationDelay: `${index * 50}ms` }"
      >
        <div class="card-header">
          <div class="status-badge" :class="getStatusClass(item.status)">
            <el-icon><component :is="getStatusIcon(item.status)" /></el-icon>
            <span>{{ item.statusText }}</span>
          </div>
          <div class="apply-time">{{ formatDate(item.createTime) }}</div>
        </div>

        <div class="card-body" @click="goJobDetail(item.jobId)">
          <div class="job-info">
            <h3 class="job-title">{{ item.jobTitle }}</h3>
            <div class="job-meta">
              <span class="company">{{ item.companyName }}</span>
              <span class="separator">·</span>
              <span class="location">{{ item.location }}</span>
              <span class="separator">·</span>
              <span class="salary">{{ item.salaryRange }}</span>
            </div>
          </div>
          <div class="resume-info">
            <el-icon><Document /></el-icon>
            <span>{{ item.resumeTitle }}</span>
          </div>
        </div>

        <div class="card-footer">
          <div class="timeline" v-if="item.viewTime">
            <div class="timeline-item">
              <div class="timeline-dot active"></div>
              <div class="timeline-content">
                <div class="timeline-title">投递成功</div>
                <div class="timeline-time">{{ formatDate(item.createTime) }}</div>
              </div>
            </div>
            <div class="timeline-item">
              <div class="timeline-dot active"></div>
              <div class="timeline-content">
                <div class="timeline-title">已被查看</div>
                <div class="timeline-time">{{ formatDate(item.viewTime) }}</div>
              </div>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" text @click="viewDetail(item)">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
            <el-button 
              v-if="item.status <= 1" 
              type="danger" 
              text 
              @click="handleCancel(item)"
            >
              <el-icon><Close /></el-icon>
              取消投递
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="total > pageSize" class="pagination-section">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="prev, pager, next"
        @size-change="handleSearch"
        @current-change="handleSearch"
        background
      />
    </div>

    <el-dialog v-model="detailVisible" title="投递详情" width="600px" class="detail-dialog">
      <div class="detail-content" v-if="currentApplication">
        <div class="detail-header">
          <div class="status-badge large" :class="getStatusClass(currentApplication.status)">
            <el-icon><component :is="getStatusIcon(currentApplication.status)" /></el-icon>
            <span>{{ currentApplication.statusText }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">职位信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">职位名称</span>
              <span class="value">{{ currentApplication.jobTitle }}</span>
            </div>
            <div class="detail-item">
              <span class="label">公司名称</span>
              <span class="value">{{ currentApplication.companyName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">工作地点</span>
              <span class="value">{{ currentApplication.location }}</span>
            </div>
            <div class="detail-item">
              <span class="label">薪资范围</span>
              <span class="value salary">{{ currentApplication.salaryRange }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h4 class="section-title">投递信息</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">投递简历</span>
              <span class="value">{{ currentApplication.resumeTitle }}</span>
            </div>
            <div class="detail-item">
              <span class="label">投递时间</span>
              <span class="value">{{ formatDate(currentApplication.createTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">查看时间</span>
              <span class="value">{{ currentApplication.viewTime ? formatDate(currentApplication.viewTime) : '-' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="currentApplication.remark">
          <h4 class="section-title">备注</h4>
          <p class="remark">{{ currentApplication.remark }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getApplicationList, cancelApplication } from '@/api/application'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, View, Close, Clock, Check, Warning, CircleClose, Timer } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const applicationList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)
const detailVisible = ref(false)
const currentApplication = ref(null)

const statusStats = computed(() => [
  { label: '全部', value: null, count: total.value, color: '#6366F1' },
  { label: '待查看', value: 0, count: applicationList.value.filter(a => a.status === 0).length, color: '#9CA3AF' },
  { label: '已查看', value: 1, count: applicationList.value.filter(a => a.status === 1).length, color: '#F59E0B' },
  { label: '待面试', value: 2, count: applicationList.value.filter(a => a.status === 2).length, color: '#3B82F6' },
  { label: '已录用', value: 3, count: applicationList.value.filter(a => a.status === 3).length, color: '#10B981' }
])

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

const filterByStatus = (status) => {
  statusFilter.value = status
  handleSearch()
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

const getStatusClass = (status) => {
  const classes = {
    0: 'pending',
    1: 'viewed',
    2: 'interview',
    3: 'hired',
    4: 'rejected'
  }
  return classes[status] || 'pending'
}

const getStatusIcon = (status) => {
  const icons = {
    0: Clock,
    1: View,
    2: Timer,
    3: Check,
    4: CircleClose
  }
  return icons[status] || Clock
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
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
  
  .page-title {
    font-size: 28px;
    font-weight: 700;
    color: #111827;
    margin: 0 0 8px 0;
  }
  
  .page-subtitle {
    font-size: 15px;
    color: #6B7280;
    margin: 0;
  }
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  margin-bottom: 24px;
  
  .stat-card {
    background: white;
    border-radius: 12px;
    padding: 20px;
    text-align: center;
    cursor: pointer;
    transition: all 200ms ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
    
    .stat-number {
      font-size: 28px;
      font-weight: 700;
      line-height: 1.2;
    }
    
    .stat-label {
      font-size: 13px;
      color: #6B7280;
      margin-top: 4px;
    }
  }
}

.filter-bar {
  margin-bottom: 24px;
  
  .status-select {
    width: 160px;
  }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 16px;
  
  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 24px;
    background: linear-gradient(135deg, #EEF2FF, #E0E7FF);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .el-icon {
      font-size: 36px;
      color: #6366F1;
    }
  }
  
  h3 {
    font-size: 20px;
    font-weight: 600;
    color: #111827;
    margin: 0 0 8px 0;
  }
  
  p {
    color: #6B7280;
    margin: 0 0 24px 0;
  }
}

.application-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  
  .application-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    animation: slideUp 300ms ease-out backwards;
    transition: all 200ms ease;
    
    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    }
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      background: #F9FAFB;
      border-bottom: 1px solid #F3F4F6;
      
      .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 500;
        
        &.pending {
          background: #F3F4F6;
          color: #6B7280;
        }
        
        &.viewed {
          background: #FEF3C7;
          color: #D97706;
        }
        
        &.interview {
          background: #DBEAFE;
          color: #2563EB;
        }
        
        &.hired {
          background: #D1FAE5;
          color: #059669;
        }
        
        &.rejected {
          background: #FEE2E2;
          color: #DC2626;
        }
      }
      
      .apply-time {
        font-size: 13px;
        color: #9CA3AF;
      }
    }
    
    .card-body {
      padding: 20px;
      cursor: pointer;
      
      .job-info {
        margin-bottom: 12px;
        
        .job-title {
          font-size: 18px;
          font-weight: 600;
          color: #111827;
          margin: 0 0 8px 0;
          
          &:hover {
            color: #4F46E5;
          }
        }
        
        .job-meta {
          font-size: 14px;
          color: #6B7280;
          
          .separator {
            margin: 0 8px;
            color: #D1D5DB;
          }
          
          .salary {
            color: #F97316;
            font-weight: 500;
          }
        }
      }
      
      .resume-info {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 6px 12px;
        background: #EEF2FF;
        border-radius: 6px;
        font-size: 13px;
        color: #4F46E5;
      }
    }
    
    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      border-top: 1px solid #F3F4F6;
      
      .timeline {
        display: flex;
        gap: 24px;
        
        .timeline-item {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .timeline-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #E5E7EB;
            
            &.active {
              background: #10B981;
            }
          }
          
          .timeline-content {
            .timeline-title {
              font-size: 12px;
              font-weight: 500;
              color: #374151;
            }
            
            .timeline-time {
              font-size: 11px;
              color: #9CA3AF;
            }
          }
        }
      }
      
      .actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.pagination-section {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.detail-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
  }
  
  .detail-content {
    .detail-header {
      text-align: center;
      margin-bottom: 24px;
      
      .status-badge.large {
        padding: 10px 20px;
        font-size: 15px;
      }
    }
    
    .detail-section {
      margin-bottom: 24px;
      
      .section-title {
        font-size: 14px;
        font-weight: 600;
        color: #6B7280;
        margin: 0 0 12px 0;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
      
      .detail-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        
        .detail-item {
          .label {
            display: block;
            font-size: 12px;
            color: #9CA3AF;
            margin-bottom: 4px;
          }
          
          .value {
            font-size: 15px;
            color: #111827;
            font-weight: 500;
            
            &.salary {
              color: #F97316;
            }
          }
        }
      }
      
      .remark {
        font-size: 14px;
        color: #374151;
        line-height: 1.6;
        margin: 0;
        padding: 12px;
        background: #F9FAFB;
        border-radius: 8px;
      }
    }
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .stats-overview {
    grid-template-columns: repeat(3, 1fr);
    
    .stat-card:nth-child(4),
    .stat-card:nth-child(5) {
      display: none;
    }
  }
  
  .application-list .application-card {
    .card-footer {
      flex-direction: column;
      gap: 16px;
      
      .timeline {
        width: 100%;
      }
      
      .actions {
        width: 100%;
        justify-content: flex-end;
      }
    }
  }
  
  .detail-dialog .detail-content .detail-section .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
