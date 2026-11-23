import { ref, type Ref } from 'vue'
import { message } from 'ant-design-vue'
import { deployApp as deployAppApi, deleteApp as deleteAppApi } from '@/api/appController'
import request from '@/request'

/**
 * 应用操作：部署、下载、删除
 */
export function useAppActions(appId: Ref<string | undefined>, appInfo: Ref<API.AppVO | undefined>) {
  const deploying = ref(false)
  const downloading = ref(false)
  const deployModalVisible = ref(false)
  const deployUrl = ref('')

  const downloadCode = async () => {
    if (!appId.value) {
      message.error('应用ID不存在')
      return
    }
    downloading.value = true
    try {
      const baseURL = request.defaults.baseURL || ''
      const url = `${baseURL}/app/download/${appId.value}`
      const response = await fetch(url, { method: 'GET', credentials: 'include' })
      if (!response.ok) {
        throw new Error(`下载失败: ${response.status}`)
      }
      const contentDisposition = response.headers.get('Content-Disposition')
      const fileName =
        contentDisposition?.match(/filename="(.+)"/)?.[1] || `app-${appId.value}.zip`
      const blob = await response.blob()
      const downloadUrl = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = fileName
      link.click()
      URL.revokeObjectURL(downloadUrl)
      message.success('代码下载成功')
    } catch (error) {
      console.error('下载失败：', error)
      message.error('下载失败，请重试')
    } finally {
      downloading.value = false
    }
  }

  const deployApp = async () => {
    if (!appId.value) {
      message.error('应用ID不存在')
      return
    }
    deploying.value = true
    try {
      const res = await deployAppApi({ appId: appId.value as unknown as number })
      if (res.data.code === 0 && res.data.data) {
        deployUrl.value = res.data.data
        deployModalVisible.value = true
        message.success('部署成功')
      } else {
        message.error('部署失败：' + res.data.message)
      }
    } catch (error) {
      console.error('部署失败：', error)
      message.error('部署失败，请重试')
    } finally {
      deploying.value = false
    }
  }

  const deleteApp = async (onSuccess: () => void) => {
    if (!appInfo.value?.id) return
    try {
      const res = await deleteAppApi({ id: appInfo.value.id })
      if (res.data.code === 0) {
        message.success('删除成功')
        onSuccess()
      } else {
        message.error('删除失败：' + res.data.message)
      }
    } catch (error) {
      console.error('删除失败：', error)
      message.error('删除失败')
    }
  }

  const openDeployedSite = () => {
    if (deployUrl.value) {
      window.open(deployUrl.value, '_blank')
    }
  }

  return {
    deploying,
    downloading,
    deployModalVisible,
    deployUrl,
    downloadCode,
    deployApp,
    deleteApp,
    openDeployedSite,
  }
}
