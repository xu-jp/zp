import { defineStore } from 'pinia'
import { getPublicSettings } from '@/api/systemSetting'

export const useSettingStore = defineStore('setting', {
  state: () => ({
    settings: {},
    loading: false,
    loaded: false
  }),

  getters: {
    platformName: (state) => state.settings.platformName || '智能招聘系统',
    logo: (state) => state.settings.logo || '',
    servicePhone: (state) => state.settings.servicePhone || '',
    serviceEmail: (state) => state.settings.serviceEmail || '',
    serviceWechat: (state) => state.settings.serviceWechat || '',
    getSettingByKey: (state) => (key) => state.settings[key] || ''
  },

  actions: {
    async fetchSettings() {
      if (this.loaded) {
        return
      }
      
      this.loading = true
      try {
        const res = await getPublicSettings()
        if (res.data) {
          const settingsMap = {}
          res.data.forEach(item => {
            settingsMap[item.settingKey] = item.settingValue
          })
          this.settings = settingsMap
          this.loaded = true
        }
      } catch (error) {
        console.error('获取系统设置失败:', error)
      } finally {
        this.loading = false
      }
    },

    async refreshSettings() {
      this.loaded = false
      await this.fetchSettings()
    },

    updateSetting(key, value) {
      this.settings[key] = value
    }
  }
})
