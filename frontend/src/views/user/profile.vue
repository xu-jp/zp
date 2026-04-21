<template>
  <div class="user-profile-page">
    <div class="profile-header">
      <div class="header-bg"></div>
      <div class="profile-card">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="profile.avatar || defaultAvatar" class="user-avatar">
              <el-icon :size="40"><User /></el-icon>
            </el-avatar>
            <el-upload
              class="avatar-upload"
              action="#"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              accept="image/*"
            >
              <div class="upload-overlay">
                <el-icon><Camera /></el-icon>
              </div>
            </el-upload>
          </div>
          <div class="user-info">
            <h2 class="user-name">{{ profile.realName || profile.username || '用户' }}</h2>
            <div class="user-meta">
              <span class="meta-item">
                <el-icon><User /></el-icon>
                {{ getGenderText(profile.gender) }}
              </span>
              <span class="meta-item" v-if="profile.age">
                <el-icon><Calendar /></el-icon>
                {{ profile.age }}岁
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>
                {{ formatDate(profile.createTime) }}加入
              </span>
            </div>
          </div>
        </div>
        <div class="quick-stats">
          <div class="stat-item">
            <div class="stat-value">{{ stats.applications }}</div>
            <div class="stat-label">投递职位</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.interviews }}</div>
            <div class="stat-label">面试邀请</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.favorites }}</div>
            <div class="stat-label">收藏职位</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.resumes }}</div>
            <div class="stat-label">我的简历</div>
          </div>
        </div>
      </div>
    </div>

    <div class="profile-content">
      <div class="content-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'profile' }" 
          @click="activeTab = 'profile'"
        >
          <el-icon><EditPen /></el-icon>
          <span>个人资料</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'security' }" 
          @click="activeTab = 'security'"
        >
          <el-icon><Lock /></el-icon>
          <span>账号安全</span>
        </div>
      </div>

      <div class="tab-content" v-show="activeTab === 'profile'">
        <div class="section-card">
          <div class="section-header">
            <h3 class="section-title">基本信息</h3>
          </div>
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" class="profile-form">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="profileForm.realName" placeholder="请输入姓名" maxlength="20">
                    <template #prefix>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender" class="gender-radio">
                    <el-radio :label="0">保密</el-radio>
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="2">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input-number v-model="profileForm.age" :min="18" :max="100" placeholder="请输入年龄" class="full-width" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="profileLoading" class="save-btn">
                <el-icon><Check /></el-icon>
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="tab-content" v-show="activeTab === 'security'">
        <div class="security-list">
          <div class="security-card">
            <div class="security-icon" style="background: linear-gradient(135deg, #6366F1, #8B5CF6);">
              <el-icon><Lock /></el-icon>
            </div>
            <div class="security-info">
              <div class="security-title">登录密码</div>
              <div class="security-desc">定期更换密码可以提高账号安全性</div>
            </div>
            <el-button type="primary" @click="showChangePassword" class="security-btn">修改密码</el-button>
          </div>

          <div class="security-card">
            <div class="security-icon" style="background: linear-gradient(135deg, #10B981, #059669);">
              <el-icon><Phone /></el-icon>
            </div>
            <div class="security-info">
              <div class="security-title">手机绑定</div>
              <div class="security-desc">
                {{ profile.phone ? '已绑定手机：' + maskPhone(profile.phone) : '未绑定手机，绑定后可用于找回密码' }}
              </div>
            </div>
            <el-button type="primary" @click="showBindPhone" class="security-btn">
              {{ profile.phone ? '更换手机' : '绑定手机' }}
            </el-button>
          </div>

          <div class="security-card">
            <div class="security-icon" style="background: linear-gradient(135deg, #F59E0B, #D97706);">
              <el-icon><Message /></el-icon>
            </div>
            <div class="security-info">
              <div class="security-title">邮箱绑定</div>
              <div class="security-desc">
                {{ profile.email ? '已绑定邮箱：' + maskEmail(profile.email) : '未绑定邮箱，绑定后可用于找回密码' }}
              </div>
            </div>
            <el-button type="primary" @click="showBindEmail" class="security-btn">
              {{ profile.email ? '更换邮箱' : '绑定邮箱' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="480px" class="profile-dialog">
      <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="passwordLoading">确认修改</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="phoneDialogVisible" :title="profile.phone ? '更换手机号' : '绑定手机号'" width="480px" class="profile-dialog">
      <el-form ref="phoneFormRef" :model="phoneForm" :rules="phoneRules" label-width="100px">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="phoneForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="phoneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="bindPhone" :loading="phoneLoading">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="emailDialogVisible" :title="profile.email ? '更换邮箱' : '绑定邮箱'" width="480px" class="profile-dialog">
      <el-form ref="emailFormRef" :model="emailForm" :rules="emailRules" label-width="100px">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="emailForm.email" placeholder="请输入邮箱" maxlength="50" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="emailDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="bindEmail" :loading="emailLoading">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, Message, Camera, Calendar, Clock, EditPen, Check } from '@element-plus/icons-vue'
import { getUserProfile, updateUserProfile, changePassword as changePasswordApi, bindPhone as bindPhoneApi, bindEmail as bindEmailApi } from '@/api/userProfile'
import { useUserStore } from '@/stores/user'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const activeTab = ref('profile')
const profile = ref({})
const stats = ref({
  applications: 0,
  interviews: 0,
  favorites: 0,
  resumes: 0
})
const profileLoading = ref(false)
const passwordLoading = ref(false)
const phoneLoading = ref(false)
const emailLoading = ref(false)
const passwordDialogVisible = ref(false)
const phoneDialogVisible = ref(false)
const emailDialogVisible = ref(false)
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const phoneFormRef = ref(null)
const emailFormRef = ref(null)
const userStore = useUserStore()

const profileForm = ref({
  realName: '',
  gender: 0,
  age: null
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneForm = ref({
  phone: ''
})

const emailForm = ref({
  email: ''
})

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  if (!/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) {
    callback(new Error('请输入正确的邮箱'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const profileRules = {
  realName: [{ max: 20, message: '姓名不能超过20个字符', trigger: 'blur' }],
  age: [{ type: 'number', min: 18, max: 100, message: '年龄必须在18-100之间', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ]
}

const emailRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ]
}

const fetchProfile = async () => {
  try {
    const res = await getUserProfile()
    profile.value = res.data || {}
    profileForm.value = {
      realName: profile.value.realName || '',
      gender: profile.value.gender || 0,
      age: profile.value.age || null
    }
  } catch (error) {
    console.error('获取个人信息失败:', error)
  }
}

const saveProfile = async () => {
  try {
    await profileFormRef.value.validate()
  } catch {
    return
  }

  profileLoading.value = true
  try {
    await updateUserProfile(profileForm.value)
    ElMessage.success('保存成功')
    fetchProfile()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    profileLoading.value = false
  }
}

const showChangePassword = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  passwordDialogVisible.value = true
}

const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
  } catch {
    return
  }

  passwordLoading.value = true
  try {
    await changePasswordApi({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '修改失败')
  } finally {
    passwordLoading.value = false
  }
}

const showBindPhone = () => {
  phoneForm.value = {
    phone: ''
  }
  phoneDialogVisible.value = true
}

const bindPhone = async () => {
  try {
    await phoneFormRef.value.validate()
  } catch {
    return
  }

  phoneLoading.value = true
  try {
    await bindPhoneApi(phoneForm.value.phone)
    ElMessage.success('手机号绑定成功')
    phoneDialogVisible.value = false
    fetchProfile()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '绑定失败')
  } finally {
    phoneLoading.value = false
  }
}

const showBindEmail = () => {
  emailForm.value = {
    email: ''
  }
  emailDialogVisible.value = true
}

const bindEmail = async () => {
  try {
    await emailFormRef.value.validate()
  } catch {
    return
  }

  emailLoading.value = true
  try {
    await bindEmailApi(emailForm.value.email)
    ElMessage.success('邮箱绑定成功')
    emailDialogVisible.value = false
    fetchProfile()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '绑定失败')
  } finally {
    emailLoading.value = false
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过2MB')
    return false
  }

  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      await updateUserProfile({ avatar: e.target.result })
      ElMessage.success('头像更新成功')
      const res = await getUserProfile()
      profile.value = res.data || {}
      userStore.updateUserInfo({ avatar: profile.value.avatar })
    } catch (error) {
      ElMessage.error('头像更新失败')
    }
  }
  reader.readAsDataURL(file)
  return false
}

const maskPhone = (phone) => {
  if (!phone || phone.length < 7) return phone
  return phone.substring(0, 3) + '****' + phone.substring(7)
}

const maskEmail = (email) => {
  if (!email) return email
  const [name, domain] = email.split('@')
  if (!domain) return email
  const maskedName = name.length > 2 ? name.substring(0, 2) + '***' : name + '***'
  return maskedName + '@' + domain
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const getGenderText = (gender) => {
  switch (gender) {
    case 1: return '男'
    case 2: return '女'
    default: return '保密'
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style lang="scss" scoped>
.user-profile-page {
  max-width: 900px;
  margin: 0 auto;
}

.profile-header {
  position: relative;
  margin-bottom: 24px;
  
  .header-bg {
    height: 140px;
    background: linear-gradient(135deg, #4F46E5 0%, #6366F1 50%, #818CF8 100%);
    border-radius: 20px 20px 0 0;
  }
  
  .profile-card {
    position: relative;
    margin: -60px 24px 0;
    background: white;
    border-radius: 16px;
    padding: 24px 32px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    
    .avatar-section {
      display: flex;
      align-items: center;
      gap: 24px;
      margin-bottom: 24px;
      
      .avatar-wrapper {
        position: relative;
        
        .user-avatar {
          border: 4px solid white;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          background: linear-gradient(135deg, #EEF2FF, #C7D2FE);
          color: #4F46E5;
        }
        
        .avatar-upload {
          position: absolute;
          bottom: 0;
          right: 0;
          
          .upload-overlay {
            width: 32px;
            height: 32px;
            background: linear-gradient(135deg, #4F46E5, #6366F1);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: transform 200ms ease;
            
            &:hover {
              transform: scale(1.1);
            }
            
            .el-icon {
              font-size: 16px;
              color: white;
            }
          }
        }
      }
      
      .user-info {
        flex: 1;
        
        .user-name {
          font-size: 24px;
          font-weight: 700;
          color: #111827;
          margin: 0 0 8px 0;
        }
        
        .user-meta {
          display: flex;
          gap: 16px;
          
          .meta-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 14px;
            color: #6B7280;
            
            .el-icon {
              font-size: 14px;
              color: #9CA3AF;
            }
          }
        }
      }
    }
    
    .quick-stats {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      padding-top: 24px;
      border-top: 1px solid #F3F4F6;
      
      .stat-item {
        text-align: center;
        
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #111827;
          line-height: 1.2;
        }
        
        .stat-label {
          font-size: 13px;
          color: #6B7280;
          margin-top: 4px;
        }
      }
    }
  }
}

.profile-content {
  padding: 0 24px;
  
  .content-tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 24px;
    
    .tab-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      background: white;
      border-radius: 12px;
      cursor: pointer;
      transition: all 200ms ease;
      font-weight: 500;
      color: #6B7280;
      
      &:hover {
        color: #4F46E5;
        background: #EEF2FF;
      }
      
      &.active {
        background: linear-gradient(135deg, #4F46E5, #6366F1);
        color: white;
      }
    }
  }
  
  .tab-content {
    .section-card {
      background: white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
      
      .section-header {
        margin-bottom: 24px;
        
        .section-title {
          font-size: 18px;
          font-weight: 600;
          color: #111827;
          margin: 0;
        }
      }
      
      .profile-form {
        .full-width {
          width: 100%;
        }
        
        .gender-radio {
          display: flex;
          gap: 24px;
        }
        
        .save-btn {
          padding: 12px 32px;
          border-radius: 10px;
          font-weight: 600;
        }
      }
    }
    
    .security-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
      
      .security-card {
        display: flex;
        align-items: center;
        gap: 20px;
        background: white;
        border-radius: 16px;
        padding: 24px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
        transition: all 200ms ease;
        
        &:hover {
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
          transform: translateY(-2px);
        }
        
        .security-icon {
          width: 52px;
          height: 52px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          
          .el-icon {
            font-size: 24px;
            color: white;
          }
        }
        
        .security-info {
          flex: 1;
          
          .security-title {
            font-size: 16px;
            font-weight: 600;
            color: #111827;
            margin-bottom: 4px;
          }
          
          .security-desc {
            font-size: 14px;
            color: #6B7280;
          }
        }
        
        .security-btn {
          border-radius: 10px;
          font-weight: 500;
        }
      }
    }
  }
}

.profile-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
  }
  
  :deep(.el-dialog__header) {
    padding: 20px 24px;
    border-bottom: 1px solid #F3F4F6;
  }
  
  :deep(.el-dialog__body) {
    padding: 24px;
  }
  
  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid #F3F4F6;
  }
}

@media (max-width: 768px) {
  .profile-header {
    .profile-card {
      margin: -40px 16px 0;
      padding: 20px;
      
      .avatar-section {
        flex-direction: column;
        text-align: center;
        
        .user-info {
          .user-meta {
            flex-wrap: wrap;
            justify-content: center;
          }
        }
      }
      
      .quick-stats {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  }
  
  .profile-content {
    padding: 0 16px;
    
    .content-tabs {
      overflow-x: auto;
      
      .tab-item {
        padding: 10px 16px;
        white-space: nowrap;
      }
    }
  }
}
</style>
