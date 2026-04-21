<template>
  <div class="main-layout">
    <el-container>
      <el-aside :width="isCollapse ? '72px' : '240px'" class="aside">
        <div class="logo" @click="$router.push('/home')">
          <div class="logo-icon" :class="{ collapsed: isCollapse }">
            <el-icon><House /></el-icon>
          </div>
          <transition name="fade">
            <span v-if="!isCollapse" class="logo-text">{{ centerName }}</span>
          </transition>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="side-menu"
        >
          <template v-for="item in menuList" :key="item.path">
            <el-menu-item v-if="!item.children" :index="item.path" class="menu-item-wrapper">
              <el-icon><component :is="item.icon" /></el-icon>
              <template #title>{{ item.title }}</template>
            </el-menu-item>
            <el-sub-menu v-else :index="item.path" class="menu-item-wrapper">
              <template #title>
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.title }}</span>
              </template>
              <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
                {{ child.title }}
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>

        <div class="collapse-btn" @click="isCollapse = !isCollapse">
          <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
        </div>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-button
              v-if="showBackButton"
              class="back-button"
              @click="handleBack"
              :icon="ArrowLeft"
              text
            >
              返回
            </el-button>
          </div>
          
          <div class="header-right">
            <template v-if="userStore.isLoggedIn">
              <el-dropdown @command="handleCommand" trigger="click" class="user-dropdown">
                <div class="user-info">
                  <el-avatar :size="38" :src="userStore.userInfo.avatar" class="user-avatar">
                    {{ userStore.username?.charAt(0)?.toUpperCase() }}
                  </el-avatar>
                  <div class="user-details">
                    <span class="username">{{ userStore.userInfo.realName || userStore.username }}</span>
                    <span class="user-role">{{ getRoleName(userStore.userType) }}</span>
                  </div>
                  <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>
                      <span>系统中心</span>
                    </el-dropdown-item>
                    <el-dropdown-item command="logout" divided>
                      <el-icon><SwitchButton /></el-icon>
                      <span>退出登录</span>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button type="primary" @click="$router.push('/login')" class="login-btn">
                登录
              </el-button>
              <el-button @click="$router.push('/register')" class="register-btn">
                注册
              </el-button>
            </template>
          </div>
        </el-header>
        
        <el-main class="main">
          <router-view v-slot="{ Component }">
            <transition name="page-fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useUserStore, useNavigationStore } from '@/stores'
import { useRouter, useRoute } from 'vue-router'
import {
  Fold, Expand, ArrowDown, User, SwitchButton,
  Briefcase, Document, Star, Tickets, Calendar, Setting, List, ArrowLeft
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const navigationStore = useNavigationStore()
const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

const activeMenu = computed(() => {
  return route.path
})

const showBackButton = computed(() => {
  return navigationStore.shouldShowBackButton(route.path)
})

const getHomePath = () => {
  switch (userType.value) {
    case 3:
      return '/admin'
    default:
      return '/home'
  }
}

const handleBack = () => {
  const previousPage = navigationStore.goBack(router)
  if (previousPage) {
    const state = navigationStore.getPageState(previousPage.path)
    router.push(previousPage.fullPath || previousPage.path).then(() => {
      if (state && state.scrollPosition) {
        setTimeout(() => {
          window.scrollTo(state.scrollPosition.x, state.scrollPosition.y)
        }, 100)
      }
    })
  } else {
    router.push(getHomePath())
  }
}

watch(() => route.path, (newPath, oldPath) => {
  if (newPath !== oldPath) {
    navigationStore.pushHistory(route)
  }
}, { immediate: true })

onMounted(() => {
  navigationStore.pushHistory(route)
})

const userType = computed(() => userStore.userType)

const centerName = computed(() => {
  switch (userType.value) {
    case 1:
      return '求职者中心'
    case 2:
      return '招聘者中心'
    case 3:
      return '管理中心'
    default:
      return '智能招聘系统'
  }
})

const getRoleName = (type) => {
  switch (type) {
    case 1:
      return '求职者'
    case 2:
      return '招聘者'
    case 3:
      return '管理员'
    default:
      return '游客'
  }
}

const menuList = computed(() => {
  switch (userType.value) {
    case 1:
      return [
        { path: '/home', title: '首页', icon: House },
        { path: '/jobs', title: '职位推荐', icon: Briefcase },
        { path: '/user/applications', title: '我的投递', icon: Document },
        { path: '/user/favorites', title: '我的收藏', icon: Star },
        { path: '/user/resume', title: '我的简历', icon: Tickets },
        { path: '/user/interviews', title: '我的面试', icon: Calendar },
        { path: '/user/profile', title: '个人中心', icon: User }
      ]
    case 2:
      return [
        { path: '/home', title: '首页', icon: House },
        { path: '/company/info', title: '企业信息', icon: Briefcase },
        { path: '/company/jobs', title: '职位管理', icon: Document },
        { path: '/company/applications', title: '投递管理', icon: Tickets },
        { path: '/company/interviews', title: '面试管理', icon: Calendar },
        { path: '/company/statistics', title: '数据统计', icon: Star },
        { path: '/company/profile', title: '个人中心', icon: User }
      ]
    case 3:
      return [
        { path: '/admin', title: '首页', icon: House },
        { path: '/admin/users', title: '用户管理', icon: User },
        { path: '/admin/companies', title: '企业管理', icon: Briefcase },
        { path: '/admin/jobs', title: '职位审核', icon: Document },
        { path: '/admin/statistics', title: '数据统计', icon: Star },
        { path: '/admin/settings', title: '系统设置', icon: Setting },
        { path: '/admin/logs', title: '操作日志', icon: List }
      ]
    default:
      return [
        { path: '/home', title: '首页', icon: House },
        { path: '/jobs', title: '职位列表', icon: Briefcase }
      ]
  }
})

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      const type = userStore.userType
      if (type === 1) {
        router.push('/user/profile')
      } else if (type === 2) {
        router.push('/company/profile')
      } else if (type === 3) {
        router.push('/admin/settings')
      }
      break
    case 'logout':
      userStore.logoutAction()
      break
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  height: 100vh;
  display: flex;
  background: var(--gray-50);

  .aside {
    background: linear-gradient(180deg, var(--primary-800) 0%, var(--primary-700) 50%, var(--primary-600) 100%);
    display: flex;
    flex-direction: column;
    transition: width var(--transition-slow);
    box-shadow: var(--shadow-xl);
    position: relative;
    z-index: 10;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.03'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
      pointer-events: none;
    }

    .logo {
      height: 64px;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      padding: 0 16px;
      cursor: pointer;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      position: relative;
      z-index: 1;
      
      .logo-icon {
        width: 40px;
        height: 40px;
        border-radius: var(--radius-lg);
        background: rgba(255, 255, 255, 0.15);
        backdrop-filter: blur(8px);
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all var(--transition-normal);
        flex-shrink: 0;
        
        .el-icon {
          font-size: 22px;
          color: white;
        }
        
        &.collapsed {
          width: 36px;
          height: 36px;
        }
      }
      
      .logo-text {
        font-size: 16px;
        font-weight: var(--font-weight-bold);
        color: white;
        white-space: nowrap;
        letter-spacing: 0.5px;
      }
    }

    .side-menu {
      flex: 1;
      border-right: none;
      background: transparent;
      padding: 8px;
      position: relative;
      z-index: 1;

      :deep(.el-menu-item) {
        color: rgba(255, 255, 255, 0.7);
        height: 48px;
        line-height: 48px;
        margin: 4px 0;
        border-radius: var(--radius-lg);
        transition: all var(--transition-normal);
        position: relative;
        overflow: hidden;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 0;
          width: 3px;
          height: 100%;
          background: white;
          transform: scaleY(0);
          transition: transform var(--transition-normal);
        }

        &:hover {
          background: rgba(255, 255, 255, 0.1);
          color: white;
          transform: translateX(4px);
        }

        &.is-active {
          background: rgba(255, 255, 255, 0.2);
          color: white;
          font-weight: var(--font-weight-medium);
          
          &::before {
            transform: scaleY(1);
          }
        }

        .el-icon {
          font-size: 18px;
        }
      }

      :deep(.el-sub-menu) {
        .el-sub-menu__title {
          color: rgba(255, 255, 255, 0.7);
          height: 48px;
          line-height: 48px;
          margin: 4px 0;
          border-radius: var(--radius-lg);
          transition: all var(--transition-normal);

          &:hover {
            background: rgba(255, 255, 255, 0.1);
            color: white;
          }
        }

        &.is-active {
          .el-sub-menu__title {
            color: white;
          }
        }

        .el-menu-item {
          padding-left: 55px !important;
          min-width: auto;
        }
      }
    }

    .collapse-btn {
      height: 56px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: rgba(255, 255, 255, 0.7);
      cursor: pointer;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
      transition: all var(--transition-normal);
      position: relative;
      z-index: 1;

      &:hover {
        color: white;
        background: rgba(255, 255, 255, 0.1);
      }

      .el-icon {
        font-size: 20px;
        transition: transform var(--transition-normal);
      }
    }
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    box-shadow: var(--shadow-sm);
    padding: 0 24px;
    height: 64px;
    position: relative;
    z-index: 5;

    .header-left {
      display: flex;
      align-items: center;

      .back-button {
        font-weight: var(--font-weight-medium);
        color: var(--text-secondary);
        transition: all var(--transition-fast);

        &:hover {
          color: var(--primary-600);
          background: var(--primary-50);
        }

        .el-icon {
          font-size: 16px;
        }
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-dropdown {
        cursor: pointer;
      }

      .user-info {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 6px 12px 6px 6px;
        border-radius: var(--radius-xl);
        transition: all var(--transition-normal);
        background: var(--gray-50);

        &:hover {
          background: var(--gray-100);
          
          .dropdown-arrow {
            transform: rotate(180deg);
          }
        }

        .user-avatar {
          border: 2px solid var(--primary-200);
          box-shadow: var(--shadow-sm);
          background: linear-gradient(135deg, var(--primary-500), var(--primary-600));
          color: white;
          font-weight: var(--font-weight-semibold);
        }

        .user-details {
          display: flex;
          flex-direction: column;
          gap: 2px;

          .username {
            color: var(--text-primary);
            font-size: var(--font-size-sm);
            font-weight: var(--font-weight-semibold);
            line-height: 1.2;
          }

          .user-role {
            color: var(--text-tertiary);
            font-size: var(--font-size-xs);
            line-height: 1.2;
          }
        }

        .dropdown-arrow {
          color: var(--text-tertiary);
          transition: transform var(--transition-normal);
          font-size: 12px;
        }
      }

      .login-btn,
      .register-btn {
        height: 38px;
        padding: 0 20px;
        border-radius: var(--radius-lg);
        font-weight: var(--font-weight-medium);
        transition: all var(--transition-normal);
      }

      .register-btn {
        border: 1px solid var(--border-default);
        color: var(--text-secondary);
        
        &:hover {
          border-color: var(--primary-400);
          color: var(--primary-600);
          background: var(--primary-50);
        }
      }
    }
  }

  .main {
    background: var(--gray-50);
    padding: 24px;
    overflow-y: auto;
    min-height: calc(100vh - 64px);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-normal);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: all var(--transition-slow);
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
