<template>
  <div class="favorites-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的收藏</span>
          <span class="total-count">共 {{ total }} 个收藏</span>
        </div>
      </template>

      <div class="favorite-list" v-loading="loading">
        <div 
          v-for="item in favoriteList" 
          :key="item.id" 
          class="favorite-item"
        >
          <div class="job-info" @click="goJobDetail(item.jobId)">
            <div class="job-header">
              <span class="job-title">{{ item.jobTitle }}</span>
              <el-tag v-if="item.jobStatus === 0" type="danger" size="small">已下架</el-tag>
            </div>
            <div class="company-info">
              <el-avatar :size="24" :src="item.companyLogo">
                {{ item.companyName?.charAt(0) }}
              </el-avatar>
              <span class="company-name">{{ item.companyName }}</span>
            </div>
            <div class="job-tags">
              <el-tag size="small" type="info">{{ item.location }}</el-tag>
              <el-tag size="small" type="success">{{ item.salaryRange }}</el-tag>
              <el-tag size="small">{{ item.experience }}</el-tag>
              <el-tag size="small" type="warning">{{ item.education }}</el-tag>
            </div>
            <div class="remark-info" v-if="item.remark">
              <el-icon><Edit /></el-icon>
              <span>{{ item.remark }}</span>
            </div>
          </div>
          <div class="actions">
            <el-button type="primary" link @click="openRemarkDialog(item)">编辑备注</el-button>
            <el-button type="danger" link @click="handleRemove(item)">取消收藏</el-button>
          </div>
        </div>

        <el-empty v-if="!loading && favoriteList.length === 0" description="暂无收藏职位" />
      </div>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchFavorites"
          @current-change="fetchFavorites"
        />
      </div>
    </el-card>

    <el-dialog v-model="remarkVisible" title="编辑备注" width="400px">
      <el-input
        v-model="remarkForm.remark"
        type="textarea"
        :rows="4"
        placeholder="请输入备注信息"
        maxlength="200"
        show-word-limit
      />
      <template #footer>
        <el-button @click="remarkVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRemark" :loading="remarkLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getFavoriteList, removeFavorite, updateFavoriteRemark } from '@/api/favorite'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const favoriteList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const remarkVisible = ref(false)
const remarkLoading = ref(false)
const remarkForm = reactive({
  id: null,
  remark: ''
})

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    favoriteList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    loading.value = false
  }
}

const goJobDetail = (jobId) => {
  router.push(`/jobs/${jobId}`)
}

const openRemarkDialog = (item) => {
  remarkForm.id = item.id
  remarkForm.remark = item.remark || ''
  remarkVisible.value = true
}

const saveRemark = async () => {
  remarkLoading.value = true
  try {
    await updateFavoriteRemark(remarkForm.id, remarkForm.remark)
    ElMessage.success('备注保存成功')
    remarkVisible.value = false
    fetchFavorites()
  } catch (error) {
    console.error('保存备注失败:', error)
  } finally {
    remarkLoading.value = false
  }
}

const handleRemove = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏该职位吗？', '提示', {
      type: 'warning'
    })
    await removeFavorite(item.jobId)
    ElMessage.success('取消收藏成功')
    fetchFavorites()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
    }
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
.favorites-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .total-count {
      color: #999;
      font-size: 14px;
    }
  }

  .favorite-list {
    .favorite-item {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      padding: 16px;
      border-bottom: 1px solid #eee;
      transition: all 0.3s;

      &:hover {
        background: #f9fafc;
      }

      &:last-child {
        border-bottom: none;
      }

      .job-info {
        flex: 1;
        cursor: pointer;

        .job-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;

          .job-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;

            &:hover {
              color: #409eff;
            }
          }
        }

        .company-info {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;

          .company-name {
            color: #666;
            font-size: 14px;
          }
        }

        .job-tags {
          display: flex;
          gap: 8px;
          flex-wrap: wrap;
          margin-bottom: 8px;
        }

        .remark-info {
          display: flex;
          align-items: center;
          gap: 4px;
          color: #999;
          font-size: 13px;
          margin-top: 8px;
        }
      }

      .actions {
        display: flex;
        flex-direction: column;
        gap: 8px;
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
