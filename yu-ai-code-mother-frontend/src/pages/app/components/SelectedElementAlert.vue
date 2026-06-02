<template>
  <a-alert
    v-if="elementInfo"
    class="selected-element-alert"
    type="info"
    closable
    @close="$emit('clear')"
  >
    <template #message>
      <div class="selected-element-info">
        <div class="element-header">
          <span class="element-tag">选中元素：{{ elementInfo.tagName.toLowerCase() }}</span>
          <span v-if="elementInfo.id" class="element-id">#{{ elementInfo.id }}</span>
          <span v-if="elementInfo.className" class="element-class">
            .{{ elementInfo.className.split(' ').join('.') }}
          </span>
        </div>
        <div class="element-details">
          <div v-if="elementInfo.textContent" class="element-item">
            内容: {{ elementInfo.textContent.substring(0, 50) }}
            {{ elementInfo.textContent.length > 50 ? '...' : '' }}
          </div>
          <div v-if="elementInfo.pagePath" class="element-item">
            页面路径: {{ elementInfo.pagePath }}
          </div>
          <div class="element-item">
            选择器:
            <code class="element-selector-code">{{ elementInfo.selector }}</code>
          </div>
        </div>
      </div>
    </template>
  </a-alert>
</template>

<script setup lang="ts">
import type { ElementInfo } from '@/utils/visualEditor'

defineProps<{ elementInfo: ElementInfo | null }>()
defineEmits<{ clear: [] }>()
</script>

<style scoped>
.selected-element-alert {
  margin: 0 16px;
}

.selected-element-info {
  line-height: 1.4;
}

.element-header {
  margin-bottom: 8px;
}

.element-details {
  margin-top: 8px;
}

.element-item {
  margin-bottom: 4px;
  font-size: 13px;
}

.element-item:last-child {
  margin-bottom: 0;
}

.element-tag {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 14px;
  font-weight: 600;
  color: #007bff;
}

.element-id {
  color: #28a745;
  margin-left: 4px;
}

.element-class {
  color: #ffc107;
  margin-left: 4px;
}

.element-selector-code {
  font-family: 'Monaco', 'Menlo', monospace;
  background: #f6f8fa;
  padding: 2px 4px;
  border-radius: 3px;
  font-size: 12px;
  color: #d73a49;
  border: 1px solid #e1e4e8;
}
</style>
