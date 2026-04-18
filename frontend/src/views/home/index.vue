<template>
  <div class="home-page">
    <div class="search-section">
      <el-card>
        <div class="search-box">
          <el-input v-model="searchForm.keyword" placeholder="搜索职位、公司" size="large" clearable @keyup.enter="handleSearch">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
        </div>
        <div class="filter-section">
          <el-select v-model="searchForm.category" placeholder="职位类别" clearable @change="handleSearch">
            <el-option label="技术研发" value="技术研发" />
            <el-option label="产品设计" value="产品设计" />
            <el-option label="设计" value="设计" />
            <el-option label="数据分析" value="数据分析" />
            <el-option label="移动开发" value="移动开发" />
          </el-select>
          <el-select v-model="searchForm.location" placeholder="工作地点" clearable @change="handleSearch">
            <el-option label="北京" value="北京" />
            <el-option label="上海" value="上海" />
            <el-option label="杭州" value="杭州" />
            <el-option label="深圳" value="深圳" />
            <el-option label="广州" value="广州" />
          </el-select>
          <el-select v-model="searchForm.experience" placeholder="工作经验" clearable @change="handleSearch">
            <el-option label="不限" :value="0" />
            <el-option label="1年以下" :value="1" />
            <el-option label="1-3年" :value="2" />
            <el-option label="3-5年" :value="3" />
            <el-option label="5-10年" :value="4" />
            <el-option label="10年以上" :value="5" />
          </el-select>
          <el-select v-model="searchForm.education" placeholder="学历要求" clearable @change="handleSearch">
            <el-option label="不限" value="不限" />
            <el-option label="大专" value="大专" />
            <el-option label="本科" value="本科" />
            <el-option label="硕士" value="硕士" />
            <el-option label="博士" value="博士" />
          </el-select>
          <div class="salary-range">
            <el-input-number v-model="searchForm.salaryMin" :min="0" :max="100" placeholder="最低薪资" controls-position="right" />
            <span class="separator">-</span>
            <el-input-number v-model="searchForm.salaryMax" :min="0" :max="200" placeholder="最高薪资" controls-position="right" />
            <span class="unit">K</span>
          </div>
        </div>
      </el-card>
    </div>

    <div class="job-list-section">
      <el-card v-loading="loading">
        <template #header>
          <div class="card-header">
            <span>职位列表</span>
            <span class="total">共 {{ total }} 个职位</span>
          </div>
        </template>

        <div v-if="jobList.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无符合条件的职位" />
        </div>

        <div v-else class="job-list">
          <div v-for="job in jobList" :key="job.id" class="job-item" @click="goToDetail(job.id)">
            <div class="job-main">
              <div class="job-title-row">
                <h3 class="job-title">{{ job.title }}</h3>
                <span class="salary">{{ job.salaryRange }}</span>
              </div>
              <div class="job-tags">
                <el-tag size="small" type="info">{{ job.location }}</el-tag>
                <el-tag size="small" type="info">{{ job.experienceName }}</el-tag>
                <el-tag size="small" type="info">{{ job.education }}</el-tag>
              </div>
            </div>
            <div class="company-info">
              <el-avatar :size="48" :src="job.companyLogo || defaultLogo">
                {{ job.companyName?.charAt(0) }}
              </el-avatar>
              <div class="company-detail">
                <div class="company-name">{{ job.companyName }}</div>
                <div class="company-meta">
                  <span>{{ job.companyIndustry }}</span>
                  <span v-if="job.companyScale">| {{ job.companyScale }}</span>
                </div>
              </div>
            </div>
            <div class="job-stats">
              <span><el-icon><View /></el-icon> {{ job.viewCount }}</span>
              <span><el-icon><Document /></el-icon> {{ job.applicationCount }}</span>
            </div>
          </div>
        </div>

        <div class="pagination-section">
          <el-pagination
            v-model:current-page="searchForm.pageNum"
            v-model:page-size="searchForm.pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSearch"
            @current-change="handleSearch"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getJobList } from '@/api/job'
import { Search, View, Document } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const jobList = ref([])
const total = ref(0)
const defaultLogo = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const searchForm = reactive({
  keyword: '',
  category: '',
  location: '',
  experience: null,
  education: '',
  salaryMin: null,
  salaryMax: null,
  pageNum: 1,
  pageSize: 10
})

const fetchJobList = async () => {
  loading.value = true
  try {
    const res = await getJobList(searchForm)
    jobList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('Failed to fetch job list:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  searchForm.pageNum = 1
  fetchJobList()
}

const goToDetail = (id) => {
  router.push(`/jobs/${id}`)
}

onMounted(() => {
  fetchJobList()
})
</script>

<style lang="scss" scoped>
.home-page {
  .search-section {
    margin-bottom: 20px;

    .search-box {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;

      .el-input {
        flex: 1;
      }
    }

    .filter-section {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      align-items: center;

      .el-select {
        width: 140px;
      }

      .salary-range {
        display: flex;
        align-items: center;
        gap: 8px;

        .el-input-number {
          width: 100px;
        }

        .separator {
          color: #999;
        }

        .unit {
          color: #666;
          font-size: 14px;
        }
      }
    }
  }

  .job-list-section {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .total {
        font-size: 14px;
        color: #999;
      }
    }

    .job-list {
      .job-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px;
        border-bottom: 1px solid #f0f0f0;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #f9fafc;
        }

        &:last-child {
          border-bottom: none;
        }

        .job-main {
          flex: 1;

          .job-title-row {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 10px;

            .job-title {
              font-size: 18px;
              font-weight: 600;
              color: #333;
              margin: 0;
            }

            .salary {
              font-size: 18px;
              color: #ff6b00;
              font-weight: 600;
            }
          }

          .job-tags {
            display: flex;
            gap: 8px;
          }
        }

        .company-info {
          display: flex;
          align-items: center;
          gap: 12px;
          margin: 0 40px;

          .company-detail {
            .company-name {
              font-size: 14px;
              color: #333;
              font-weight: 500;
            }

            .company-meta {
              font-size: 12px;
              color: #999;
              margin-top: 4px;
            }
          }
        }

        .job-stats {
          display: flex;
          flex-direction: column;
          gap: 6px;
          font-size: 12px;
          color: #999;

          span {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
    }

    .pagination-section {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }

    .empty-state {
      padding: 40px 0;
    }
  }
}
</style>
