import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useNavigationStore = defineStore('navigation', () => {
  // 页面状态缓存
  const pageStateCache = ref(new Map())
  
  // 导航历史栈
  const navigationHistory = ref([])
  
  // 当前是否可以返回
  const canGoBack = computed(() => navigationHistory.value.length > 1)
  
  // 保存页面状态
  const savePageState = (path, state) => {
    pageStateCache.value.set(path, {
      ...state,
      timestamp: Date.now(),
      scrollPosition: state.scrollPosition || { x: 0, y: 0 }
    })
  }
  
  // 获取页面状态
  const getPageState = (path) => {
    return pageStateCache.value.get(path)
  }
  
  // 清除页面状态
  const clearPageState = (path) => {
    if (path) {
      pageStateCache.value.delete(path)
    } else {
      pageStateCache.value.clear()
    }
  }
  
  // 添加导航记录
  const pushHistory = (route) => {
    const record = {
      path: route.path,
      fullPath: route.fullPath,
      meta: route.meta,
      timestamp: Date.now()
    }
    
    // 避免重复添加相同路径
    const lastRecord = navigationHistory.value[navigationHistory.value.length - 1]
    if (lastRecord && lastRecord.path === route.path) {
      return
    }
    
    navigationHistory.value.push(record)
    
    // 限制历史栈长度，避免内存泄漏
    if (navigationHistory.value.length > 20) {
      navigationHistory.value.shift()
    }
  }
  
  // 返回上一页
  const goBack = (router) => {
    if (navigationHistory.value.length >= 2) {
      // 移除当前页
      navigationHistory.value.pop()
      // 获取上一页
      const previousPage = navigationHistory.value[navigationHistory.value.length - 1]
      return previousPage
    }
    return null
  }
  
  // 获取上一页路径
  const getPreviousPage = () => {
    if (navigationHistory.value.length >= 2) {
      return navigationHistory.value[navigationHistory.value.length - 2]
    }
    return null
  }
  
  // 判断是否需要显示返回按钮
  const shouldShowBackButton = (currentPath) => {
    // 首页不显示返回按钮
    const homePaths = ['/home', '/admin', '/']
    if (homePaths.includes(currentPath)) {
      return false
    }
    return canGoBack.value
  }
  
  // 获取返回目标路径
  const getBackTarget = () => {
    const previousPage = getPreviousPage()
    if (previousPage) {
      return previousPage.fullPath || previousPage.path
    }
    return '/home'
  }
  
  return {
    pageStateCache,
    navigationHistory,
    canGoBack,
    savePageState,
    getPageState,
    clearPageState,
    pushHistory,
    goBack,
    getPreviousPage,
    shouldShowBackButton,
    getBackTarget
  }
})
