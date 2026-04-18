import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginNew.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/login-old',
    name: 'LoginOld',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/forgot-password/index.vue'),
    meta: { title: '找回密码', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: (to) => {
      const token = localStorage.getItem('token')
      return token ? '/home' : '/login'
    },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', requiresAuth: false }
      },
      {
        path: 'jobs',
        name: 'Jobs',
        component: () => import('@/views/jobs/index.vue'),
        meta: { title: '职位列表', requiresAuth: false }
      },
      {
        path: 'jobs/:id',
        name: 'JobDetail',
        component: () => import('@/views/jobs/detail.vue'),
        meta: { title: '职位详情', requiresAuth: false }
      },
      {
        path: 'user',
        name: 'UserCenter',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '个人中心', requiresAuth: true, roles: [1] },
        children: [
          {
            path: 'profile',
            name: 'UserProfile',
            component: () => import('@/views/user/profile.vue'),
            meta: { title: '个人资料', requiresAuth: true, roles: [1] }
          },
          {
            path: 'resume',
            name: 'ResumeManage',
            component: () => import('@/views/user/resume.vue'),
            meta: { title: '简历管理', requiresAuth: true, roles: [1] }
          },
          {
            path: 'applications',
            name: 'Applications',
            component: () => import('@/views/user/applications.vue'),
            meta: { title: '我的投递', requiresAuth: true, roles: [1] }
          },
          {
            path: 'favorites',
            name: 'Favorites',
            component: () => import('@/views/user/favorites.vue'),
            meta: { title: '我的收藏', requiresAuth: true, roles: [1] }
          },
          {
            path: 'interviews',
            name: 'Interviews',
            component: () => import('@/views/user/interviews.vue'),
            meta: { title: '面试管理', requiresAuth: true, roles: [1] }
          }
        ]
      },
      {
        path: 'company',
        name: 'CompanyCenter',
        component: () => import('@/views/company/index.vue'),
        meta: { title: '企业中心', requiresAuth: true, roles: [2] },
        children: [
          {
            path: 'info',
            name: 'CompanyInfo',
            component: () => import('@/views/company/info.vue'),
            meta: { title: '企业信息', requiresAuth: true, roles: [2] }
          },
          {
            path: 'jobs',
            name: 'CompanyJobs',
            component: () => import('@/views/company/jobs.vue'),
            meta: { title: '职位管理', requiresAuth: true, roles: [2] }
          },
          {
            path: 'applications',
            name: 'CompanyApplications',
            component: () => import('@/views/company/applications.vue'),
            meta: { title: '投递管理', requiresAuth: true, roles: [2] }
          },
          {
            path: 'interviews',
            name: 'CompanyInterviews',
            component: () => import('@/views/company/interviews.vue'),
            meta: { title: '面试管理', requiresAuth: true, roles: [2] }
          },
          {
            path: 'statistics',
            name: 'CompanyStatistics',
            component: () => import('@/views/company/statistics.vue'),
            meta: { title: '数据统计', requiresAuth: true, roles: [2] }
          },
          {
            path: 'profile',
            name: 'CompanyProfile',
            component: () => import('@/views/company/profile.vue'),
            meta: { title: '个人中心', requiresAuth: true, roles: [2] }
          }
        ]
      },
      {
        path: 'admin',
        name: 'AdminCenter',
        component: () => import('@/views/admin/index.vue'),
        meta: { title: '管理后台', requiresAuth: true, roles: [3] },
        children: [
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/users.vue'),
            meta: { title: '用户管理', requiresAuth: true, roles: [3] }
          },
          {
            path: 'companies',
            name: 'AdminCompanies',
            component: () => import('@/views/admin/companies.vue'),
            meta: { title: '企业管理', requiresAuth: true, roles: [3] }
          },
          {
            path: 'jobs',
            name: 'AdminJobs',
            component: () => import('@/views/admin/jobs.vue'),
            meta: { title: '职位审核', requiresAuth: true, roles: [3] }
          },
          {
            path: 'statistics',
            name: 'AdminStatistics',
            component: () => import('@/views/admin/statistics.vue'),
            meta: { title: '数据统计', requiresAuth: true, roles: [3] }
          },
          {
            path: 'settings',
            name: 'AdminSettings',
            component: () => import('@/views/admin/settings.vue'),
            meta: { title: '系统设置', requiresAuth: true, roles: [3] }
          },
          {
            path: 'logs',
            name: 'AdminLogs',
            component: () => import('@/views/admin/logs.vue'),
            meta: { title: '操作日志', requiresAuth: true, roles: [3] }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 智能招聘系统` : '智能招聘系统'

  const token = localStorage.getItem('token')
  const userInfo = localStorage.getItem('userInfo')
  const userType = userInfo ? JSON.parse(userInfo).userType : null

  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  if (to.meta.roles && to.meta.roles.length > 0) {
    if (!userType || !to.meta.roles.includes(userType)) {
      next({ name: 'Home' })
      return
    }
  }

  next()
})

export default router
