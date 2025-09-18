<template>
  <div class="user-info">
    <a-avatar :src="user?.userAvatar" :size="size">
      {{ user?.userName?.charAt(0) || 'U' }}
    </a-avatar>
    <span v-if="showName" class="user-name">{{ user?.userName || '未知用户' }}</span>
  </div>
</template>

<script setup lang="ts">
interface Props {
  user?: API.UserVO
  size?: number | 'small' | 'default' | 'large'
  showName?: boolean
}

withDefaults(defineProps<Props>(), {
  size: 'default',
  showName: true,
})
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 14px;
  color: #1a1a1a;
}
</style>
