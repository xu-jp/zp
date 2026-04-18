import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function sendCode(data) {
  return request({
    url: '/auth/send-code',
    method: 'post',
    data
  })
}

export function resetPassword(data) {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data
  })
}

export function checkUsername(username) {
  return request({
    url: '/auth/check-username',
    method: 'get',
    params: { username }
  })
}

export function checkPhone(phone) {
  return request({
    url: '/auth/check-phone',
    method: 'get',
    params: { phone }
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function checkEmail(email) {
  return request({
    url: '/auth/check-email',
    method: 'get',
    params: { email }
  })
}
