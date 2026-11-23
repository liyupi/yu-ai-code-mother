import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUser } from '@/api/userController'

const DEFAULT_LOGIN_USER: API.LoginUserVO = {
  userName: '未登录',
}

export const useLoginUserStore = defineStore('loginUser', () => {
  const loginUser = ref<API.LoginUserVO>({ ...DEFAULT_LOGIN_USER })

  async function fetchLoginUser() {
    const res = await getLoginUser()
    if (res.data.code === 0 && res.data.data) {
      loginUser.value = res.data.data
    }
  }

  function setLoginUser(newLoginUser: Partial<API.LoginUserVO>) {
    loginUser.value = { ...DEFAULT_LOGIN_USER, ...newLoginUser }
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})
