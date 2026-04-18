<template>
  <div class="resume-page">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>我的简历</span>
          <el-button type="primary" @click="handleCreate">新增简历</el-button>
        </div>
      </template>

      <div v-if="resumeList.length === 0" class="empty-state">
        <el-empty description="暂无简历，请先创建">
          <el-button type="primary" @click="handleCreate">创建简历</el-button>
        </el-empty>
      </div>

      <div v-else class="resume-list">
        <div v-for="resume in resumeList" :key="resume.id" class="resume-item">
          <div class="resume-info">
            <div class="resume-title">
              <span>{{ resume.title }}</span>
              <el-tag v-if="resume.isDefault === 1" type="success" size="small">默认</el-tag>
            </div>
            <div class="resume-meta">
              <span>{{ resume.realName }}</span>
              <span class="separator">|</span>
              <span>{{ resume.education }}</span>
              <span class="separator">|</span>
              <span>{{ resume.location }}</span>
              <span class="separator">|</span>
              <span>{{ resume.school }}</span>
            </div>
            <div class="resume-skills">
              <el-tag v-for="skill in (resume.skills || '').split(',').filter(s => s)" :key="skill" size="small" type="info" class="skill-tag">
                {{ skill.trim() }}
              </el-tag>
            </div>
          </div>
          <div class="resume-actions">
            <el-button type="primary" link @click="handleEdit(resume)">编辑</el-button>
            <el-button type="primary" link @click="handleOptimize(resume)">AI优化</el-button>
            <el-button v-if="resume.isDefault !== 1" type="primary" link @click="handleSetDefault(resume)">设为默认</el-button>
            <el-button type="danger" link @click="handleDelete(resume)">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑简历' : '新增简历'" width="800px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="简历标题" prop="title">
          <el-input v-model="form.title" placeholder="如：Java开发工程师简历" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在城市" prop="location">
              <el-input v-model="form.location" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历" prop="education">
              <el-select v-model="form.education" style="width: 100%">
                <el-option label="大专" value="大专" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="毕业日期" prop="graduationDate">
              <el-date-picker v-model="form.graduationDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择毕业日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学校" prop="school">
              <el-input v-model="form.school" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="form.major" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="技能特长" prop="skills">
          <el-input v-model="form.skills" placeholder="如：Java, Spring, MySQL (用逗号分隔)" />
        </el-form-item>
        <el-form-item label="工作经历" prop="workExperience">
          <el-input v-model="form.workExperience" type="textarea" :rows="4" placeholder="请按时间倒序描述工作经历" />
        </el-form-item>
        <el-form-item label="项目经验" prop="projectExperience">
          <el-input v-model="form.projectExperience" type="textarea" :rows="4" placeholder="请描述参与的项目及个人贡献" />
        </el-form-item>
        <el-form-item label="自我介绍" prop="selfIntroduction">
          <el-input v-model="form.selfIntroduction" type="textarea" :rows="3" placeholder="简要介绍自己的优势和工作风格" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="optimizeDialogVisible" title="AI简历优化建议" width="700px" destroy-on-close>
      <div class="optimize-content">
        <div v-if="optimizeLoading" class="optimize-loading">
          <div class="loading-animation">
            <div class="loading-spinner"></div>
            <div class="loading-text">
              <p class="main-text">AI优化生成中，请等待...</p>
              <p class="sub-text">正在分析您的简历，预计需要10-30秒</p>
            </div>
          </div>
          <div v-if="streamContent" class="stream-preview">
            <div class="preview-header">实时生成内容：</div>
            <div class="preview-content">{{ streamContent }}</div>
          </div>
        </div>
        <div v-else-if="optimizeResult" class="optimize-result">
          <div class="overall-score">
            <div class="score-circle">
              <span class="score">{{ optimizeResult.overallScore }}</span>
            </div>
            <div class="summary">{{ optimizeResult.summary }}</div>
          </div>
          <el-divider />
          <div class="suggestions">
            <div v-for="(suggestion, index) in optimizeResult.suggestions" :key="index" class="suggestion-item">
              <div class="suggestion-header">
                <span class="dimension">{{ suggestion.dimension }}</span>
                <el-tag :type="getScoreType(suggestion.score)">{{ suggestion.level }}</el-tag>
                <span class="score-text">{{ suggestion.score }}分</span>
              </div>
              <div class="suggestion-desc">{{ suggestion.description }}</div>
              <div class="suggestion-list">
                <div v-for="(item, idx) in suggestion.suggestions" :key="idx" class="suggestion-item-text">
                  {{ idx + 1 }}. {{ item }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getResumeList, createResume, updateResume, deleteResume, setDefaultResume, optimizeResumeStream } from '@/api/resume'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const resumeList = ref([])
const dialogVisible = ref(false)
const optimizeDialogVisible = ref(false)
const optimizeLoading = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const optimizeResult = ref(null)
const streamContent = ref('')

const form = reactive({
  id: null,
  title: '',
  realName: '',
  gender: '',
  birthday: '',
  phone: '',
  email: '',
  location: '',
  education: '',
  school: '',
  major: '',
  graduationDate: '',
  skills: '',
  workExperience: '',
  projectExperience: '',
  selfIntroduction: '',
  isDefault: 0
})

const rules = {
  title: [{ required: true, message: '请输入简历标题', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const fetchResumeList = async () => {
  loading.value = true
  try {
    const res = await getResumeList()
    resumeList.value = res.data || []
  } catch (error) {
    console.error('Failed to fetch resume list:', error)
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    title: '',
    realName: '',
    gender: '',
    birthday: '',
    phone: '',
    email: '',
    location: '',
    education: '',
    school: '',
    major: '',
    graduationDate: '',
    skills: '',
    workExperience: '',
    projectExperience: '',
    selfIntroduction: '',
    isDefault: 0
  })
  dialogVisible.value = true
}

const handleEdit = (resume) => {
  isEdit.value = true
  Object.assign(form, {
    id: resume.id,
    title: resume.title,
    realName: resume.realName,
    gender: resume.gender,
    birthday: resume.birthday,
    phone: resume.phone,
    email: resume.email,
    location: resume.location,
    education: resume.education,
    school: resume.school,
    major: resume.major,
    graduationDate: resume.graduationDate,
    skills: resume.skills,
    workExperience: resume.workExperience,
    projectExperience: resume.projectExperience,
    selfIntroduction: resume.selfIntroduction,
    isDefault: resume.isDefault
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateResume(form)
      ElMessage.success('简历更新成功')
    } else {
      await createResume(form)
      ElMessage.success('简历创建成功')
    }
    dialogVisible.value = false
    fetchResumeList()
  } catch (error) {
    console.error('Failed to save resume:', error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (resume) => {
  try {
    await ElMessageBox.confirm('确定要删除这份简历吗？', '提示', {
      type: 'warning'
    })
    await deleteResume(resume.id)
    ElMessage.success('删除成功')
    fetchResumeList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete resume:', error)
    }
  }
}

const handleSetDefault = async (resume) => {
  try {
    await setDefaultResume(resume.id)
    ElMessage.success('已设为默认简历')
    fetchResumeList()
  } catch (error) {
    console.error('Failed to set default resume:', error)
  }
}

const handleOptimize = async (resume) => {
  optimizeDialogVisible.value = true
  optimizeLoading.value = true
  optimizeResult.value = null
  streamContent.value = ''
  
  optimizeResumeStream(
    resume.id,
    (chunk, fullContent) => {
      streamContent.value = fullContent
    },
    (fullContent) => {
      optimizeLoading.value = false
      console.log('[AI优化] 收到完整内容, 长度:', fullContent.length)
      console.log('[AI优化] 内容预览:', fullContent.substring(0, 200))
      try {
        let jsonStr = fullContent
        if (fullContent.includes('```json')) {
          jsonStr = fullContent.substring(fullContent.indexOf('{'), fullContent.lastIndexOf('}') + 1)
        } else if (fullContent.includes('{')) {
          jsonStr = fullContent.substring(fullContent.indexOf('{'), fullContent.lastIndexOf('}') + 1)
        }
        console.log('[AI优化] 提取的JSON字符串:', jsonStr.substring(0, 200))
        optimizeResult.value = JSON.parse(jsonStr)
        console.log('[AI优化] 解析成功:', optimizeResult.value)
      } catch (e) {
        console.error('[AI优化] 解析AI响应失败:', e)
        console.error('[AI优化] 原始内容:', fullContent)
        ElMessage.error('AI响应解析失败，请重试')
      }
    },
    (error) => {
      optimizeLoading.value = false
      console.error('AI优化失败:', error)
      ElMessage.error('AI优化失败，请检查网络连接')
    }
  )
}

const getScoreType = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

onMounted(() => {
  fetchResumeList()
})
</script>

<style lang="scss" scoped>
.resume-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .resume-list {
    .resume-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .resume-info {
        flex: 1;

        .resume-title {
          display: flex;
          align-items: center;
          gap: 10px;
          font-size: 16px;
          font-weight: 600;
          color: #333;
          margin-bottom: 8px;
        }

        .resume-meta {
          font-size: 14px;
          color: #666;
          margin-bottom: 8px;

          .separator {
            margin: 0 8px;
            color: #ddd;
          }
        }

        .resume-skills {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;

          .skill-tag {
            margin: 0;
          }
        }
      }

      .resume-actions {
        display: flex;
        gap: 8px;
      }
    }
  }

  .optimize-content {
    min-height: 300px;

    .optimize-loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px;

      .loading-animation {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 20px;

        .loading-spinner {
          width: 60px;
          height: 60px;
          border: 4px solid #f3f3f3;
          border-top: 4px solid #667eea;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }

        .loading-text {
          text-align: center;

          .main-text {
            font-size: 18px;
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
          }

          .sub-text {
            font-size: 14px;
            color: #999;
          }
        }
      }

      .stream-preview {
        width: 100%;
        margin-top: 30px;
        padding: 16px;
        background: #f9fafc;
        border-radius: 8px;
        max-height: 200px;
        overflow-y: auto;

        .preview-header {
          font-size: 14px;
          font-weight: 600;
          color: #666;
          margin-bottom: 8px;
        }

        .preview-content {
          font-size: 13px;
          color: #333;
          line-height: 1.6;
          white-space: pre-wrap;
          word-break: break-all;
        }
      }
    }

    .overall-score {
      display: flex;
      align-items: center;
      gap: 20px;
      margin-bottom: 20px;

      .score-circle {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;

        .score {
          font-size: 24px;
          font-weight: 600;
          color: #fff;
        }
      }

      .summary {
        flex: 1;
        font-size: 14px;
        color: #666;
        line-height: 1.6;
      }
    }

    .suggestions {
      .suggestion-item {
        margin-bottom: 20px;
        padding: 16px;
        background: #f9fafc;
        border-radius: 8px;

        .suggestion-header {
          display: flex;
          align-items: center;
          gap: 10px;
          margin-bottom: 10px;

          .dimension {
            font-size: 16px;
            font-weight: 600;
            color: #333;
          }

          .score-text {
            margin-left: auto;
            font-size: 14px;
            color: #666;
          }
        }

        .suggestion-desc {
          font-size: 14px;
          color: #666;
          margin-bottom: 10px;
        }

        .suggestion-list {
          .suggestion-item-text {
            font-size: 13px;
            color: #333;
            line-height: 1.8;
            padding-left: 10px;
            border-left: 2px solid #667eea;
            margin-bottom: 6px;
          }
        }
      }
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
