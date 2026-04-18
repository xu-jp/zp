import { defineStore } from 'pinia'
import { login, logout } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    userType: (state) => state.userInfo.userType,
    username: (state) => state.userInfo.username,
    userId: (state) => state.userInfo.userId
  },

  actions: {
    async loginAction(loginData) {
      try {
        const res = await login(loginData)
        this.token = res.data.token
        this.userInfo = {
          userId: res.data.userId,
          username: res.data.username,
          userType: res.data.userType,
          avatar: res.data.avatar,
          realName: res.data.realName
        }
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
        return res
      } catch (error) {
        throw error
      }
    },

    async logoutAction() {
      try {
        await logout()
      } catch (error) {
        console.error('Logout error:', error)
      } finally {
        this.token = ''
        this.userInfo = {}
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
    },

    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    }
  }
})
