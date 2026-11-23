import { ref, onMounted, onUnmounted } from 'vue'
import { VisualEditor, type ElementInfo } from '@/utils/visualEditor'

/**
 * 可视化编辑模式逻辑
 */
export function useVisualEdit() {
  const isEditMode = ref(false)
  const selectedElementInfo = ref<ElementInfo | null>(null)

  const visualEditor = new VisualEditor({
    onElementSelected: (elementInfo: ElementInfo) => {
      selectedElementInfo.value = elementInfo
    },
  })

  const initIframe = (iframe: HTMLIFrameElement) => {
    visualEditor.init(iframe)
    visualEditor.onIframeLoad()
  }

  const toggleEditMode = (previewReady: boolean) => {
    const iframe = document.querySelector('.preview-iframe') as HTMLIFrameElement
    if (!iframe || !previewReady) {
      return { success: false, message: '请等待页面加载完成' }
    }
    const newEditMode = visualEditor.toggleEditMode()
    isEditMode.value = newEditMode
    return { success: true }
  }

  const clearSelectedElement = () => {
    selectedElementInfo.value = null
    visualEditor.clearSelection()
  }

  const getInputPlaceholder = () => {
    if (selectedElementInfo.value) {
      return `正在编辑 ${selectedElementInfo.value.tagName.toLowerCase()} 元素，描述您想要的修改...`
    }
    return '请描述你想生成的网站，越详细效果越好哦'
  }

  const buildMessageWithElementContext = (message: string): string => {
    if (!selectedElementInfo.value) return message

    let elementContext = '\n\n选中元素信息：'
    if (selectedElementInfo.value.pagePath) {
      elementContext += `\n- 页面路径: ${selectedElementInfo.value.pagePath}`
    }
    elementContext += `\n- 标签: ${selectedElementInfo.value.tagName.toLowerCase()}\n- 选择器: ${selectedElementInfo.value.selector}`
    if (selectedElementInfo.value.textContent) {
      elementContext += `\n- 当前内容: ${selectedElementInfo.value.textContent.substring(0, 100)}`
    }
    return message + elementContext
  }

  const handleIframeMessage = (event: MessageEvent) => {
    visualEditor.handleIframeMessage(event)
  }

  onMounted(() => {
    window.addEventListener('message', handleIframeMessage)
  })

  onUnmounted(() => {
    window.removeEventListener('message', handleIframeMessage)
  })

  return {
    isEditMode,
    selectedElementInfo,
    initIframe,
    toggleEditMode,
    clearSelectedElement,
    getInputPlaceholder,
    buildMessageWithElementContext,
  }
}
