import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    collapsed: false,
    device: 'desktop'
  }),

  actions: {
    toggleCollapsed() {
      this.collapsed = !this.collapsed
    },

    setDevice(device) {
      this.device = device
    }
  }
})
