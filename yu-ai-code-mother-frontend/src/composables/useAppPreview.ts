import { ref, type Ref } from 'vue'
import { CodeGenTypeEnum } from '@/utils/codeGenTypes'
import { getStaticPreviewUrl } from '@/config/env'

/**
 * 应用预览 URL 管理
 */
export function useAppPreview(
  appId: Ref<string | undefined>,
  appInfo: Ref<API.AppVO | undefined>,
) {
  const previewUrl = ref('')
  const previewReady = ref(false)

  const updatePreview = () => {
    if (appId.value) {
      const codeGenType = appInfo.value?.codeGenType || CodeGenTypeEnum.HTML
      previewUrl.value = getStaticPreviewUrl(codeGenType, appId.value)
      previewReady.value = true
    }
  }

  const openInNewTab = () => {
    if (previewUrl.value) {
      window.open(previewUrl.value, '_blank')
    }
  }

  return {
    previewUrl,
    previewReady,
    updatePreview,
    openInNewTab,
  }
}
