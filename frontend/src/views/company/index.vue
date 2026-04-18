<template>
  <div class="company-center">
    <router-view />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  const path = window.location.hash
  if (path === '#/company' || path === '#/company/') {
    router.replace('/company/profile')
  }
})
</script>

<style lang="scss" scoped>
.company-center {
  height: 100%;
}
</style>
