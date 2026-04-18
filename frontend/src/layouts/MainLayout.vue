<template>
  <div class="main-layout">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
        <div class="logo" @click="$router.push('/home')">
          <el-icon v-if="isCollapse"><House /></el-icon>
          <span v-else>{{ centerName }}</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="side-menu"
        >
          <template v-for="item in menuList" :key="item.path">
            <el-menu-item v-if="!item.children" :index="item.path">
              <el-icon><component :is="item.icon" /></el-icon>
              <template #title>{{ item.title }}</template>
            </el-menu-item>
            <el-sub-menu v-else :index="item.path">
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
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentRoute.meta?.title">{{ currentRoute.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <template v-if="userStore.isLoggedIn">
              <el-dropdown @command="handleCommand">
                <span class="user-info">
                  <el-avatar :size="36" :src="userStore.userInfo.avatar">
                    {{ userStore.username?.charAt(0)?.toUpperCase() }}
                  </el-avatar>
                  <span class="username">{{ userStore.userInfo.realName || userStore.username }}</span>
                  <el-icon><ArrowDown /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>系统中心
                    </el-dropdown-item>
                    <el-dropdown-item command="logout" divided>
                      <el-icon><SwitchButton /></el-icon>退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button type="primary" @click="$router.push('/login')">登录</el-button>
              <el-button @click="$router.push('/register')">注册</el-button>
            </template>
          </div>
        </el-header>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { 
  House, Fold, Expand, ArrowDown, User, SwitchButton,
  Briefcase, Document, Star, Tickets, Calendar, Setting, List
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

const currentRoute = computed(() => route)

const activeMenu = computed(() => {
  return route.path
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

  .aside {
    background: linear-gradient(180deg, #1a1f36 0%, #2d3555 100%);
    display: flex;
    flex-direction: column;
    transition: width 0.3s;

    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      cursor: pointer;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      
      .el-icon {
        font-size: 24px;
      }
    }

    .side-menu {
      flex: 1;
      border-right: none;
      background: transparent;

      :deep(.el-menu-item) {
        color: rgba(255, 255, 255, 0.7);
        height: 50px;
        line-height: 50px;

        &:hover {
          background: rgba(255, 255, 255, 0.1);
          color: #fff;
        }

        &.is-active {
          background: linear-gradient(90deg, #409eff 0%, rgba(64, 158, 255, 0.5) 100%);
          color: #fff;
        }

        .el-icon {
          font-size: 18px;
        }
      }

      :deep(.el-sub-menu) {
        .el-sub-menu__title {
          color: rgba(255, 255, 255, 0.7);
          height: 50px;
          line-height: 50px;

          &:hover {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
          }
        }

        &.is-active {
          .el-sub-menu__title {
            color: #fff;
          }
        }

        .el-menu-item {
          padding-left: 55px !important;
          min-width: auto;
        }
      }
    }

    .collapse-btn {
      height: 48px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: rgba(255, 255, 255, 0.7);
      cursor: pointer;
      border-top: 1px solid rgba(255, 255, 255, 0.1);

      &:hover {
        color: #fff;
        background: rgba(255, 255, 255, 0.1);
      }

      .el-icon {
        font-size: 18px;
      }
    }
  }

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    padding: 0 24px;
    height: 60px;

    .header-left {
      display: flex;
      align-items: center;
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
        padding: 6px 12px;
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          background: #f5f7fa;
        }

        .username {
          color: #333;
          font-size: 14px;
        }
      }
    }
  }

  .main {
    background: #f5f7fa;
    padding: 20px;
    overflow-y: auto;
  }
}
</style>
