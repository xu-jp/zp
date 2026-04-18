<template>
  <div class="forgot-password-container">
    <div class="forgot-password-box">
      <h2 class="title">找回密码</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="手机号/邮箱" prop="target">
          <el-input v-model="form.target" placeholder="请输入注册时的手机号或邮箱" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-input">
            <el-input v-model="form.code" placeholder="请输入验证码" />
            <el-button :disabled="countdown > 0" :loading="sendingCode" @click="sendCodeHandler">
              {{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleReset" style="width: 100%">
            重置密码
          </el-button>
        </el-form-item>
      </el-form>
      <div class="footer-links">
        <el-link type="primary" @click="$router.push('/login')">返回登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { resetPassword, sendCode } from '@/api/auth'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)

const form = reactive({
  target: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const validateTarget = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号或邮箱'))
    return
  }
  const phoneRegex = /^1[3-9]\d{9}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!phoneRegex.test(value) && !emailRegex.test(value)) {
    callback(new Error('请输入正确的手机号或邮箱'))
    return
  }
  callback()
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  target: [{ required: true, validator: validateTarget, trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const sendCodeHandler = async () => {
  if (!form.target) {
    ElMessage.warning('请先输入手机号或邮箱')
    return
  }

  const phoneRegex = /^1[3-9]\d{9}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!phoneRegex.test(form.target) && !emailRegex.test(form.target)) {
    ElMessage.warning('请输入正确的手机号或邮箱')
    return
  }

  sendingCode.value = true
  try {
    await sendCode({ target: form.target, type: 2 })
    ElMessage.success('验证码已发送，请查看控制台日志')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    console.error(error)
  } finally {
    sendingCode.value = false
  }
}

const handleReset = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await resetPassword({
      target: form.target,
      code: form.code,
      password: form.password
    })
    ElMessage.success('密码重置成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('Reset password failed:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.forgot-password-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .forgot-password-box {
    width: 480px;
    padding: 40px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);

    .title {
      text-align: center;
      margin-bottom: 30px;
      color: #333;
    }

    .code-input {
      display: flex;
      gap: 10px;

      .el-input {
        flex: 1;
      }
    }

    .footer-links {
      text-align: center;
      margin-top: 20px;
    }
  }
}
</style>
