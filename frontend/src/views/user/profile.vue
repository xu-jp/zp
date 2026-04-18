<template>
  <div class="user-profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          <div class="profile-content">
            <div class="avatar-section">
              <el-avatar :size="100" :src="profile.avatar || defaultAvatar">
                <el-icon :size="40"><User /></el-icon>
              </el-avatar>
              <el-upload
                class="avatar-upload"
                action="#"
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                accept="image/*"
              >
                <el-button type="primary" link>更换头像</el-button>
              </el-upload>
            </div>
            <el-descriptions :column="1" border class="profile-info">
              <el-descriptions-item label="用户名">{{ profile.username || '-' }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ profile.realName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ getGenderText(profile.gender) }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ profile.age || '-' }}</el-descriptions-item>
              <el-descriptions-item label="注册时间">{{ formatDate(profile.createTime) }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <span>账号设置</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="个人资料" name="profile">
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" class="settings-form">
                <el-form-item label="姓名" prop="realName">
                  <el-input v-model="profileForm.realName" placeholder="请输入姓名" maxlength="20" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="profileForm.gender">
                    <el-radio :label="0">保密</el-radio>
                    <el-radio :label="1">男</el-radio>
                    <el-radio :label="2">女</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                  <el-input-number v-model="profileForm.age" :min="18" :max="100" placeholder="请输入年龄" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="saveProfile" :loading="profileLoading">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="账号安全" name="security">
              <div class="security-section">
                <div class="security-item">
                  <div class="security-info">
                    <div class="security-title">
                      <el-icon><Lock /></el-icon>
                      <span>登录密码</span>
                    </div>
                    <div class="security-desc">定期更换密码可以提高账号安全性</div>
                  </div>
                  <el-button type="primary" @click="showChangePassword">修改密码</el-button>
                </div>

                <div class="security-item">
                  <div class="security-info">
                    <div class="security-title">
                      <el-icon><Phone /></el-icon>
                      <span>手机绑定</span>
                    </div>
                    <div class="security-desc">
                      {{ profile.phone ? '已绑定手机：' + maskPhone(profile.phone) : '未绑定手机，绑定后可用于找回密码' }}
                    </div>
                  </div>
                  <el-button type="primary" @click="showBindPhone">
                    {{ profile.phone ? '更换手机' : '绑定手机' }}
                  </el-button>
                </div>

                <div class="security-item">
                  <div class="security-info">
                    <div class="security-title">
                      <el-icon><Message /></el-icon>
                      <span>邮箱绑定</span>
                    </div>
                    <div class="security-desc">
                      {{ profile.email ? '已绑定邮箱：' + maskEmail(profile.email) : '未绑定邮箱，绑定后可用于找回密码' }}
                    </div>
                  </div>
                  <el-button type="primary" @click="showBindEmail">
                    {{ profile.email ? '更换邮箱' : '绑定邮箱' }}
                  </el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="450px">
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

    <el-dialog v-model="phoneDialogVisible" :title="profile.phone ? '更换手机号' : '绑定手机号'" width="450px">
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

    <el-dialog v-model="emailDialogVisible" :title="profile.email ? '更换邮箱' : '绑定邮箱'" width="450px">
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
import { User, Lock, Phone, Message } from '@element-plus/icons-vue'
import { getUserProfile, updateUserProfile, changePassword as changePasswordApi, bindPhone as bindPhoneApi, bindEmail as bindEmailApi } from '@/api/userProfile'
import { useUserStore } from '@/stores/user'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const activeTab = ref('profile')
const profile = ref({})
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
  return new Date(date).toLocaleString('zh-CN')
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
  .profile-card {
    .profile-content {
      .avatar-section {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-bottom: 20px;

        .avatar-upload {
          margin-top: 12px;
        }
      }

      .profile-info {
        margin-top: 20px;
      }
    }
  }

  .settings-card {
    .settings-form {
      max-width: 500px;
      padding: 20px 0;
    }

    .security-section {
      padding: 20px 0;

      .security-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px;
        margin-bottom: 16px;
        background: #f5f7fa;
        border-radius: 8px;

        .security-info {
          .security-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 16px;
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;

            .el-icon {
              color: #409eff;
            }
          }

          .security-desc {
            font-size: 14px;
            color: #999;
          }
        }
      }
    }
  }
}
</style>
