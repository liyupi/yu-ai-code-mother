import { ref, computed, nextTick, type Ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppVoById } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'
import { useChatHistory } from './useChatHistory'
import { useAppPreview } from './useAppPreview'
import { useVisualEdit } from './useVisualEdit'
import { useCodeGeneration } from './useCodeGeneration'
import { useAppActions } from './useAppActions'

/**
 * 应用对话页核心逻辑
 */
export function useAppChat() {
  const route = useRoute()
  const router = useRouter()
  const loginUserStore = useLoginUserStore()

  const appInfo = ref<API.AppVO>()
  const appId = ref<string>()
  const userInput = ref('')
  const isGenerating = ref(false)
  const messagesContainer = ref<HTMLElement>()
  const appDetailVisible = ref(false)

  const isOwner = computed(() => appInfo.value?.userId === loginUserStore.loginUser.id)
  const isAdmin = computed(() => loginUserStore.loginUser.userRole === 'admin')

  const {
    messages,
    loadingHistory,
    hasMoreHistory,
    historyLoaded,
    loadChatHistory,
    loadMoreHistory,
  } = useChatHistory(appId)

  const { previewUrl, previewReady, updatePreview, openInNewTab } = useAppPreview(appId, appInfo)

  const {
    isEditMode,
    selectedElementInfo,
    initIframe,
    toggleEditMode: doToggleEditMode,
    clearSelectedElement,
    getInputPlaceholder,
    buildMessageWithElementContext,
  } = useVisualEdit()

  const scrollToBottom = () => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  }

  const refreshAppAndPreview = async () => {
    await fetchAppInfo(false)
    updatePreview()
  }

  const { generateCode } = useCodeGeneration({
    appId,
    messages,
    isGenerating,
    scrollToBottom,
    onComplete: refreshAppAndPreview,
  })

  const {
    deploying,
    downloading,
    deployModalVisible,
    deployUrl,
    downloadCode,
    deployApp,
    deleteApp,
    openDeployedSite,
  } = useAppActions(appId, appInfo)

  const fetchAppInfo = async (loadHistory = true) => {
    const id = route.params.id as string
    if (!id) {
      message.error('应用ID不存在')
      router.push('/')
      return
    }

    appId.value = id

    try {
      const res = await getAppVoById({ id: id as unknown as number })
      if (res.data.code === 0 && res.data.data) {
        appInfo.value = res.data.data

        if (loadHistory) {
          await loadChatHistory()
          if (messages.value.length >= 2) {
            updatePreview()
          }
          if (
            appInfo.value.initPrompt &&
            isOwner.value &&
            messages.value.length === 0 &&
            historyLoaded.value
          ) {
            await sendInitialMessage(appInfo.value.initPrompt)
          }
        }
      } else {
        message.error('获取应用信息失败')
        router.push('/')
      }
    } catch (error) {
      console.error('获取应用信息失败：', error)
      message.error('获取应用信息失败')
      router.push('/')
    }
  }

  const appendAiPlaceholder = () => {
    const aiMessageIndex = messages.value.length
    messages.value.push({ type: 'ai', content: '', loading: true })
    return aiMessageIndex
  }

  const sendInitialMessage = async (prompt: string) => {
    messages.value.push({ type: 'user', content: prompt })
    const aiMessageIndex = appendAiPlaceholder()
    await nextTick()
    scrollToBottom()
    isGenerating.value = true
    await generateCode(prompt, aiMessageIndex)
  }

  const sendMessage = async () => {
    if (!userInput.value.trim() || isGenerating.value) return

    const rawMessage = userInput.value.trim()
    const messageContent = buildMessageWithElementContext(rawMessage)
    userInput.value = ''

    if (selectedElementInfo.value) {
      clearSelectedElement()
      if (isEditMode.value) {
        toggleEditMode()
      }
    }

    messages.value.push({ type: 'user', content: messageContent })
    const aiMessageIndex = appendAiPlaceholder()
    await nextTick()
    scrollToBottom()

    isGenerating.value = true
    await generateCode(messageContent, aiMessageIndex)
  }

  const toggleEditMode = () => {
    const result = doToggleEditMode(previewReady.value)
    if (!result.success && result.message) {
      message.warning(result.message)
    }
  }

  const onIframeLoad = () => {
    previewReady.value = true
    const iframe = document.querySelector('.preview-iframe') as HTMLIFrameElement
    if (iframe) {
      initIframe(iframe)
    }
  }

  const showAppDetail = () => {
    appDetailVisible.value = true
  }

  const editApp = () => {
    if (appInfo.value?.id) {
      router.push(`/app/edit/${appInfo.value.id}`)
    }
  }

  const handleDeleteApp = async () => {
    await deleteApp(() => {
      appDetailVisible.value = false
      router.push('/')
    })
  }

  return {
    appInfo,
    appId,
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
    previewReady,
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
  }
}
