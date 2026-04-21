<template>
  <div class="admin-statistics-page">
    <div class="stats-overview">
      <div class="stats-grid">
        <div class="stat-card" v-for="(stat, index) in statsCards" :key="index" :style="{ animationDelay: `${index * 50}ms` }">
          <div class="stat-icon" :style="{ background: stat.gradient }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <div class="stat-trend" v-if="stat.trend" :class="stat.trend > 0 ? 'up' : 'down'">
            <el-icon><component :is="stat.trend > 0 ? 'Top' : 'Bottom'" /></el-icon>
            <span>{{ Math.abs(stat.trend) }}%</span>
          </div>
        </div>
      </div>
    </div>

    <div class="charts-section">
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <el-icon><TrendCharts /></el-icon>
              投递趋势
            </h3>
            <span class="chart-subtitle">近7天数据</span>
          </div>
          <div ref="applicationChartRef" class="chart-container"></div>
        </div>
        
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <el-icon><User /></el-icon>
              用户增长
            </h3>
            <span class="chart-subtitle">近7天数据</span>
          </div>
          <div ref="userChartRef" class="chart-container"></div>
        </div>
      </div>

      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <el-icon><PieChart /></el-icon>
              职位类别分布
            </h3>
            <span class="chart-subtitle">投递数据</span>
          </div>
          <div ref="categoryChartRef" class="chart-container"></div>
        </div>
        
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">
              <el-icon><DataAnalysis /></el-icon>
              行业投递分布
            </h3>
            <span class="chart-subtitle">热门行业</span>
          </div>
          <div ref="industryChartRef" class="chart-container"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { getDashboardData } from '@/api/dashboard'
import * as echarts from 'echarts'
import { User, OfficeBuilding, Briefcase, Document, CircleCheck, TrendCharts, PieChart, DataAnalysis, Top, Bottom } from '@element-plus/icons-vue'

const dashboard = ref({
  userCount: 0,
  companyCount: 0,
  jobCount: 0,
  applicationCount: 0,
  hiredCount: 0,
  todayUserCount: 0,
  todayApplicationCount: 0,
  applicationByCategory: [],
  applicationByIndustry: [],
  applicationByDate: [],
  userTrend: []
})

const statsCards = computed(() => [
  {
    icon: User,
    label: '用户总数',
    value: dashboard.value.userCount,
    gradient: 'linear-gradient(135deg, #6366F1 0%, #8B5CF6 100%)',
    trend: 12
  },
  {
    icon: OfficeBuilding,
    label: '企业总数',
    value: dashboard.value.companyCount,
    gradient: 'linear-gradient(135deg, #3B82F6 0%, #2563EB 100%)',
    trend: 8
  },
  {
    icon: Briefcase,
    label: '职位总数',
    value: dashboard.value.jobCount,
    gradient: 'linear-gradient(135deg, #10B981 0%, #059669 100%)',
    trend: 15
  },
  {
    icon: Document,
    label: '投递总数',
    value: dashboard.value.applicationCount,
    gradient: 'linear-gradient(135deg, #F59E0B 0%, #D97706 100%)',
    trend: 23
  },
  {
    icon: CircleCheck,
    label: '录用人数',
    value: dashboard.value.hiredCount,
    gradient: 'linear-gradient(135deg, #EC4899 0%, #DB2777 100%)',
    trend: -5
  },
  {
    icon: User,
    label: '今日新增',
    value: dashboard.value.todayUserCount,
    gradient: 'linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%)',
    trend: 18
  }
])

const applicationChartRef = ref(null)
const userChartRef = ref(null)
const categoryChartRef = ref(null)
const industryChartRef = ref(null)

let applicationChart = null
let userChart = null
let categoryChart = null
let industryChart = null

const fetchDashboard = async () => {
  try {
    const res = await getDashboardData()
    dashboard.value = res.data || {}
    initCharts()
  } catch (error) {
    console.error('获取数据失败:', error)
  }
}

const initCharts = () => {
  initApplicationChart()
  initUserChart()
  initCategoryChart()
  initIndustryChart()
}

const chartColors = {
  primary: '#6366F1',
  success: '#10B981',
  warning: '#F59E0B',
  danger: '#EF4444',
  info: '#3B82F6',
  purple: '#8B5CF6',
  pink: '#EC4899'
}

const initApplicationChart = () => {
  if (!applicationChartRef.value) return
  applicationChart = echarts.init(applicationChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#374151' },
      axisPointer: {
        type: 'cross',
        crossStyle: { color: '#999' }
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dashboard.value.applicationByDate?.map(item => item.name) || [],
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: { color: '#6B7280' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6B7280' },
      splitLine: { lineStyle: { color: '#F3F4F6' } }
    },
    series: [{
      data: dashboard.value.applicationByDate?.map(item => item.value) || [],
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: {
        width: 3,
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#6366F1' },
          { offset: 1, color: '#8B5CF6' }
        ])
      },
      itemStyle: {
        color: '#6366F1',
        borderWidth: 2,
        borderColor: '#fff'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(99, 102, 241, 0.3)' },
          { offset: 1, color: 'rgba(99, 102, 241, 0.05)' }
        ])
      }
    }]
  }
  applicationChart.setOption(option)
}

const initUserChart = () => {
  if (!userChartRef.value) return
  userChart = echarts.init(userChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#374151' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dashboard.value.userTrend?.map(item => item.name) || [],
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: { color: '#6B7280' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6B7280' },
      splitLine: { lineStyle: { color: '#F3F4F6' } }
    },
    series: [{
      data: dashboard.value.userTrend?.map(item => item.value) || [],
      type: 'bar',
      barWidth: '50%',
      itemStyle: {
        borderRadius: [8, 8, 0, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#10B981' },
          { offset: 1, color: '#059669' }
        ])
      }
    }]
  }
  userChart.setOption(option)
}

const initCategoryChart = () => {
  if (!categoryChartRef.value) return
  categoryChart = echarts.init(categoryChartRef.value)
  const colors = ['#6366F1', '#8B5CF6', '#EC4899', '#F59E0B', '#10B981', '#3B82F6']
  const option = {
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#374151' }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: { color: '#6B7280' }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: { show: false },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      labelLine: { show: false },
      data: (dashboard.value.applicationByCategory || []).map((item, index) => ({
        ...item,
        itemStyle: { color: colors[index % colors.length] }
      }))
    }]
  }
  categoryChart.setOption(option)
}

const initIndustryChart = () => {
  if (!industryChartRef.value) return
  industryChart = echarts.init(industryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#E5E7EB',
      borderWidth: 1,
      textStyle: { color: '#374151' }
    },
    grid: {
      left: '3%',
      right: '10%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6B7280' },
      splitLine: { lineStyle: { color: '#F3F4F6' } }
    },
    yAxis: {
      type: 'category',
      data: dashboard.value.applicationByIndustry?.map(item => item.name) || [],
      axisLine: { lineStyle: { color: '#E5E7EB' } },
      axisLabel: { color: '#6B7280' }
    },
    series: [{
      type: 'bar',
      data: dashboard.value.applicationByIndustry?.map((item, index) => ({
        value: item.value,
        itemStyle: {
          borderRadius: [0, 8, 8, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#F59E0B' },
            { offset: 1, color: '#FBBF24' }
          ])
        }
      })) || [],
      barWidth: '60%'
    }]
  }
  industryChart.setOption(option)
}

const handleResize = () => {
  applicationChart?.resize()
  userChart?.resize()
  categoryChart?.resize()
  industryChart?.resize()
}

onMounted(() => {
  fetchDashboard()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  applicationChart?.dispose()
  userChart?.dispose()
  categoryChart?.dispose()
  industryChart?.dispose()
})
</script>

<style lang="scss" scoped>
.admin-statistics-page {
  .stats-overview {
    margin-bottom: 24px;

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;

      .stat-card {
        background: white;
        border-radius: var(--radius-xl);
        padding: 24px;
        display: flex;
        align-items: center;
        gap: 16px;
        box-shadow: var(--shadow-sm);
        transition: all var(--transition-normal);
        animation: slideUp var(--transition-slow) ease-out backwards;
        position: relative;
        overflow: hidden;

        &:hover {
          transform: translateY(-4px);
          box-shadow: var(--shadow-lg);
        }

        .stat-icon {
          width: 56px;
          height: 56px;
          border-radius: var(--radius-xl);
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;

          .el-icon {
            font-size: 28px;
            color: white;
          }
        }

        .stat-content {
          flex: 1;

          .stat-value {
            font-size: 28px;
            font-weight: var(--font-weight-bold);
            color: var(--text-primary);
            line-height: 1.2;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: var(--font-size-sm);
            color: var(--text-tertiary);
            font-weight: var(--font-weight-medium);
          }
        }

        .stat-trend {
          position: absolute;
          top: 16px;
          right: 16px;
          display: flex;
          align-items: center;
          gap: 4px;
          padding: 4px 8px;
          border-radius: var(--radius-md);
          font-size: var(--font-size-xs);
          font-weight: var(--font-weight-semibold);

          &.up {
            background: rgba(16, 185, 129, 0.1);
            color: var(--success-600);
          }

          &.down {
            background: rgba(239, 68, 68, 0.1);
            color: var(--danger-500);
          }
        }
      }
    }
  }

  .charts-section {
    display: flex;
    flex-direction: column;
    gap: 24px;

    .chart-row {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 24px;

      .chart-card {
        background: white;
        border-radius: var(--radius-xl);
        padding: 24px;
        box-shadow: var(--shadow-sm);
        animation: slideUp var(--transition-slow) ease-out backwards;

        &:nth-child(1) { animation-delay: 200ms; }
        &:nth-child(2) { animation-delay: 250ms; }
        &:nth-child(3) { animation-delay: 300ms; }
        &:nth-child(4) { animation-delay: 350ms; }

        .chart-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;

          .chart-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: var(--font-size-lg);
            font-weight: var(--font-weight-semibold);
            color: var(--text-primary);
            margin: 0;

            .el-icon {
              font-size: 20px;
              color: var(--primary-500);
            }
          }

          .chart-subtitle {
            font-size: var(--font-size-sm);
            color: var(--text-tertiary);
          }
        }

        .chart-container {
          height: 300px;
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
  .admin-statistics-page {
    .charts-section {
      .chart-row {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
