import { ref, type Ref } from 'vue'
import { message } from 'ant-design-vue'
import { listAppChatHistory } from '@/api/chatHistoryController'
import type { ChatMessage } from '@/types/chat'

/**
 * 对话历史加载逻辑
 */
export function useChatHistory(appId: Ref<string | undefined>) {
  const messages = ref<ChatMessage[]>([])
  const loadingHistory = ref(false)
  const hasMoreHistory = ref(false)
  const lastCreateTime = ref<string>()
  const historyLoaded = ref(false)

  const loadChatHistory = async (isLoadMore = false) => {
    if (!appId.value || loadingHistory.value) return

    loadingHistory.value = true
    try {
      const params: API.listAppChatHistoryParams = {
        appId: appId.value as unknown as number,
        pageSize: 10,
      }
      if (isLoadMore && lastCreateTime.value) {
        params.lastCreateTime = lastCreateTime.value
      }

      const res = await listAppChatHistory(params)
      if (res.data.code === 0 && res.data.data) {
        const chatHistories = res.data.data.records || []
        if (chatHistories.length > 0) {
          const historyMessages: ChatMessage[] = chatHistories
            .map((chat) => ({
              type: (chat.messageType === 'user' ? 'user' : 'ai') as 'user' | 'ai',
              content: chat.message || '',
              createTime: chat.createTime,
            }))
            .reverse()

          if (isLoadMore) {
            messages.value.unshift(...historyMessages)
          } else {
            messages.value = historyMessages
          }

          lastCreateTime.value = chatHistories[chatHistories.length - 1]?.createTime
          hasMoreHistory.value = chatHistories.length === 10
        } else {
          hasMoreHistory.value = false
        }
        historyLoaded.value = true
      }
    } catch (error) {
      console.error('加载对话历史失败：', error)
      message.error('加载对话历史失败')
    } finally {
      loadingHistory.value = false
    }
  }

  const loadMoreHistory = () => loadChatHistory(true)

  return {
    messages,
    loadingHistory,
    hasMoreHistory,
    historyLoaded,
    loadChatHistory,
    loadMoreHistory,
  }
}
