<template>
  <div class="admin-settings-page">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>

      <el-form :model="settings" label-width="120px" v-loading="loading">
        <el-form-item label="平台名称">
          <el-input v-model="settings.platformName" placeholder="请输入平台名称" />
        </el-form-item>
        <el-form-item label="平台Logo">
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
        </el-form-item>
        <el-form-item label="客服电话">
          <el-input v-model="settings.servicePhone" placeholder="请输入客服电话" />
        </el-form-item>
        <el-form-item label="客服邮箱">
          <el-input v-model="settings.serviceEmail" placeholder="请输入客服邮箱" />
        </el-form-item>
        <el-form-item label="客服微信">
          <el-input v-model="settings.serviceWechat" placeholder="请输入客服微信" />
        </el-form-item>
        <el-form-item label="平台简介">
          <el-input 
            v-model="settings.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入平台简介"
          />
        </el-form-item>
        <el-form-item label="ICP备案号">
          <el-input v-model="settings.icpNumber" placeholder="请输入ICP备案号" />
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
  logo: '',
  servicePhone: '',
  serviceEmail: '',
  serviceWechat: '',
  description: '',
  icpNumber: ''
})

const fetchSettings = async () => {
  loading.value = true
  try {
    const res = await getAllSettings()
    if (res.data) {
      settingsList.value = res.data
      res.data.forEach(item => {
        if (item.settingKey === 'platformName') settings.value.platformName = item.settingValue || ''
        if (item.settingKey === 'logo') settings.value.logo = item.settingValue || ''
        if (item.settingKey === 'servicePhone') settings.value.servicePhone = item.settingValue || ''
        if (item.settingKey === 'serviceEmail') settings.value.serviceEmail = item.settingValue || ''
        if (item.settingKey === 'serviceWechat') settings.value.serviceWechat = item.settingValue || ''
        if (item.settingKey === 'description') settings.value.description = item.settingValue || ''
        if (item.settingKey === 'icpNumber') settings.value.icpNumber = item.settingValue || ''
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
      { id: settingsList.value.find(s => s.settingKey === 'platformName')?.id, settingValue: settings.value.platformName },
      { id: settingsList.value.find(s => s.settingKey === 'logo')?.id, settingValue: settings.value.logo },
      { id: settingsList.value.find(s => s.settingKey === 'servicePhone')?.id, settingValue: settings.value.servicePhone },
      { id: settingsList.value.find(s => s.settingKey === 'serviceEmail')?.id, settingValue: settings.value.serviceEmail },
      { id: settingsList.value.find(s => s.settingKey === 'serviceWechat')?.id, settingValue: settings.value.serviceWechat },
      { id: settingsList.value.find(s => s.settingKey === 'description')?.id, settingValue: settings.value.description },
      { id: settingsList.value.find(s => s.settingKey === 'icpNumber')?.id, settingValue: settings.value.icpNumber }
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
