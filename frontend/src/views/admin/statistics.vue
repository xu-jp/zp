<template>
  <div class="admin-statistics-page">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.userCount }}</div>
          <div class="stat-label">用户总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.companyCount }}</div>
          <div class="stat-label">企业总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.jobCount }}</div>
          <div class="stat-label">职位总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.applicationCount }}</div>
          <div class="stat-label">投递总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.hiredCount }}</div>
          <div class="stat-label">录用人数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.todayUserCount }}</div>
          <div class="stat-label">今日新增用户</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>投递趋势（近7天）</span>
          </template>
          <div ref="applicationChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>用户增长趋势（近7天）</span>
          </template>
          <div ref="userChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>职位类别投递分布</span>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>行业投递分布</span>
          </template>
          <div ref="industryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getDashboardData } from '@/api/dashboard'
import * as echarts from 'echarts'

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

const initApplicationChart = () => {
  if (!applicationChartRef.value) return
  applicationChart = echarts.init(applicationChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: dashboard.value.applicationByDate?.map(item => item.name) || []
    },
    yAxis: { type: 'value' },
    series: [{
      data: dashboard.value.applicationByDate?.map(item => item.value) || [],
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 },
      itemStyle: { color: '#409eff' }
    }]
  }
  applicationChart.setOption(option)
}

const initUserChart = () => {
  if (!userChartRef.value) return
  userChart = echarts.init(userChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: dashboard.value.userTrend?.map(item => item.name) || []
    },
    yAxis: { type: 'value' },
    series: [{
      data: dashboard.value.userTrend?.map(item => item.value) || [],
      type: 'bar',
      itemStyle: { color: '#67c23a' }
    }]
  }
  userChart.setOption(option)
}

const initCategoryChart = () => {
  if (!categoryChartRef.value) return
  categoryChart = echarts.init(categoryChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [{
      type: 'pie',
      radius: '50%',
      data: dashboard.value.applicationByCategory || [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  categoryChart.setOption(option)
}

const initIndustryChart = () => {
  if (!industryChartRef.value) return
  industryChart = echarts.init(industryChartRef.value)
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: dashboard.value.applicationByIndustry?.map(item => item.name) || []
    },
    series: [{
      type: 'bar',
      data: dashboard.value.applicationByIndustry?.map(item => item.value) || [],
      itemStyle: { color: '#e6a23c' }
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
  .stat-cards {
    margin-bottom: 20px;

    .stat-card {
      text-align: center;
      padding: 10px 0;

      .stat-value {
        font-size: 28px;
        font-weight: bold;
        color: #409eff;
      }

      .stat-label {
        font-size: 14px;
        color: #666;
        margin-top: 5px;
      }
    }
  }

  .chart-row {
    margin-bottom: 20px;

    .chart-container {
      height: 300px;
    }
  }
}
</style>
