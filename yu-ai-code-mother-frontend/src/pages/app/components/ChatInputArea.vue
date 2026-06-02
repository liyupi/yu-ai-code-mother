<template>
  <div class="input-container">
    <div class="input-wrapper">
      <a-tooltip v-if="!isOwner" title="无法在别人的作品下对话哦~" placement="top">
        <a-textarea
          :value="modelValue"
          :placeholder="placeholder"
          :rows="4"
          :maxlength="1000"
          :disabled="disabled || !isOwner"
          @update:value="$emit('update:modelValue', $event)"
          @keydown.enter.prevent="$emit('send')"
        />
      </a-tooltip>
      <a-textarea
        v-else
        :value="modelValue"
        :placeholder="placeholder"
        :rows="4"
        :maxlength="1000"
        :disabled="disabled"
        @update:value="$emit('update:modelValue', $event)"
        @keydown.enter.prevent="$emit('send')"
      />
      <div class="input-actions">
        <a-button
          type="primary"
          :loading="loading"
          :disabled="!isOwner"
          @click="$emit('send')"
        >
          <template #icon><SendOutlined /></template>
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { SendOutlined } from '@ant-design/icons-vue'

defineProps<{
  modelValue: string
  placeholder: string
  isOwner: boolean
  disabled: boolean
  loading: boolean
}>()

defineEmits<{
  'update:modelValue': [value: string]
  send: []
}>()
</script>

<style scoped>
.input-container {
  padding: 16px;
  background: white;
}

.input-wrapper {
  position: relative;
}

.input-wrapper :deep(.ant-input) {
  padding-right: 50px;
}

.input-actions {
  position: absolute;
  bottom: 8px;
  right: 8px;
}
</style>
