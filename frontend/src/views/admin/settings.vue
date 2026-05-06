<template>
  <div class="admin-settings-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
          <el-tag type="info" size="small">修改后全局生效</el-tag>
        </div>
      </template>

      <el-form :model="settings" label-width="120px" v-loading="loading">
        <el-form-item label="平台名称">
          <div class="form-item-content">
            <el-input v-model="settings.platformName" placeholder="请输入平台名称" />
            <el-checkbox v-model="settings.platformNamePublic" label="对所有用户可见" />
          </div>
        </el-form-item>
        <el-form-item label="平台Logo">
          <div class="form-item-content">
            <el-upload
              class="logo-uploader"
              :show-file-list="false"
              :http-request="handleLogoUpload"
            >
              <el-image 
                v-if="settings.logo" 
                :src="settings.logo" 
                style="width: 120px; height: 40px"
                fit="contain"
              />
              <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <el-checkbox v-model="settings.logoPublic" label="对所有用户可见" />
          </div>
        </el-form-item>
        <el-form-item label="客服电话">
          <div class="form-item-content">
            <el-input v-model="settings.servicePhone" placeholder="请输入客服电话" />
            <el-checkbox v-model="settings.servicePhonePublic" label="对所有用户可见" />
          </div>
        </el-form-item>
        <el-form-item label="客服邮箱">
          <div class="form-item-content">
            <el-input v-model="settings.serviceEmail" placeholder="请输入客服邮箱" />
            <el-checkbox v-model="settings.serviceEmailPublic" label="对所有用户可见" />
          </div>
        </el-form-item>
        <el-form-item label="客服微信">
          <div class="form-item-content">
            <el-input v-model="settings.serviceWechat" placeholder="请输入客服微信" />
            <el-checkbox v-model="settings.serviceWechatPublic" label="对所有用户可见" />
          </div>
        </el-form-item>
        <el-form-item label="平台简介">
          <div class="form-item-content">
            <el-input 
              v-model="settings.description" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入平台简介"
            />
            <el-checkbox v-model="settings.descriptionPublic" label="对所有用户可见" />
          </div>
        </el-form-item>
        <el-form-item label="ICP备案号">
          <div class="form-item-content">
            <el-input v-model="settings.icpNumber" placeholder="请输入ICP备案号" />
            <el-checkbox v-model="settings.icpNumberPublic" label="对所有用户可见" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllSettings, updateSettings } from '@/api/systemSetting'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const saving = ref(false)
const settingsList = ref([])

const settings = ref({
  platformName: '',
  platformNamePublic: true,
  logo: '',
  logoPublic: true,
  servicePhone: '',
  servicePhonePublic: true,
  serviceEmail: '',
  serviceEmailPublic: true,
  serviceWechat: '',
  serviceWechatPublic: true,
  description: '',
  descriptionPublic: false,
  icpNumber: '',
  icpNumberPublic: false
})

const fetchSettings = async () => {
  loading.value = true
  try {
    const res = await getAllSettings()
    if (res.data) {
      settingsList.value = res.data
      res.data.forEach(item => {
        if (item.settingKey === 'platformName') {
          settings.value.platformName = item.settingValue || ''
          settings.value.platformNamePublic = item.isPublic === 1
        }
        if (item.settingKey === 'logo') {
          settings.value.logo = item.settingValue || ''
          settings.value.logoPublic = item.isPublic === 1
        }
        if (item.settingKey === 'servicePhone') {
          settings.value.servicePhone = item.settingValue || ''
          settings.value.servicePhonePublic = item.isPublic === 1
        }
        if (item.settingKey === 'serviceEmail') {
          settings.value.serviceEmail = item.settingValue || ''
          settings.value.serviceEmailPublic = item.isPublic === 1
        }
        if (item.settingKey === 'serviceWechat') {
          settings.value.serviceWechat = item.settingValue || ''
          settings.value.serviceWechatPublic = item.isPublic === 1
        }
        if (item.settingKey === 'description') {
          settings.value.description = item.settingValue || ''
          settings.value.descriptionPublic = item.isPublic === 1
        }
        if (item.settingKey === 'icpNumber') {
          settings.value.icpNumber = item.settingValue || ''
          settings.value.icpNumberPublic = item.isPublic === 1
        }
      })
    }
  } catch (error) {
    console.error('获取设置失败:', error)
  } finally {
    loading.value = false
  }
}

const handleLogoUpload = async (options) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await uploadFile(formData)
    settings.value.logo = res.data?.url || ''
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }
}

const handleSave = async () => {
  saving.value = true
  try {
    const updateList = [
      { id: settingsList.value.find(s => s.settingKey === 'platformName')?.id, settingValue: settings.value.platformName, isPublic: settings.value.platformNamePublic ? 1 : 0 },
      { id: settingsList.value.find(s => s.settingKey === 'logo')?.id, settingValue: settings.value.logo, isPublic: settings.value.logoPublic ? 1 : 0 },
      { id: settingsList.value.find(s => s.settingKey === 'servicePhone')?.id, settingValue: settings.value.servicePhone, isPublic: settings.value.servicePhonePublic ? 1 : 0 },
      { id: settingsList.value.find(s => s.settingKey === 'serviceEmail')?.id, settingValue: settings.value.serviceEmail, isPublic: settings.value.serviceEmailPublic ? 1 : 0 },
      { id: settingsList.value.find(s => s.settingKey === 'serviceWechat')?.id, settingValue: settings.value.serviceWechat, isPublic: settings.value.serviceWechatPublic ? 1 : 0 },
      { id: settingsList.value.find(s => s.settingKey === 'description')?.id, settingValue: settings.value.description, isPublic: settings.value.descriptionPublic ? 1 : 0 },
      { id: settingsList.value.find(s => s.settingKey === 'icpNumber')?.id, settingValue: settings.value.icpNumber, isPublic: settings.value.icpNumberPublic ? 1 : 0 }
    ].filter(item => item.id)
    
    await updateSettings(updateList)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchSettings()
})
</script>

<style lang="scss" scoped>
.admin-settings-page {
  max-width: 800px;

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .form-item-content {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;
  }

  .logo-uploader {
    :deep(.el-upload) {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: border-color 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }

    .logo-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 120px;
      height: 40px;
      text-align: center;
      line-height: 40px;
    }
  }
}
</style>
