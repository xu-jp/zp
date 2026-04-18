<template>
  <div class="login-page">
    <div class="left-section">
      <div class="brand">
        <div class="brand-icon">
          <el-icon><Star /></el-icon>
        </div>
        <span class="brand-name">智能招聘系统</span>
      </div>

      <div class="characters-container">
        <div class="character purple" ref="purpleRef" :style="purpleStyle">
          <div class="eyes" :style="purpleEyesStyle">
            <EyeBall 
              :size="18" 
              :pupilSize="7" 
              :maxDistance="5" 
              :isBlinking="isPurpleBlinking"
              :forceLookX="purpleForceLook.x"
              :forceLookY="purpleForceLook.y"
            />
            <EyeBall 
              :size="18" 
              :pupilSize="7" 
              :maxDistance="5" 
              :isBlinking="isPurpleBlinking"
              :forceLookX="purpleForceLook.x"
              :forceLookY="purpleForceLook.y"
            />
          </div>
        </div>

        <div class="character black" ref="blackRef" :style="blackStyle">
          <div class="eyes" :style="blackEyesStyle">
            <EyeBall 
              :size="16" 
              :pupilSize="6" 
              :maxDistance="4" 
              :isBlinking="isBlackBlinking"
              :forceLookX="blackForceLook.x"
              :forceLookY="blackForceLook.y"
            />
            <EyeBall 
              :size="16" 
              :pupilSize="6" 
              :maxDistance="4" 
              :isBlinking="isBlackBlinking"
              :forceLookX="blackForceLook.x"
              :forceLookY="blackForceLook.y"
            />
          </div>
        </div>

        <div class="character orange" ref="orangeRef" :style="orangeStyle">
          <div class="eyes pupils-only" :style="orangeEyesStyle">
            <Pupil :size="12" :maxDistance="5" :forceLookX="orangeForceLook.x" :forceLookY="orangeForceLook.y" />
            <Pupil :size="12" :maxDistance="5" :forceLookX="orangeForceLook.x" :forceLookY="orangeForceLook.y" />
          </div>
        </div>

        <div class="character yellow" ref="yellowRef" :style="yellowStyle">
          <div class="eyes pupils-only" :style="yellowEyesStyle">
            <Pupil :size="12" :maxDistance="5" :forceLookX="yellowForceLook.x" :forceLookY="yellowForceLook.y" />
            <Pupil :size="12" :maxDistance="5" :forceLookX="yellowForceLook.x" :forceLookY="yellowForceLook.y" />
          </div>
          <div class="mouth" :style="yellowMouthStyle"></div>
        </div>
      </div>

      <div class="footer-links">
        <a href="#">隐私政策</a>
        <a href="#">服务条款</a>
        <a href="#">联系我们</a>
      </div>

      <div class="decorative-grid"></div>
      <div class="decorative-circle circle-1"></div>
      <div class="decorative-circle circle-2"></div>
    </div>

    <div class="right-section">
      <div class="form-container">
        <div class="mobile-brand">
          <div class="brand-icon mobile">
            <el-icon><Star /></el-icon>
          </div>
          <span>智能招聘系统</span>
        </div>

        <transition name="fade-slide" mode="out-in">
          <div key="login" v-if="currentForm === 'login'" class="form-panel">
            <div class="form-header">
              <h1>欢迎回来</h1>
              <p>请登录您的账户继续使用</p>
            </div>

            <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="auth-form">
              <el-form-item prop="username">
                <label class="form-label">用户名</label>
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="请输入用户名" 
                  size="large"
                  @focus="handleInputFocus"
                  @blur="handleInputBlur"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="password">
                <label class="form-label">密码</label>
                <el-input 
                  v-model="loginForm.password" 
                  :type="showPassword ? 'text' : 'password'" 
                  placeholder="请输入密码" 
                  size="large"
                  @focus="handleInputFocus"
                  @blur="handleInputBlur"
                  @input="handlePasswordInput"
                  @keyup.enter="handleLogin"
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                  <template #suffix>
                    <el-icon class="password-toggle" @click="showPassword = !showPassword">
                      <View v-if="!showPassword" />
                      <Hide v-else />
                    </el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <el-link type="primary" @click="switchForm('forgotPassword')">忘记密码？</el-link>
              </div>

              <el-form-item v-if="errorMessage" class="error-message">
                <el-alert :title="errorMessage" type="error" show-icon :closable="false" />
              </el-form-item>

              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  :loading="loading" 
                  class="submit-button"
                  @click="handleLogin"
                >
                  {{ loading ? '登录中...' : '登录' }}
                </el-button>
              </el-form-item>
            </el-form>

            <div class="divider">
              <span>或者</span>
            </div>

            <div class="social-login">
              <el-button class="social-btn" circle>
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M12 2C6.477 2 2 6.477 2 12c0 4.42 2.865 8.166 6.839 9.489.5.092.682-.217.682-.482 0-.237-.008-.866-.013-1.7-2.782.604-3.369-1.341-3.369-1.341-.454-1.155-1.11-1.462-1.11-1.462-.908-.62.069-.608.069-.608 1.003.07 1.531 1.03 1.531 1.03.892 1.529 2.341 1.087 2.91.831.092-.646.35-1.086.636-1.336-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.253-.446-1.27.098-2.647 0 0 .84-.269 2.75 1.025A9.578 9.578 0 0112 6.836c.85.004 1.705.114 2.504.336 1.909-1.294 2.747-1.025 2.747-1.025.546 1.377.203 2.394.1 2.647.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48C19.138 20.163 22 16.418 22 12c0-5.523-4.477-10-10-10z"/>
                </svg>
              </el-button>
              <el-button class="social-btn" circle>
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                  <path fill="currentColor" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                  <path fill="currentColor" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                  <path fill="currentColor" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                </svg>
              </el-button>
              <el-button class="social-btn" circle>
                <svg viewBox="0 0 24 24" width="20" height="20">
                  <path fill="currentColor" d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/>
                </svg>
              </el-button>
            </div>

            <div class="switch-link">
              <span>还没有账号？</span>
              <el-link type="primary" @click="switchForm('register')">立即注册</el-link>
            </div>
          </div>

          <div key="register" v-else-if="currentForm === 'register'" class="form-panel">
            <div class="form-header">
              <h1>注册账号</h1>
              <p>创建您的账户开始使用</p>
            </div>

            <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="auth-form">
              <el-form-item prop="userType">
                <label class="form-label">用户类型</label>
                <el-radio-group v-model="registerForm.userType" size="large" class="radio-group">
                  <el-radio-button :value="1">求职者</el-radio-button>
                  <el-radio-button :value="2">招聘者</el-radio-button>
                </el-radio-group>
              </el-form-item>

              <el-form-item prop="username">
                <label class="form-label">用户名</label>
                <el-input 
                  v-model="registerForm.username" 
                  placeholder="请输入用户名" 
                  size="large"
                  @blur="checkUsername"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="password">
                <label class="form-label">密码</label>
                <el-input 
                  v-model="registerForm.password" 
                  type="password" 
                  placeholder="请输入密码" 
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <label class="form-label">确认密码</label>
                <el-input 
                  v-model="registerForm.confirmPassword" 
                  type="password" 
                  placeholder="请确认密码" 
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="realName">
                <label class="form-label">真实姓名</label>
                <el-input 
                  v-model="registerForm.realName" 
                  placeholder="请输入真实姓名" 
                  size="large"
                >
                  <template #prefix>
                    <el-icon><UserFilled /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="target">
                <label class="form-label">手机号/邮箱</label>
                <el-input 
                  v-model="registerForm.target" 
                  placeholder="请输入手机号或邮箱" 
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="code">
                <label class="form-label">验证码</label>
                <div class="code-input">
                  <el-input 
                    v-model="registerForm.code" 
                    placeholder="请输入验证码" 
                    size="large"
                  >
                    <template #prefix>
                      <el-icon><Key /></el-icon>
                    </template>
                  </el-input>
                  <el-button 
                    size="large" 
                    :disabled="registerCountdown > 0" 
                    :loading="sendingCode"
                    @click="sendRegisterCode"
                    class="code-button"
                  >
                    {{ registerCountdown > 0 ? `${registerCountdown}s后重试` : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  :loading="loading" 
                  class="submit-button"
                  @click="handleRegister"
                >
                  {{ loading ? '注册中...' : '注册' }}
                </el-button>
              </el-form-item>
            </el-form>

            <div class="switch-link">
              <span>已有账号？</span>
              <el-link type="primary" @click="switchForm('login')">立即登录</el-link>
            </div>
          </div>

          <div key="forgotPassword" v-else-if="currentForm === 'forgotPassword'" class="form-panel">
            <div class="form-header">
              <h1>找回密码</h1>
              <p>重置您的账户密码</p>
            </div>

            <el-form ref="forgotFormRef" :model="forgotForm" :rules="forgotRules" class="auth-form">
              <el-form-item prop="target">
                <label class="form-label">手机号/邮箱</label>
                <el-input 
                  v-model="forgotForm.target" 
                  placeholder="请输入注册时的手机号或邮箱" 
                  size="large"
                >
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="code">
                <label class="form-label">验证码</label>
                <div class="code-input">
                  <el-input 
                    v-model="forgotForm.code" 
                    placeholder="请输入验证码" 
                    size="large"
                  >
                    <template #prefix>
                      <el-icon><Key /></el-icon>
                    </template>
                  </el-input>
                  <el-button 
                    size="large" 
                    :disabled="forgotCountdown > 0" 
                    :loading="sendingCode"
                    @click="sendForgotCode"
                    class="code-button"
                  >
                    {{ forgotCountdown > 0 ? `${forgotCountdown}s后重试` : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>

              <el-form-item prop="password">
                <label class="form-label">新密码</label>
                <el-input 
                  v-model="forgotForm.password" 
                  type="password" 
                  placeholder="请输入新密码" 
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <label class="form-label">确认密码</label>
                <el-input 
                  v-model="forgotForm.confirmPassword" 
                  type="password" 
                  placeholder="请确认新密码" 
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button 
                  type="primary" 
                  size="large" 
                  :loading="loading" 
                  class="submit-button"
                  @click="handleResetPassword"
                >
                  {{ loading ? '重置中...' : '重置密码' }}
                </el-button>
              </el-form-item>
            </el-form>

            <div class="switch-link">
              <el-link type="primary" @click="switchForm('login')">返回登录</el-link>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Star, User, Lock, View, Hide, UserFilled, Message, Key } from '@element-plus/icons-vue'
import { register, sendCode, checkUsername as checkUsernameApi, resetPassword } from '@/api/auth'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

const currentForm = ref('login')
const loading = ref(false)
const showPassword = ref(false)
const rememberMe = ref(false)
const errorMessage = ref('')
const isTyping = ref(false)
const sendingCode = ref(false)
const registerCountdown = ref(0)
const forgotCountdown = ref(0)

const loginFormRef = ref(null)
const registerFormRef = ref(null)
const forgotFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  target: '',
  code: '',
  userType: 1
})

const forgotForm = reactive({
  target: '',
  code: '',
  password: '',
  confirmPassword: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const validateUsername = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
    return
  }
  if (value.length < 3 || value.length > 20) {
    callback(new Error('用户名长度为3-20个字符'))
    return
  }
  callback()
}

const validateRegisterConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateForgotConfirmPassword = (rule, value, callback) => {
  if (value !== forgotForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

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

const registerRules = {
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }],
  username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateRegisterConfirmPassword, trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  target: [{ required: true, validator: validateTarget, trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const forgotRules = {
  target: [{ required: true, validator: validateTarget, trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateForgotConfirmPassword, trigger: 'blur' }
  ]
}

const switchForm = (formType) => {
  currentForm.value = formType
  errorMessage.value = ''
}

const checkUsername = async () => {
  if (!registerForm.username || registerForm.username.length < 3) return
  try {
    const res = await checkUsernameApi(registerForm.username)
    if (!res.data) {
      ElMessage.warning('用户名已存在')
    }
  } catch (error) {
    console.error(error)
  }
}

const startCountdown = (type) => {
  const countdown = type === 'register' ? registerCountdown : forgotCountdown
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const sendRegisterCode = async () => {
  if (!registerForm.target) {
    ElMessage.warning('请先输入手机号或邮箱')
    return
  }
  
  const phoneRegex = /^1[3-9]\d{9}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!phoneRegex.test(registerForm.target) && !emailRegex.test(registerForm.target)) {
    ElMessage.warning('请输入正确的手机号或邮箱')
    return
  }

  sendingCode.value = true
  try {
    await sendCode({ target: registerForm.target, type: 1 })
    ElMessage.success('验证码已发送')
    startCountdown('register')
  } catch (error) {
    console.error(error)
  } finally {
    sendingCode.value = false
  }
}

const sendForgotCode = async () => {
  if (!forgotForm.target) {
    ElMessage.warning('请先输入手机号或邮箱')
    return
  }

  const phoneRegex = /^1[3-9]\d{9}$/
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!phoneRegex.test(forgotForm.target) && !emailRegex.test(forgotForm.target)) {
    ElMessage.warning('请输入正确的手机号或邮箱')
    return
  }

  sendingCode.value = true
  try {
    await sendCode({ target: forgotForm.target, type: 2 })
    ElMessage.success('验证码已发送，请查看控制台日志')
    startCountdown('forgot')
  } catch (error) {
    console.error(error)
  } finally {
    sendingCode.value = false
  }
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  errorMessage.value = ''
  
  try {
    await userStore.loginAction(loginForm)
    ElMessage.success('登录成功')
    
    const redirect = route.query.redirect || '/home'
    router.push(redirect)
  } catch (error) {
    console.error('Login failed:', error)
    errorMessage.value = '用户名或密码错误，请重试'
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const registerData = {
      username: registerForm.username,
      password: registerForm.password,
      realName: registerForm.realName,
      target: registerForm.target,
      code: registerForm.code,
      userType: registerForm.userType
    }
    
    if (/^1[3-9]\d{9}$/.test(registerForm.target)) {
      registerData.phone = registerForm.target
    } else {
      registerData.email = registerForm.target
    }
    
    await register(registerData)
    ElMessage.success('注册成功，请登录')
    switchForm('login')
    loginForm.username = registerForm.username
  } catch (error) {
    console.error('Register failed:', error)
  } finally {
    loading.value = false
  }
}

const handleResetPassword = async () => {
  const valid = await forgotFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await resetPassword({
      target: forgotForm.target,
      code: forgotForm.code,
      password: forgotForm.password
    })
    ElMessage.success('密码重置成功，请登录')
    switchForm('login')
  } catch (error) {
    console.error('Reset password failed:', error)
  } finally {
    loading.value = false
  }
}

const mouseX = ref(0)
const mouseY = ref(0)
const isPurpleBlinking = ref(false)
const isBlackBlinking = ref(false)
const isLookingAtEachOther = ref(false)
const isPurplePeeking = ref(false)

const purpleRef = ref(null)
const blackRef = ref(null)
const orangeRef = ref(null)
const yellowRef = ref(null)

const handleMouseMove = (e) => {
  mouseX.value = e.clientX
  mouseY.value = e.clientY
}

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove)
  schedulePurpleBlink()
  scheduleBlackBlink()
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
})

const schedulePurpleBlink = () => {
  const timeout = setTimeout(() => {
    isPurpleBlinking.value = true
    setTimeout(() => {
      isPurpleBlinking.value = false
      schedulePurpleBlink()
    }, 150)
  }, Math.random() * 4000 + 3000)
  
  return timeout
}

const scheduleBlackBlink = () => {
  const timeout = setTimeout(() => {
    isBlackBlinking.value = true
    setTimeout(() => {
      isBlackBlinking.value = false
      scheduleBlackBlink()
    }, 150)
  }, Math.random() * 4000 + 3000)
  
  return timeout
}

const calculatePosition = (refValue) => {
  if (!refValue) return { faceX: 0, faceY: 0, bodySkew: 0 }
  
  const rect = refValue.getBoundingClientRect()
  const centerX = rect.left + rect.width / 2
  const centerY = rect.top + rect.height / 3
  
  const deltaX = mouseX.value - centerX
  const deltaY = mouseY.value - centerY
  
  const faceX = Math.max(-15, Math.min(15, deltaX / 20))
  const faceY = Math.max(-10, Math.min(10, deltaY / 30))
  const bodySkew = Math.max(-6, Math.min(6, -deltaX / 120))
  
  return { faceX, faceY, bodySkew }
}

const purplePos = computed(() => calculatePosition(purpleRef.value))
const blackPos = computed(() => calculatePosition(blackRef.value))
const orangePos = computed(() => calculatePosition(orangeRef.value))
const yellowPos = computed(() => calculatePosition(yellowRef.value))

const isPasswordVisible = computed(() => loginForm.password.length > 0 && showPassword.value)
const isPasswordHidden = computed(() => loginForm.password.length > 0 && !showPassword.value)

const purpleStyle = computed(() => ({
  height: (isTyping.value || isPasswordHidden.value) ? '440px' : '400px',
  transform: isPasswordVisible.value
    ? 'skewX(0deg)'
    : (isTyping.value || isPasswordHidden.value)
      ? `skewX(${(purplePos.value.bodySkew || 0) - 12}deg) translateX(40px)`
      : `skewX(${purplePos.value.bodySkew || 0}deg)`
}))

const purpleEyesStyle = computed(() => ({
  left: isPasswordVisible.value ? '20px' : isLookingAtEachOther.value ? '55px' : `${45 + purplePos.value.faceX}px`,
  top: isPasswordVisible.value ? '35px' : isLookingAtEachOther.value ? '65px' : `${40 + purplePos.value.faceY}px`
}))

const purpleForceLook = computed(() => ({
  x: isPasswordVisible.value ? (isPurplePeeking.value ? 4 : -4) : isLookingAtEachOther.value ? 3 : undefined,
  y: isPasswordVisible.value ? (isPurplePeeking.value ? 5 : -4) : isLookingAtEachOther.value ? 4 : undefined
}))

const blackStyle = computed(() => ({
  transform: isPasswordVisible.value
    ? 'skewX(0deg)'
    : isLookingAtEachOther.value
      ? `skewX(${(blackPos.value.bodySkew || 0) * 1.5 + 10}deg) translateX(20px)`
      : (isTyping.value || isPasswordHidden.value)
        ? `skewX(${(blackPos.value.bodySkew || 0) * 1.5}deg)`
        : `skewX(${blackPos.value.bodySkew || 0}deg)`
}))

const blackEyesStyle = computed(() => ({
  left: isPasswordVisible.value ? '10px' : isLookingAtEachOther.value ? '32px' : `${26 + blackPos.value.faceX}px`,
  top: isPasswordVisible.value ? '28px' : isLookingAtEachOther.value ? '12px' : `${32 + blackPos.value.faceY}px`
}))

const blackForceLook = computed(() => ({
  x: isPasswordVisible.value ? -4 : isLookingAtEachOther.value ? 0 : undefined,
  y: isPasswordVisible.value ? -4 : isLookingAtEachOther.value ? -4 : undefined
}))

const orangeStyle = computed(() => ({
  transform: isPasswordVisible.value ? 'skewX(0deg)' : `skewX(${orangePos.value.bodySkew || 0}deg)`
}))

const orangeEyesStyle = computed(() => ({
  left: isPasswordVisible.value ? '50px' : `${82 + (orangePos.value.faceX || 0)}px`,
  top: isPasswordVisible.value ? '85px' : `${90 + (orangePos.value.faceY || 0)}px`
}))

const orangeForceLook = computed(() => ({
  x: isPasswordVisible.value ? -5 : undefined,
  y: isPasswordVisible.value ? -4 : undefined
}))

const yellowStyle = computed(() => ({
  transform: isPasswordVisible.value ? 'skewX(0deg)' : `skewX(${yellowPos.value.bodySkew || 0}deg)`
}))

const yellowEyesStyle = computed(() => ({
  left: isPasswordVisible.value ? '20px' : `${52 + (yellowPos.value.faceX || 0)}px`,
  top: isPasswordVisible.value ? '35px' : `${40 + (yellowPos.value.faceY || 0)}px`
}))

const yellowMouthStyle = computed(() => ({
  left: isPasswordVisible.value ? '10px' : `${40 + (yellowPos.value.faceX || 0)}px`,
  top: isPasswordVisible.value ? '88px' : `${88 + (yellowPos.value.faceY || 0)}px`
}))

const yellowForceLook = computed(() => ({
  x: isPasswordVisible.value ? -5 : undefined,
  y: isPasswordVisible.value ? -4 : undefined
}))

watch(isTyping, (newVal) => {
  if (newVal) {
    isLookingAtEachOther.value = true
    setTimeout(() => {
      isLookingAtEachOther.value = false
    }, 800)
  } else {
    isLookingAtEachOther.value = false
  }
})

watch([() => loginForm.password, showPassword, isPurplePeeking], () => {
  if (loginForm.password.length > 0 && showPassword.value) {
    const schedulePeek = () => {
      const peekInterval = setTimeout(() => {
        isPurplePeeking.value = true
        setTimeout(() => {
          isPurplePeeking.value = false
        }, 800)
      }, Math.random() * 3000 + 2000)
      return peekInterval
    }
    
    const firstPeek = schedulePeek()
    return () => clearTimeout(firstPeek)
  } else {
    isPurplePeeking.value = false
  }
})

const handleInputFocus = () => {
  isTyping.value = true
}

const handleInputBlur = () => {
  isTyping.value = false
}

const handlePasswordInput = () => {
  isTyping.value = true
}

const Pupil = {
  name: 'Pupil',
  props: {
    size: { type: Number, default: 12 },
    maxDistance: { type: Number, default: 5 },
    pupilColor: { type: String, default: '#2D2D2D' },
    forceLookX: { type: Number, default: undefined },
    forceLookY: { type: Number, default: undefined }
  },
  setup(props) {
    const mouseX = ref(0)
    const mouseY = ref(0)
    const pupilRef = ref(null)

    const handleMouseMove = (e) => {
      mouseX.value = e.clientX
      mouseY.value = e.clientY
    }

    onMounted(() => {
      window.addEventListener('mousemove', handleMouseMove)
    })

    onUnmounted(() => {
      window.removeEventListener('mousemove', handleMouseMove)
    })

    const pupilPosition = computed(() => {
      if (!pupilRef.value) return { x: 0, y: 0 }
      
      if (props.forceLookX !== undefined && props.forceLookY !== undefined) {
        return { x: props.forceLookX, y: props.forceLookY }
      }

      const pupil = pupilRef.value.getBoundingClientRect()
      const pupilCenterX = pupil.left + pupil.width / 2
      const pupilCenterY = pupil.top + pupil.height / 2

      const deltaX = mouseX.value - pupilCenterX
      const deltaY = mouseY.value - pupilCenterY
      const distance = Math.min(Math.sqrt(deltaX ** 2 + deltaY ** 2), props.maxDistance)

      const angle = Math.atan2(deltaY, deltaX)
      const x = Math.cos(angle) * distance
      const y = Math.sin(angle) * distance

      return { x, y }
    })

    return () => h('div', {
      ref: pupilRef,
      class: 'pupil',
      style: {
        width: `${props.size}px`,
        height: `${props.size}px`,
        backgroundColor: props.pupilColor,
        transform: `translate(${pupilPosition.value.x}px, ${pupilPosition.value.y}px)`
      }
    })
  }
}

const EyeBall = {
  name: 'EyeBall',
  props: {
    size: { type: Number, default: 48 },
    pupilSize: { type: Number, default: 16 },
    maxDistance: { type: Number, default: 10 },
    eyeColor: { type: String, default: 'white' },
    pupilColor: { type: String, default: '#2D2D2D' },
    isBlinking: { type: Boolean, default: false },
    forceLookX: { type: Number, default: undefined },
    forceLookY: { type: Number, default: undefined }
  },
  setup(props) {
    const mouseX = ref(0)
    const mouseY = ref(0)
    const eyeRef = ref(null)

    const handleMouseMove = (e) => {
      mouseX.value = e.clientX
      mouseY.value = e.clientY
    }

    onMounted(() => {
      window.addEventListener('mousemove', handleMouseMove)
    })

    onUnmounted(() => {
      window.removeEventListener('mousemove', handleMouseMove)
    })

    const pupilPosition = computed(() => {
      if (!eyeRef.value) return { x: 0, y: 0 }
      
      if (props.forceLookX !== undefined && props.forceLookY !== undefined) {
        return { x: props.forceLookX, y: props.forceLookY }
      }

      const eye = eyeRef.value.getBoundingClientRect()
      const eyeCenterX = eye.left + eye.width / 2
      const eyeCenterY = eye.top + eye.height / 2

      const deltaX = mouseX.value - eyeCenterX
      const deltaY = mouseY.value - eyeCenterY
      const distance = Math.min(Math.sqrt(deltaX ** 2 + deltaY ** 2), props.maxDistance)

      const angle = Math.atan2(deltaY, deltaX)
      const x = Math.cos(angle) * distance
      const y = Math.sin(angle) * distance

      return { x, y }
    })

    return () => h('div', {
      ref: eyeRef,
      class: 'eyeball',
      style: {
        width: `${props.size}px`,
        height: props.isBlinking ? '2px' : `${props.size}px`,
        backgroundColor: props.eyeColor
      }
    }, !props.isBlinking ? [
      h('div', {
        class: 'pupil',
        style: {
          width: `${props.pupilSize}px`,
          height: `${props.pupilSize}px`,
          backgroundColor: props.pupilColor,
          transform: `translate(${pupilPosition.value.x}px, ${pupilPosition.value.y}px)`
        }
      })
    ] : [])
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 1fr;
  
  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.left-section {
  position: relative;
  display: none;
  flex-direction: column;
  justify-content: space-between;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #a855f7 100%);
  padding: 48px;
  color: white;
  overflow: hidden;
  
  @media (min-width: 1024px) {
    display: flex;
  }
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  z-index: 20;
  
  .brand-icon {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.characters-container {
  position: relative;
  width: 550px;
  height: 400px;
  margin: 0 auto;
  z-index: 20;
}

.character {
  position: absolute;
  bottom: 0;
  transition: all 0.7s ease-in-out;
  transform-origin: bottom center;
  
  &.purple {
    left: 70px;
    width: 180px;
    height: 400px;
    background-color: #6C3FF5;
    border-radius: 10px 10px 0 0;
    z-index: 1;
  }
  
  &.black {
    left: 240px;
    width: 120px;
    height: 310px;
    background-color: #2D2D2D;
    border-radius: 8px 8px 0 0;
    z-index: 2;
  }
  
  &.orange {
    left: 0;
    width: 240px;
    height: 200px;
    background-color: #FF9B6B;
    border-radius: 120px 120px 0 0;
    z-index: 3;
  }
  
  &.yellow {
    left: 310px;
    width: 140px;
    height: 230px;
    background-color: #E8D754;
    border-radius: 70px 70px 0 0;
    z-index: 4;
  }
}

.eyes {
  position: absolute;
  display: flex;
  gap: 8px;
  transition: all 0.7s ease-in-out;
  
  &.pupils-only {
    gap: 8px;
  }
}

.eyeball {
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: height 0.15s ease-out;
  overflow: hidden;
}

.pupil {
  border-radius: 50%;
  transition: transform 0.1s ease-out;
}

.mouth {
  position: absolute;
  width: 80px;
  height: 4px;
  background-color: #2D2D2D;
  border-radius: 2px;
  transition: all 0.2s ease-out;
}

.footer-links {
  display: flex;
  align-items: center;
  gap: 32px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  z-index: 20;
  
  a {
    color: inherit;
    text-decoration: none;
    transition: color 0.2s;
    
    &:hover {
      color: white;
    }
  }
}

.decorative-grid {
  position: absolute;
  inset: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}

.decorative-circle {
  position: absolute;
  border-radius: 50%;
  
  &.circle-1 {
    top: 25%;
    right: 25%;
    width: 256px;
    height: 256px;
    background: rgba(255, 255, 255, 0.1);
    filter: blur(64px);
  }
  
  &.circle-2 {
    bottom: 25%;
    left: 25%;
    width: 384px;
    height: 384px;
    background: rgba(255, 255, 255, 0.05);
    filter: blur(64px);
  }
}

.right-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  background: #fff;
  overflow: hidden;
}

.form-container {
  width: 100%;
  max-width: 420px;
}

.form-panel {
  width: 100%;
}

.mobile-brand {
  display: none;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 48px;
  
  @media (max-width: 1024px) {
    display: flex;
  }
  
  .brand-icon.mobile {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    background: rgba(99, 102, 241, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6366f1;
  }
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
  
  h1 {
    font-size: 28px;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 8px;
  }
  
  p {
    color: #6b7280;
    font-size: 14px;
  }
}

.auth-form {
  .form-label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    color: #374151;
    margin-bottom: 6px;
  }
  
  :deep(.el-input__wrapper) {
    padding: 4px 12px;
    border-radius: 8px;
  }
  
  :deep(.el-input__inner) {
    height: 44px;
  }
  
  .radio-group {
    width: 100%;
    
    :deep(.el-radio-button__inner) {
      width: 100%;
    }
  }
}

.code-input {
  display: flex;
  gap: 16px;
  align-items: center;
  max-width: 400px;
  margin: 0 auto;
  
  .el-input {
    flex: 1;
  }
  
  .code-button {
    flex-shrink: 0;
    width: 130px;
    height: 44px;
    border-radius: 8px;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
    border: none;
    color: white;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
    }
    
    &:disabled {
      background: #e5e7eb;
      color: #9ca3af;
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }
    
    :deep(.el-loading-spinner) {
      color: white;
    }
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.error-message {
  :deep(.el-alert) {
    padding: 8px 12px;
  }
}

.submit-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  margin-top: 8px;
}

.divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
  
  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: #e5e7eb;
  }
  
  span {
    padding: 0 16px;
    color: #9ca3af;
    font-size: 14px;
  }
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 24px;
  
  .social-btn {
    width: 48px;
    height: 48px;
    border: 1px solid #e5e7eb;
    color: #6b7280;
    
    &:hover {
      border-color: #6366f1;
      color: #6366f1;
    }
  }
}

.switch-link {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
  margin-top: 24px;
  
  .el-link {
    font-size: 14px;
    margin-left: 4px;
  }
}

.password-toggle {
  cursor: pointer;
  color: #9ca3af;
  
  &:hover {
    color: #6366f1;
  }
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
