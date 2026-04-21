<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-bg">
        <div class="hero-shape shape-1"></div>
        <div class="hero-shape shape-2"></div>
        <div class="hero-shape shape-3"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="title-line">找到理想工作</span>
          <span class="title-line highlight">开启职业新篇章</span>
        </h1>
        <p class="hero-subtitle">数千家优质企业正在招聘，发现属于你的机会</p>
        
        <div class="hero-search">
          <div class="search-wrapper">
            <el-input 
              v-model="searchForm.keyword" 
              placeholder="搜索职位、公司或关键词..." 
              size="large" 
              clearable 
              @keyup.enter="handleSearch"
              class="search-input"
            >
              <template #prefix>
                <el-icon class="search-icon"><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" size="large" @click="handleSearch" class="search-btn">
              <span>搜索职位</span>
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          
          <div class="hot-search">
            <span class="hot-label">热门搜索：</span>
            <div class="hot-tags">
              <el-tag 
                v-for="tag in hotTags" 
                :key="tag" 
                class="hot-tag"
                @click="searchByTag(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="stats-section">
      <div class="stats-container">
        <div class="stat-item" v-for="(stat, index) in platformStats" :key="index">
          <div class="stat-icon" :style="{ background: stat.gradient }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="filter-section">
      <div class="filter-container">
        <div class="filter-grid">
          <div class="filter-item">
            <label class="filter-label">
              <el-icon><Briefcase /></el-icon>
              职位类别
            </label>
            <el-select v-model="searchForm.category" placeholder="全部类别" clearable @change="handleSearch" class="filter-select">
              <el-option label="技术研发" value="技术研发" />
              <el-option label="产品设计" value="产品设计" />
              <el-option label="设计" value="设计" />
              <el-option label="数据分析" value="数据分析" />
              <el-option label="移动开发" value="移动开发" />
              <el-option label="运营推广" value="运营推广" />
              <el-option label="市场营销" value="市场营销" />
            </el-select>
          </div>
          
          <div class="filter-item">
            <label class="filter-label">
              <el-icon><Location /></el-icon>
              工作地点
            </label>
            <el-select v-model="searchForm.location" placeholder="全部城市" clearable @change="handleSearch" class="filter-select">
              <el-option label="北京" value="北京" />
              <el-option label="上海" value="上海" />
              <el-option label="杭州" value="杭州" />
              <el-option label="深圳" value="深圳" />
              <el-option label="广州" value="广州" />
              <el-option label="成都" value="成都" />
              <el-option label="武汉" value="武汉" />
            </el-select>
          </div>
          
          <div class="filter-item">
            <label class="filter-label">
              <el-icon><Clock /></el-icon>
              工作经验
            </label>
            <el-select v-model="searchForm.experience" placeholder="不限" clearable @change="handleSearch" class="filter-select">
              <el-option label="不限" :value="0" />
              <el-option label="应届生" :value="1" />
              <el-option label="1-3年" :value="2" />
              <el-option label="3-5年" :value="3" />
              <el-option label="5-10年" :value="4" />
              <el-option label="10年以上" :value="5" />
            </el-select>
          </div>
          
          <div class="filter-item">
            <label class="filter-label">
              <el-icon><Reading /></el-icon>
              学历要求
            </label>
            <el-select v-model="searchForm.education" placeholder="不限" clearable @change="handleSearch" class="filter-select">
              <el-option label="不限" value="不限" />
              <el-option label="大专" value="大专" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
            </el-select>
          </div>
          
          <div class="filter-item salary-item">
            <label class="filter-label">
              <el-icon><Coin /></el-icon>
              薪资范围
            </label>
            <div class="salary-range">
              <el-input-number 
                v-model="searchForm.salaryMin" 
                :min="0" 
                :max="100" 
                placeholder="最低" 
                controls-position="right"
                class="salary-input"
              />
              <span class="range-separator">—</span>
              <el-input-number 
                v-model="searchForm.salaryMax" 
                :min="0" 
                :max="200" 
                placeholder="最高" 
                controls-position="right"
                class="salary-input"
              />
              <span class="salary-unit">K</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="jobs-section">
      <div class="section-header">
        <div class="header-left">
          <h2 class="section-title">
            <span class="title-icon">🎯</span>
            为你推荐
          </h2>
          <span class="job-count">共 <strong>{{ total }}</strong> 个职位</span>
        </div>
        <el-button text class="view-all-btn" @click="goToJobList">
          查看全部
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div v-if="jobList.length === 0 && !loading" class="empty-state">
        <div class="empty-illustration">
          <el-icon class="empty-icon"><Search /></el-icon>
        </div>
        <h3>暂无符合条件的职位</h3>
        <p>试试调整筛选条件，发现更多机会</p>
        <el-button type="primary" @click="resetFilters">重置筛选</el-button>
      </div>

      <div v-else class="job-grid">
        <div 
          v-for="(job, index) in jobList" 
          :key="job.id" 
          class="job-card"
          @click="goToDetail(job.id)"
          :style="{ animationDelay: `${index * 60}ms` }"
        >
          <div class="job-card-header">
            <div class="job-main">
              <h3 class="job-title">{{ job.title }}</h3>
              <div class="job-salary">{{ job.salaryRange }}</div>
            </div>
            <div class="job-tags">
              <span class="job-tag location">
                <el-icon><Location /></el-icon>
                {{ job.location }}
              </span>
              <span class="job-tag experience">
                <el-icon><Clock /></el-icon>
                {{ job.experienceName }}
              </span>
              <span class="job-tag education">
                <el-icon><Reading /></el-icon>
                {{ job.education }}
              </span>
            </div>
          </div>
          
          <div class="job-card-body">
            <div class="company-info">
              <el-avatar :size="44" :src="job.companyLogo || defaultLogo" class="company-avatar">
                {{ job.companyName?.charAt(0) }}
              </el-avatar>
              <div class="company-details">
                <div class="company-name">{{ job.companyName }}</div>
                <div class="company-meta">
                  <span class="meta-item">{{ job.companyIndustry }}</span>
                  <span v-if="job.companyScale" class="meta-divider">·</span>
                  <span v-if="job.companyScale" class="meta-item">{{ job.companyScale }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <div class="job-card-footer">
            <div class="job-stats">
              <span class="stat">
                <el-icon><View /></el-icon>
                {{ job.viewCount || 0 }}
              </span>
              <span class="stat">
                <el-icon><Document /></el-icon>
                {{ job.applicationCount || 0 }}人投递
              </span>
            </div>
            <el-button type="primary" size="small" class="apply-btn" @click.stop="quickApply(job)">
              立即投递
            </el-button>
          </div>
        </div>
      </div>

      <div v-if="total > searchForm.pageSize" class="pagination-section">
        <el-pagination
          v-model:current-page="searchForm.pageNum"
          v-model:page-size="searchForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="prev, pager, next"
          @size-change="handleSearch"
          @current-change="handleSearch"
          background
        />
      </div>
    </section>

    <section class="companies-section" v-if="hotCompanies.length > 0">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">🏢</span>
          热门企业
        </h2>
      </div>
      <div class="companies-grid">
        <div 
          v-for="(company, index) in hotCompanies" 
          :key="company.id" 
          class="company-card"
          :style="{ animationDelay: `${index * 80}ms` }"
          @click="goToCompanyJobs(company.id)"
        >
          <el-avatar :size="56" :src="company.logo || defaultLogo" class="company-logo">
            {{ company.name?.charAt(0) }}
          </el-avatar>
          <div class="company-content">
            <div class="company-name">{{ company.name }}</div>
            <div class="company-industry">{{ company.industry }}</div>
            <div class="company-jobs">{{ company.jobCount }}个在招职位</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getJobList } from '@/api/job'
import {
  Search, ArrowRight, View, Document, Location, Clock, Reading,
  Briefcase, Coin, User, OfficeBuilding, Trophy, Star
} from '@element-plus/icons-vue'
import { useUserStore, useNavigationStore } from '@/stores'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const navigationStore = useNavigationStore()
const loading = ref(false)
const jobList = ref([])
const total = ref(0)
const defaultLogo = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const hotTags = ['Java', '前端', '产品经理', '数据分析', 'UI设计', 'Python']

const platformStats = [
  { icon: Briefcase, value: '1,000+', label: '在招职位', gradient: 'linear-gradient(135deg, #6366F1, #8B5CF6)' },
  { icon: OfficeBuilding, value: '500+', label: '优质企业', gradient: 'linear-gradient(135deg, #3B82F6, #2563EB)' },
  { icon: User, value: '10,000+', label: '求职者', gradient: 'linear-gradient(135deg, #10B981, #059669)' },
  { icon: Trophy, value: '98%', label: '好评率', gradient: 'linear-gradient(135deg, #F59E0B, #D97706)' }
]

const hotCompanies = ref([
  { id: 1, name: '字节跳动', industry: '互联网', jobCount: 128, logo: '' },
  { id: 2, name: '阿里巴巴', industry: '电商', jobCount: 96, logo: '' },
  { id: 3, name: '腾讯', industry: '互联网', jobCount: 85, logo: '' },
  { id: 4, name: '美团', industry: '本地生活', jobCount: 72, logo: '' }
])

const searchForm = reactive({
  keyword: '',
  category: '',
  location: '',
  experience: null,
  education: '',
  salaryMin: null,
  salaryMax: null,
  pageNum: 1,
  pageSize: 12
})

// 保存页面状态
const savePageState = () => {
  const state = {
    searchForm: { ...searchForm },
    scrollPosition: { x: window.scrollX, y: window.scrollY },
    jobList: jobList.value,
    total: total.value
  }
  navigationStore.savePageState('/home', state)
}

// 恢复页面状态
const restorePageState = () => {
  const state = navigationStore.getPageState('/home')
  if (state && state.searchForm) {
    Object.assign(searchForm, state.searchForm)
    if (state.jobList) {
      jobList.value = state.jobList
      total.value = state.total
    }
    // 恢复滚动位置
    if (state.scrollPosition) {
      setTimeout(() => {
        window.scrollTo(state.scrollPosition.x, state.scrollPosition.y)
      }, 100)
    }
    return true
  }
  return false
}

const fetchJobList = async () => {
  loading.value = true
  try {
    const res = await getJobList(searchForm)
    jobList.value = res.data.records
    total.value = res.data.total
    // 保存状态
    savePageState()
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

const searchByTag = (tag) => {
  searchForm.keyword = tag
  handleSearch()
}

const resetFilters = () => {
  Object.assign(searchForm, {
    keyword: '',
    category: '',
    location: '',
    experience: null,
    education: '',
    salaryMin: null,
    salaryMax: null,
    pageNum: 1
  })
  fetchJobList()
}

const goToDetail = (id) => {
  // 保存当前状态
  savePageState()
  router.push(`/jobs/${id}`)
}

const goToJobList = () => {
  router.push('/jobs')
}

const goToCompanyJobs = (companyId) => {
  router.push(`/jobs?companyId=${companyId}`)
}

const quickApply = (job) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 保存当前状态
  savePageState()
  router.push(`/jobs/${job.id}`)
}

// 监听滚动事件，保存滚动位置
let scrollTimeout = null
const handleScroll = () => {
  if (scrollTimeout) {
    clearTimeout(scrollTimeout)
  }
  scrollTimeout = setTimeout(() => {
    savePageState()
  }, 200)
}

onMounted(() => {
  // 尝试恢复状态，如果没有缓存则重新加载
  const restored = restorePageState()
  if (!restored) {
    fetchJobList()
  }
  // 监听滚动事件
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  if (scrollTimeout) {
    clearTimeout(scrollTimeout)
  }
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100%;
}

.hero-section {
  position: relative;
  padding: 60px 24px 80px;
  overflow: hidden;
  
  .hero-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #4F46E5 0%, #6366F1 50%, #818CF8 100%);
    
    .hero-shape {
      position: absolute;
      border-radius: 50%;
      opacity: 0.1;
      
      &.shape-1 {
        width: 400px;
        height: 400px;
        background: white;
        top: -100px;
        right: -100px;
      }
      
      &.shape-2 {
        width: 300px;
        height: 300px;
        background: white;
        bottom: -50px;
        left: -50px;
      }
      
      &.shape-3 {
        width: 200px;
        height: 200px;
        background: white;
        top: 50%;
        left: 30%;
      }
    }
  }
  
  .hero-content {
    position: relative;
    max-width: 800px;
    margin: 0 auto;
    text-align: center;
    
    .hero-title {
      margin-bottom: 16px;
      
      .title-line {
        display: block;
        font-size: 42px;
        font-weight: 700;
        color: white;
        line-height: 1.3;
        
        &.highlight {
          background: linear-gradient(90deg, #FCD34D, #FBBF24);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
      }
    }
    
    .hero-subtitle {
      font-size: 18px;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 40px;
    }
    
    .hero-search {
      .search-wrapper {
        display: flex;
        gap: 12px;
        padding: 8px;
        background: white;
        border-radius: 16px;
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
        
        .search-input {
          flex: 1;
          
          :deep(.el-input__wrapper) {
            height: 52px;
            background: transparent;
            box-shadow: none;
            padding: 0 16px;
            
            .el-input__inner {
              font-size: 16px;
              
              &::placeholder {
                color: #9CA3AF;
              }
            }
          }
          
          .search-icon {
            font-size: 20px;
            color: #6B7280;
          }
        }
        
        .search-btn {
          height: 52px;
          padding: 0 28px;
          border-radius: 12px;
          font-size: 16px;
          font-weight: 600;
          background: linear-gradient(135deg, #4F46E5, #6366F1);
          border: none;
          display: flex;
          align-items: center;
          gap: 8px;
          
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(79, 70, 229, 0.4);
          }
        }
      }
      
      .hot-search {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        margin-top: 20px;
        
        .hot-label {
          font-size: 14px;
          color: rgba(255, 255, 255, 0.8);
        }
        
        .hot-tags {
          display: flex;
          gap: 8px;
          
          .hot-tag {
            background: rgba(255, 255, 255, 0.2);
            border: none;
            color: white;
            cursor: pointer;
            transition: all 200ms ease;
            
            &:hover {
              background: rgba(255, 255, 255, 0.3);
              transform: translateY(-2px);
            }
          }
        }
      }
    }
  }
}

.stats-section {
  margin-top: -40px;
  position: relative;
  z-index: 10;
  padding: 0 24px;
  
  .stats-container {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    
    .stat-item {
      background: white;
      border-radius: 16px;
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
      animation: slideUp 400ms ease-out backwards;
      
      @for $i from 1 through 4 {
        &:nth-child(#{$i}) {
          animation-delay: #{$i * 100}ms;
        }
      }
      
      .stat-icon {
        width: 52px;
        height: 52px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .el-icon {
          font-size: 24px;
          color: white;
        }
      }
      
      .stat-content {
        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #111827;
          line-height: 1.2;
        }
        
        .stat-label {
          font-size: 14px;
          color: #6B7280;
          margin-top: 4px;
        }
      }
    }
  }
}

.filter-section {
  padding: 32px 24px;
  
  .filter-container {
    max-width: 1200px;
    margin: 0 auto;
    
    .filter-grid {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      align-items: flex-end;
      
      .filter-item {
        display: flex;
        flex-direction: column;
        gap: 8px;
        
        .filter-label {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          font-weight: 500;
          color: #6B7280;
          
          .el-icon {
            font-size: 14px;
            color: #6366F1;
          }
        }
        
        .filter-select {
          width: 160px;
          
          :deep(.el-input__wrapper) {
            height: 40px;
            border-radius: 10px;
            border: 1px solid #E5E7EB;
            box-shadow: none;
            
            &:hover {
              border-color: #6366F1;
            }
            
            &.is-focus {
              border-color: #6366F1;
              box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
            }
          }
        }
        
        &.salary-item {
          .salary-range {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .salary-input {
              width: 90px;
              
              :deep(.el-input__wrapper) {
                height: 40px;
                border-radius: 10px;
                border: 1px solid #E5E7EB;
                box-shadow: none;
                
                &:hover {
                  border-color: #6366F1;
                }
              }
            }
            
            .range-separator {
              color: #9CA3AF;
            }
            
            .salary-unit {
              font-size: 14px;
              color: #6B7280;
              font-weight: 500;
            }
          }
        }
      }
    }
  }
}

.jobs-section {
  padding: 0 24px 40px;
  
  .section-header {
    max-width: 1200px;
    margin: 0 auto 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-left {
      display: flex;
      align-items: baseline;
      gap: 16px;
      
      .section-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 24px;
        font-weight: 700;
        color: #111827;
        margin: 0;
        
        .title-icon {
          font-size: 24px;
        }
      }
      
      .job-count {
        font-size: 14px;
        color: #6B7280;
        
        strong {
          color: #6366F1;
          font-weight: 600;
        }
      }
    }
    
    .view-all-btn {
      font-weight: 500;
      color: #6366F1;
      
      .el-icon {
        margin-left: 4px;
      }
    }
  }
  
  .empty-state {
    max-width: 1200px;
    margin: 0 auto;
    text-align: center;
    padding: 80px 20px;
    background: white;
    border-radius: 20px;
    
    .empty-illustration {
      width: 100px;
      height: 100px;
      margin: 0 auto 24px;
      background: linear-gradient(135deg, #EEF2FF, #E0E7FF);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .empty-icon {
        font-size: 48px;
        color: #6366F1;
      }
    }
    
    h3 {
      font-size: 20px;
      font-weight: 600;
      color: #111827;
      margin-bottom: 8px;
    }
    
    p {
      color: #6B7280;
      margin-bottom: 24px;
    }
  }
  
  .job-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
    
    .job-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      cursor: pointer;
      transition: all 250ms ease;
      border: 1px solid #F3F4F6;
      animation: slideUp 400ms ease-out backwards;
      display: flex;
      flex-direction: column;
      gap: 16px;
      
      &:hover {
        border-color: #C7D2FE;
        box-shadow: 0 12px 32px rgba(99, 102, 241, 0.12);
        transform: translateY(-4px);
        
        .apply-btn {
          opacity: 1;
          transform: translateX(0);
        }
      }
      
      .job-card-header {
        .job-main {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          gap: 12px;
          margin-bottom: 12px;
          
          .job-title {
            font-size: 18px;
            font-weight: 600;
            color: #111827;
            line-height: 1.4;
            margin: 0;
          }
          
          .job-salary {
            font-size: 18px;
            font-weight: 700;
            color: #F97316;
            white-space: nowrap;
          }
        }
        
        .job-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          
          .job-tag {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 4px 10px;
            background: #F3F4F6;
            border-radius: 6px;
            font-size: 12px;
            color: #6B7280;
            
            .el-icon {
              font-size: 12px;
            }
          }
        }
      }
      
      .job-card-body {
        .company-info {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .company-avatar {
            background: linear-gradient(135deg, #EEF2FF, #C7D2FE);
            color: #4F46E5;
            font-weight: 600;
            flex-shrink: 0;
          }
          
          .company-details {
            flex: 1;
            min-width: 0;
            
            .company-name {
              font-size: 14px;
              font-weight: 600;
              color: #111827;
              margin-bottom: 4px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            
            .company-meta {
              font-size: 12px;
              color: #9CA3AF;
              display: flex;
              align-items: center;
              gap: 6px;
              
              .meta-divider {
                color: #D1D5DB;
              }
            }
          }
        }
      }
      
      .job-card-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 16px;
        border-top: 1px solid #F3F4F6;
        
        .job-stats {
          display: flex;
          gap: 16px;
          
          .stat {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            color: #9CA3AF;
            
            .el-icon {
              font-size: 14px;
            }
          }
        }
        
        .apply-btn {
          opacity: 0;
          transform: translateX(10px);
          transition: all 250ms ease;
          border-radius: 8px;
          font-weight: 500;
        }
      }
    }
  }
  
  .pagination-section {
    max-width: 1200px;
    margin: 32px auto 0;
    display: flex;
    justify-content: center;
  }
}

.companies-section {
  padding: 40px 24px;
  background: #F9FAFB;
  
  .section-header {
    max-width: 1200px;
    margin: 0 auto 24px;
    
    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 24px;
      font-weight: 700;
      color: #111827;
      margin: 0;
      
      .title-icon {
        font-size: 24px;
      }
    }
  }
  
  .companies-grid {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    
    .company-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      display: flex;
      align-items: center;
      gap: 16px;
      cursor: pointer;
      transition: all 250ms ease;
      animation: slideUp 400ms ease-out backwards;
      
      &:hover {
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
        transform: translateY(-4px);
      }
      
      .company-logo {
        background: linear-gradient(135deg, #EEF2FF, #C7D2FE);
        color: #4F46E5;
        font-weight: 600;
        flex-shrink: 0;
      }
      
      .company-content {
        flex: 1;
        min-width: 0;
        
        .company-name {
          font-size: 16px;
          font-weight: 600;
          color: #111827;
          margin-bottom: 4px;
        }
        
        .company-industry {
          font-size: 13px;
          color: #6B7280;
          margin-bottom: 4px;
        }
        
        .company-jobs {
          font-size: 12px;
          color: #6366F1;
          font-weight: 500;
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
  .stats-section .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .jobs-section .job-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .companies-section .companies-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 16px 60px;
    
    .hero-content {
      .hero-title .title-line {
        font-size: 28px;
      }
      
      .hero-subtitle {
        font-size: 15px;
      }
      
      .hero-search {
        .search-wrapper {
          flex-direction: column;
          padding: 12px;
          
          .search-btn {
            width: 100%;
          }
        }
      }
    }
  }
  
  .stats-section {
    .stats-container {
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      
      .stat-item {
        padding: 16px;
        
        .stat-icon {
          width: 44px;
          height: 44px;
        }
        
        .stat-content .stat-value {
          font-size: 20px;
        }
      }
    }
  }
  
  .filter-section {
    padding: 20px 16px;
    
    .filter-container .filter-grid {
      .filter-item {
        width: 100%;
        
        .filter-select {
          width: 100%;
        }
        
        &.salary-item .salary-range {
          width: 100%;
          
          .salary-input {
            flex: 1;
          }
        }
      }
    }
  }
  
  .jobs-section {
    padding: 0 16px 32px;
    
    .job-grid {
      grid-template-columns: 1fr;
    }
  }
  
  .companies-section {
    padding: 32px 16px;
    
    .companies-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
