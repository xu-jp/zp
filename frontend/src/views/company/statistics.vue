<template>
  <div class="company-statistics-page">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409eff;">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalApplications || 0 }}</div>
              <div class="stat-label">总投递量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #e6a23c;">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingApplications || 0 }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67c23a;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.hiredApplications || 0 }}</div>
              <div class="stat-label">已录用</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #f56c6c;">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ (statistics.conversionRate || 0).toFixed(1) }}%</div>
              <div class="stat-label">转化率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>近7天投递趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>职位投递排行</span>
          </template>
          <div ref="rankChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>职位状态分布</span>
          </template>
          <div class="job-stats">
            <div class="stat-item">
              <span class="label">发布职位总数</span>
              <span class="value">{{ statistics.totalJobs || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="label">招聘中职位</span>
              <span class="value active">{{ statistics.activeJobs || 0 }}</span>
            </div>
            <div class="stat-item">
              <span class="label">面试中</span>
              <span class="value interview">{{ statistics.interviewApplications || 0 }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>投递状态分布</span>
          </template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Document, Clock, User, TrendCharts } from '@element-plus/icons-vue'
import { getStatistics } from '@/api/companyApplication'
import * as echarts from 'echarts'

const trendChartRef = ref(null)
const rankChartRef = ref(null)
const statusChartRef = ref(null)
let trendChart = null
let rankChart = null
let statusChart = null

const statistics = ref({
  totalApplications: 0,
  pendingApplications: 0,
  interviewApplications: 0,
  hiredApplications: 0,
  totalJobs: 0,
  activeJobs: 0,
  conversionRate: 0,
  applicationTrend: [],
  jobApplicationCount: []
})

const fetchStatistics = async () => {
  try {
    const res = await getStatistics()
    statistics.value = res.data || {}
    initCharts()
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const initCharts = () => {
  initTrendChart()
  initRankChart()
  initStatusChart()
}

const initTrendChart = () => {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const trendData = statistics.value.applicationTrend || []
  const dates = trendData.map(item => item.date)
  const counts = trendData.map(item => item.count)

  trendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '投递数',
      type: 'line',
      smooth: true,
      data: counts,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      },
      lineStyle: {
        color: '#409eff'
      },
      itemStyle: {
        color: '#409eff'
      }
    }]
  })
}

const initRankChart = () => {
  if (!rankChartRef.value) return
  rankChart = echarts.init(rankChartRef.value)

  const rankData = statistics.value.jobApplicationCount || []
  const jobTitles = rankData.map(item => item.jobTitle)
  const counts = rankData.map(item => item.count)

  rankChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: jobTitles.reverse(),
      axisLabel: {
        width: 80,
        overflow: 'truncate'
      }
    },
    series: [{
      name: '投递数',
      type: 'bar',
      data: counts.reverse(),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#95d475' }
        ])
      }
    }]
  })
}

const initStatusChart = () => {
  if (!statusChartRef.value) return
  statusChart = echarts.init(statusChartRef.value)

  statusChart.setOption({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '投递状态',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: statistics.value.pendingApplications || 0, name: '待处理', itemStyle: { color: '#e6a23c' } },
        { value: statistics.value.interviewApplications || 0, name: '面试中', itemStyle: { color: '#409eff' } },
        { value: statistics.value.hiredApplications || 0, name: '已录用', itemStyle: { color: '#67c23a' } }
      ]
    }]
  })
}

const handleResize = () => {
  trendChart?.resize()
  rankChart?.resize()
  statusChart?.resize()
}

onMounted(() => {
  fetchStatistics()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  rankChart?.dispose()
  statusChart?.dispose()
})
</script>

<style lang="scss" scoped>
.company-statistics-page {
  .stat-cards {
    margin-bottom: 20px;

    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;

          .el-icon {
            font-size: 28px;
            color: #fff;
          }
        }

        .stat-info {
          .stat-value {
            font-size: 28px;
            font-weight: 600;
            color: #333;
          }

          .stat-label {
            font-size: 14px;
            color: #999;
            margin-top: 4px;
          }
        }
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;

    .chart-container {
      height: 300px;
    }
  }

  .job-stats {
    padding: 20px;

    .stat-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .label {
        color: #666;
        font-size: 14px;
      }

      .value {
        font-size: 24px;
        font-weight: 600;
        color: #333;

        &.active {
          color: #67c23a;
        }

        &.interview {
          color: #409eff;
        }
      }
    }
  }
}
</style>
