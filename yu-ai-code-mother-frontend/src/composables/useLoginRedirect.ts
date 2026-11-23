import { useRoute } from 'vue-router'

/**
 * 获取登录后的重定向路径
 */
export function useLoginRedirect() {
  const route = useRoute()

  const getRedirectPath = (): string => {
    const redirect = route.query.redirect
    if (typeof redirect === 'string' && redirect.startsWith('/')) {
      return redirect
    }
    return '/'
  }

  return { getRedirectPath }
}
