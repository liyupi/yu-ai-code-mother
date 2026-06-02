<template>
  <div class="header-bar">
    <div class="header-left">
      <h1 class="app-name">{{ appName }}</h1>
      <a-tag v-if="codeGenType" color="blue" class="code-gen-type-tag">
        {{ formatCodeGenType(codeGenType) }}
      </a-tag>
    </div>
    <div class="header-right">
      <a-button type="default" @click="$emit('showDetail')">
        <template #icon><InfoCircleOutlined /></template>
        应用详情
      </a-button>
      <a-button
        type="primary"
        ghost
        :loading="downloading"
        :disabled="!isOwner"
        @click="$emit('download')"
      >
        <template #icon><DownloadOutlined /></template>
        下载代码
      </a-button>
      <a-button type="primary" :loading="deploying" @click="$emit('deploy')">
        <template #icon><CloudUploadOutlined /></template>
        部署
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  CloudUploadOutlined,
  InfoCircleOutlined,
  DownloadOutlined,
} from '@ant-design/icons-vue'
import { formatCodeGenType } from '@/utils/codeGenTypes'

defineProps<{
  appName?: string
  codeGenType?: string
  isOwner: boolean
  downloading: boolean
  deploying: boolean
}>()

defineEmits<{
  showDetail: []
  download: []
  deploy: []
}>()
</script>

<style scoped>
.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.code-gen-type-tag {
  font-size: 12px;
}

.app-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.header-right {
  display: flex;
  gap: 12px;
}

@media (max-width: 768px) {
  .header-bar {
    padding: 12px 16px;
  }

  .app-name {
    font-size: 16px;
  }
}
</style>
