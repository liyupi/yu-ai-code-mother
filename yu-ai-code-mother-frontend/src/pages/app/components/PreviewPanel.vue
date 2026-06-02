<template>
  <div class="preview-section">
    <div class="preview-header">
      <h3>生成后的网页展示</h3>
      <div class="preview-actions">
        <a-button
          v-if="isOwner && previewUrl"
          type="link"
          :danger="isEditMode"
          :class="{ 'edit-mode-active': isEditMode }"
          style="padding: 0; height: auto; margin-right: 12px"
          @click="$emit('toggleEdit')"
        >
          <template #icon><EditOutlined /></template>
          {{ isEditMode ? '退出编辑' : '编辑模式' }}
        </a-button>
        <a-button v-if="previewUrl" type="link" @click="$emit('openNewTab')">
          <template #icon><ExportOutlined /></template>
          新窗口打开
        </a-button>
      </div>
    </div>
    <div class="preview-content">
      <div v-if="!previewUrl && !isGenerating" class="preview-placeholder">
        <div class="placeholder-icon">🌐</div>
        <p>网站文件生成完成后将在这里展示</p>
      </div>
      <div v-else-if="isGenerating" class="preview-loading">
        <a-spin size="large" />
        <p>正在生成网站...</p>
      </div>
      <iframe
        v-else
        :src="previewUrl"
        class="preview-iframe"
        frameborder="0"
        @load="$emit('iframeLoad')"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { EditOutlined, ExportOutlined } from '@ant-design/icons-vue'

defineProps<{
  previewUrl: string
  isGenerating: boolean
  isOwner: boolean
  isEditMode: boolean
}>()

defineEmits<{
  toggleEdit: []
  openNewTab: []
  iframeLoad: []
}>()
</script>

<style scoped>
.preview-section {
  flex: 3;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.preview-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.preview-content {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.preview-placeholder,
.preview-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.preview-loading p {
  margin-top: 16px;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.edit-mode-active {
  background-color: #52c41a !important;
  border-color: #52c41a !important;
  color: white !important;
}

.edit-mode-active:hover {
  background-color: #73d13d !important;
  border-color: #73d13d !important;
}
</style>
