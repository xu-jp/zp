<template>
  <div class="interviews-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的面试</span>
          <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="handleSearch" style="width: 150px">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已通过" :value="4" />
            <el-option label="未通过" :value="5" />
          </el-select>
        </div>
      </template>

      <div class="interview-list" v-loading="loading">
        <div 
          v-for="item in interviewList" 
          :key="item.id" 
          class="interview-item"
        >
          <div class="interview-main">
            <div class="interview-header">
              <div class="company-info">
                <el-avatar :size="40" :src="item.companyLogo">
                  {{ item.companyName?.charAt(0) }}
                </el-avatar>
                <div class="company-detail">
                  <div class="company-name">{{ item.companyName }}</div>
                  <div class="job-title">{{ item.jobTitle }}</div>
                </div>
              </div>
              <el-tag :type="getStatusType(item.status)" size="large">{{ item.statusText }}</el-tag>
            </div>

            <div class="interview-info">
              <div class="info-item">
                <el-icon><Clock /></el-icon>
                <span>面试时间：{{ formatDate(item.interviewTime) }}</span>
              </div>
              <div class="info-item">
                <el-icon><Location /></el-icon>
                <span>面试方式：{{ item.interviewTypeText }}</span>
              </div>
              <div class="info-item">
                <el-icon><MapLocation /></el-icon>
                <span>
                  {{ item.interviewType === 1 ? '面试链接' : '面试地点' }}：
                  <template v-if="item.interviewType === 1">
                    <el-link :href="item.onlineLink" target="_blank" type="primary">点击进入</el-link>
                  </template>
                  <template v-else>{{ item.location }}</template>
                </span>
              </div>
              <div class="info-item" v-if="item.interviewerName">
                <el-icon><User /></el-icon>
                <span>面试官：{{ item.interviewerName }} {{ item.interviewerPhone }}</span>
              </div>
            </div>

            <div class="interview-requirement" v-if="item.requirement">
              <span class="label">面试要求：</span>
              {{ item.requirement }}
            </div>

            <div class="interview-result" v-if="item.result">
              <span class="label">面试评价：</span>
              {{ item.result }}
            </div>
          </div>

          <div class="interview-actions" v-if="item.status === 0">
            <el-button type="success" @click="handleConfirm(item, 1)">确认参加</el-button>
            <el-button type="danger" @click="handleConfirm(item, 2)">取消面试</el-button>
          </div>
        </div>

        <el-empty v-if="!loading && interviewList.length === 0" description="暂无面试邀约" />
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getInterviewList, updateInterviewStatus } from '@/api/interview'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Location, MapLocation, User } from '@element-plus/icons-vue'

const loading = ref(false)
const interviewList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const statusFilter = ref(null)

const fetchInterviews = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (statusFilter.value !== null) {
      params.status = statusFilter.value
    }
    const res = await getInterviewList(params)
    interviewList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取面试列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchInterviews()
}

const handleConfirm = async (item, status) => {
  const action = status === 1 ? '确认参加' : '取消'
  try {
    await ElMessageBox.confirm(`确定要${action}该面试吗？`, '提示', {
      type: 'warning'
    })
    await updateInterviewStatus({
      interviewId: item.id,
      status: status
    })
    ElMessage.success(`${action}成功`)
    fetchInterviews()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

const getStatusType = (status) => {
  const types = {
    0: 'primary',
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'success',
    5: 'danger'
  }
  return types[status] || 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchInterviews()
})
</script>

<style lang="scss" scoped>
.interviews-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .interview-list {
    .interview-item {
      padding: 20px;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .interview-main {
        .interview-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 16px;

          .company-info {
            display: flex;
            align-items: center;
            gap: 12px;

            .company-detail {
              .company-name {
                font-size: 16px;
                font-weight: 600;
                color: #333;
              }

              .job-title {
                font-size: 14px;
                color: #666;
                margin-top: 4px;
              }
            }
          }
        }

        .interview-info {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 12px;
          margin-bottom: 16px;

          .info-item {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #666;
            font-size: 14px;

            .el-icon {
              color: #409eff;
            }
          }
        }

        .interview-requirement,
        .interview-result {
          padding: 12px;
          background: #f5f7fa;
          border-radius: 4px;
          margin-bottom: 12px;
          font-size: 14px;
          color: #666;

          .label {
            color: #333;
            font-weight: 500;
          }
        }
      }

      .interview-actions {
        display: flex;
        gap: 12px;
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px dashed #eee;
      }
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
