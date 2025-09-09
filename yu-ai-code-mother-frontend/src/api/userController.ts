// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /user/add */
export async function addUser(body: API.UserAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/user/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/delete */
export async function deleteUser(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/get */
export async function getUserById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUser>('/user/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/get/login */
export async function getLoginUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>('/user/get/login', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/get/vo */
export async function getUserVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserVO>('/user/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/list/page/vo */
export async function listUserVoByPage(
  body: API.UserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUserVO>('/user/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/login */
export async function userLogin(body: API.UserLoginRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLoginUserVO>('/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/logout */
export async function userLogout(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/logout', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/register */
export async function userRegister(
  body: API.UserRegisterRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>('/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/update */
export async function updateUser(body: API.UserUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
