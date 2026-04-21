import { defineStore } from 'pinia'

export const useRecommendStore = defineStore('recommend', {
  state: () => ({
    recommendJobs: JSON.parse(sessionStorage.getItem('recommendJobs') || 'null'),
    recommendTime: sessionStorage.getItem('recommendTime') || null
  }),

  getters: {
    hasCache: (state) => !!state.recommendJobs && state.recommendJobs.length > 0
  },

  actions: {
    setRecommendJobs(jobs) {
      this.recommendJobs = jobs
      this.recommendTime = new Date().toISOString()
      sessionStorage.setItem('recommendJobs', JSON.stringify(jobs))
      sessionStorage.setItem('recommendTime', this.recommendTime)
    },

    clearCache() {
      this.recommendJobs = null
      this.recommendTime = null
      sessionStorage.removeItem('recommendJobs')
      sessionStorage.removeItem('recommendTime')
    }
  }
})
