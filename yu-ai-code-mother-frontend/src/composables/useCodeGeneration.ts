import { ref, type Ref } from 'vue'
import { message } from 'ant-design-vue'
import request from '@/request'
import { API_BASE_URL } from '@/config/env'
import type { ChatMessage } from '@/types/chat'

interface UseCodeGenerationOptions {
  appId: Ref<string | undefined>
  messages: Ref<ChatMessage[]>
  isGenerating: Ref<boolean>
  scrollToBottom: () => void
  onComplete: () => Promise<void>
}

/**
 * SSE 流式代码生成逻辑
 */
export function useCodeGeneration(options: UseCodeGenerationOptions) {
  const { appId, messages, isGenerating, scrollToBottom, onComplete } = options

  const handleError = (error: unknown, aiMessageIndex: number) => {
    console.error('生成代码失败：', error)
    messages.value[aiMessageIndex].content = '抱歉，生成过程中出现了错误，请重试。'
    messages.value[aiMessageIndex].loading = false
    message.error('生成失败，请重试')
    isGenerating.value = false
  }

  const generateCode = async (userMessage: string, aiMessageIndex: number) => {
    let eventSource: EventSource | null = null
    let streamCompleted = false

    try {
      const baseURL = request.defaults.baseURL || API_BASE_URL
      const params = new URLSearchParams({
        appId: appId.value || '',
        message: userMessage,
      })
      const url = `${baseURL}/app/chat/gen/code?${params}`

      eventSource = new EventSource(url, { withCredentials: true })
      let fullContent = ''

      eventSource.onmessage = (event) => {
        if (streamCompleted) return
        try {
          const parsed = JSON.parse(event.data)
          const content = parsed.d
          if (content !== undefined && content !== null) {
            fullContent += content
            messages.value[aiMessageIndex].content = fullContent
            messages.value[aiMessageIndex].loading = false
            scrollToBottom()
          }
        } catch (error) {
          console.error('解析消息失败:', error)
          handleError(error, aiMessageIndex)
        }
      }

      eventSource.addEventListener('done', () => {
        if (streamCompleted) return
        streamCompleted = true
        isGenerating.value = false
        eventSource?.close()
        setTimeout(() => onComplete(), 1000)
      })

      eventSource.addEventListener('business-error', (event: MessageEvent) => {
        if (streamCompleted) return
        try {
          const errorData = JSON.parse(event.data)
          const errorMessage = errorData.message || '生成过程中出现错误'
          messages.value[aiMessageIndex].content = `❌ ${errorMessage}`
          messages.value[aiMessageIndex].loading = false
          message.error(errorMessage)
          streamCompleted = true
          isGenerating.value = false
          eventSource?.close()
        } catch (parseError) {
          console.error('解析错误事件失败:', parseError)
          handleError(new Error('服务器返回错误'), aiMessageIndex)
        }
      })

      eventSource.onerror = () => {
        if (streamCompleted || !isGenerating.value) return
        if (eventSource?.readyState === EventSource.CONNECTING) {
          streamCompleted = true
          isGenerating.value = false
          eventSource?.close()
          setTimeout(() => onComplete(), 1000)
        } else {
          handleError(new Error('SSE连接错误'), aiMessageIndex)
        }
      }
    } catch (error) {
      console.error('创建 EventSource 失败：', error)
      handleError(error, aiMessageIndex)
    }
  }

  return { generateCode }
}
