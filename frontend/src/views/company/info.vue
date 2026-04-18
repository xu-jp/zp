<template>
  <div class="company-info-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业信息</span>
          <el-tag v-if="companyInfo" :type="getAuditStatusType(companyInfo.auditStatus)">
            {{ companyInfo.auditStatusText }}
          </el-tag>
        </div>
      </template>

      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-width="120px"
        v-loading="loading"
      >
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="企业名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入企业名称" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="企业Logo">
              <el-upload
                class="logo-uploader"
                action="#"
                :show-file-list="false"
                :before-upload="beforeLogoUpload"
                accept="image/*"
              >
                <img v-if="form.logo" :src="form.logo" class="logo-preview" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属行业" prop="industry">
              <el-select v-model="form.industry" placeholder="请选择行业" style="width: 100%">
                <el-option label="互联网/IT" value="互联网/IT" />
                <el-option label="金融" value="金融" />
                <el-option label="教育" value="教育" />
                <el-option label="医疗健康" value="医疗健康" />
                <el-option label="制造业" value="制造业" />
                <el-option label="房地产" value="房地产" />
                <el-option label="电子商务" value="电子商务" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业规模" prop="scale">
              <el-select v-model="form.scale" placeholder="请选择规模" style="width: 100%">
                <el-option label="0-20人" value="0-20人" />
                <el-option label="20-99人" value="20-99人" />
                <el-option label="100-499人" value="100-499人" />
                <el-option label="500-999人" value="500-999人" />
                <el-option label="1000人以上" value="1000人以上" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="企业地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入企业地址" maxlength="200" />
        </el-form-item>

        <el-form-item label="企业简介" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入企业简介"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-divider content-position="left">资质信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="营业执照号" prop="businessLicense">
              <el-input v-model="form.businessLicense" placeholder="请输入营业执照号" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalPerson">
              <el-input v-model="form.legalPerson" placeholder="请输入法定代表人" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">联系方式</el-divider>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item v-if="companyInfo && companyInfo.auditStatus === 2">
          <el-alert 
            :title="'审核拒绝原因：' + companyInfo.auditRemark" 
            type="error" 
            :closable="false"
            show-icon
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saveLoading">保存信息</el-button>
          <el-button 
            type="success" 
            @click="handleSubmitAudit" 
            :loading="auditLoading"
            :disabled="!canSubmitAudit"
          >
            提交审核
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCompanyInfo, saveCompanyInfo, submitAudit } from '@/api/company'

const formRef = ref(null)
const loading = ref(false)
const saveLoading = ref(false)
const auditLoading = ref(false)
const companyInfo = ref(null)

const form = ref({
  name: '',
  logo: '',
  industry: '',
  scale: '',
  address: '',
  description: '',
  businessLicense: '',
  legalPerson: '',
  contactPhone: '',
  contactEmail: ''
})

const rules = {
  name: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择所属行业', trigger: 'change' }],
  address: [{ required: true, message: '请输入企业地址', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  businessLicense: [{ required: true, message: '请输入营业执照号', trigger: 'blur' }]
}

const canSubmitAudit = computed(() => {
  return form.value.name && form.value.address && 
         form.value.contactPhone && form.value.businessLicense &&
         (!companyInfo.value || companyInfo.value.auditStatus !== 1)
})

const fetchCompanyInfo = async () => {
  loading.value = true
  try {
    const res = await getCompanyInfo()
    if (res.data) {
      companyInfo.value = res.data
      form.value = {
        name: res.data.name || '',
        logo: res.data.logo || '',
        industry: res.data.industry || '',
        scale: res.data.scale || '',
        address: res.data.address || '',
        description: res.data.description || '',
        businessLicense: res.data.businessLicense || '',
        legalPerson: res.data.legalPerson || '',
        contactPhone: res.data.contactPhone || '',
        contactEmail: res.data.contactEmail || ''
      }
    }
  } catch (error) {
    console.error('获取企业信息失败:', error)
  } finally {
    loading.value = false
  }
}

const beforeLogoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    form.value.logo = e.target.result
  }
  reader.readAsDataURL(file)
  return false
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saveLoading.value = true
  try {
    await saveCompanyInfo(form.value)
    ElMessage.success('保存成功')
    fetchCompanyInfo()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    saveLoading.value = false
  }
}

const handleSubmitAudit = async () => {
  try {
    await ElMessageBox.confirm('确认提交审核吗？提交后将等待管理员审核。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  auditLoading.value = true
  try {
    await submitAudit()
    ElMessage.success('提交审核成功，请等待管理员审核')
    fetchCompanyInfo()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '提交审核失败')
  } finally {
    auditLoading.value = false
  }
}

const getAuditStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'danger'
    default: return 'info'
  }
}

onMounted(() => {
  fetchCompanyInfo()
})
</script>

<style lang="scss" scoped>
.company-info-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .logo-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      width: 100px;
      height: 100px;
      display: flex;
      align-items: center;
      justify-content: center;

      &:hover {
        border-color: #409eff;
      }
    }

    .logo-preview {
      width: 100px;
      height: 100px;
      object-fit: cover;
    }

    .logo-uploader-icon {
      font-size: 28px;
      color: #8c939d;
    }
  }
}
</style>
