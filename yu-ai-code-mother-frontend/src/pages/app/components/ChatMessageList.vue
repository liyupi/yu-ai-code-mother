<template>
  <div class="messages-container" ref="containerRef">
    <div v-if="hasMoreHistory" class="load-more-container">
      <a-button type="link" :loading="loadingHistory" size="small" @click="$emit('loadMore')">
        加载更多历史消息
      </a-button>
    </div>
    <div v-for="(message, index) in messages" :key="index" class="message-item">
      <div v-if="message.type === 'user'" class="user-message">
        <div class="message-content">{{ message.content }}</div>
        <div class="message-avatar">
          <a-avatar :src="userAvatar" />
        </div>
      </div>
      <div v-else class="ai-message">
        <div class="message-avatar">
          <a-avatar :src="aiAvatar" />
        </div>
        <div class="message-content">
          <MarkdownRenderer v-if="message.content" :content="message.content" />
          <div v-if="message.loading" class="loading-indicator">
            <a-spin size="small" />
            <span>AI 正在思考...</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import aiAvatar from '@/assets/aiAvatar.svg'
import type { ChatMessage } from '@/types/chat'

defineProps<{
  messages: ChatMessage[]
  userAvatar?: string
  hasMoreHistory: boolean
  loadingHistory: boolean
}>()

defineEmits<{ loadMore: [] }>()

const containerRef = ref<HTMLElement>()

defineExpose({ containerRef })
</script>

<style scoped>
.messages-container {
  flex: 0.9;
  padding: 16px;
  overflow-y: auto;
  scroll-behavior: smooth;
}

.message-item {
  margin-bottom: 12px;
}

.user-message {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  gap: 8px;
}

.ai-message {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 8px;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  word-wrap: break-word;
}

.user-message .message-content {
  background: #1890ff;
  color: white;
}

.ai-message .message-content {
  background: #f5f5f5;
  color: #1a1a1a;
  padding: 8px 12px;
}

.message-avatar {
  flex-shrink: 0;
}

.loading-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
}

.load-more-container {
  text-align: center;
  padding: 8px 0;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .message-content {
    max-width: 85%;
  }
}
</style>
