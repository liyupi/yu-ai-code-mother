<template>
  <div id="appChatPage">
    <ChatHeaderBar
      :app-name="appInfo?.appName"
      :code-gen-type="appInfo?.codeGenType"
      :is-owner="isOwner"
      :downloading="downloading"
      :deploying="deploying"
      @show-detail="showAppDetail"
      @download="downloadCode"
      @deploy="deployApp"
    />

    <div class="main-content">
      <div class="chat-section">
        <ChatMessageList
          ref="messageListRef"
          :messages="messages"
          :user-avatar="loginUserStore.loginUser.userAvatar"
          :has-more-history="hasMoreHistory"
          :loading-history="loadingHistory"
          @load-more="loadMoreHistory"
        />

        <SelectedElementAlert
          :element-info="selectedElementInfo"
          @clear="clearSelectedElement"
        />

        <ChatInputArea
          v-model="userInput"
          :placeholder="getInputPlaceholder()"
          :is-owner="isOwner"
          :disabled="isGenerating"
          :loading="isGenerating"
          @send="sendMessage"
        />
      </div>

      <PreviewPanel
        :preview-url="previewUrl"
        :is-generating="isGenerating"
        :is-owner="isOwner"
        :is-edit-mode="isEditMode"
        @toggle-edit="toggleEditMode"
        @open-new-tab="openInNewTab"
        @iframe-load="onIframeLoad"
      />
    </div>

    <AppDetailModal
      v-model:open="appDetailVisible"
      :app="appInfo"
      :show-actions="isOwner || isAdmin"
      @edit="editApp"
      @delete="handleDeleteApp"
    />

    <DeploySuccessModal
      v-model:open="deployModalVisible"
      :deploy-url="deployUrl"
      @open-site="openDeployedSite"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useAppChat } from '@/composables/useAppChat'
import AppDetailModal from '@/components/AppDetailModal.vue'
import DeploySuccessModal from '@/components/DeploySuccessModal.vue'
import ChatHeaderBar from './components/ChatHeaderBar.vue'
import ChatMessageList from './components/ChatMessageList.vue'
import SelectedElementAlert from './components/SelectedElementAlert.vue'
import ChatInputArea from './components/ChatInputArea.vue'
import PreviewPanel from './components/PreviewPanel.vue'

const messageListRef = ref<InstanceType<typeof ChatMessageList>>()

const {
  appInfo,
  userInput,
  isGenerating,
  messagesContainer,
  appDetailVisible,
  isOwner,
  isAdmin,
  messages,
  loadingHistory,
  hasMoreHistory,
  loadMoreHistory,
  previewUrl,
  isEditMode,
  selectedElementInfo,
  deploying,
  downloading,
  deployModalVisible,
  deployUrl,
  loginUserStore,
  fetchAppInfo,
  sendMessage,
  downloadCode,
  deployApp,
  openInNewTab,
  openDeployedSite,
  onIframeLoad,
  toggleEditMode,
  clearSelectedElement,
  getInputPlaceholder,
  showAppDetail,
  editApp,
  handleDeleteApp,
} = useAppChat()

watch(
  () => messageListRef.value?.containerRef,
  (el) => {
    if (el) {
      messagesContainer.value = el
    }
  },
  { immediate: true },
)

onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
#appChatPage {
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: #fdfdfd;
}

.main-content {
  flex: 1;
  display: flex;
  gap: 16px;
  padding: 8px;
  overflow: hidden;
}

.chat-section {
  flex: 2;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

@media (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }

  .chat-section,
  :deep(.preview-section) {
    flex: none;
    height: 50vh;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 8px;
    gap: 8px;
  }
}
</style>
