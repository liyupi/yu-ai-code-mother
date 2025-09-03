<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'

import { healthCheck } from '@/api/healthController.ts'

healthCheck().then((res) => {
  console.log(res)
})
</script>

<template>
  <BasicLayout />
</template>

<style></style>
