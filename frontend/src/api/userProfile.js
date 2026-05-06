import request from '@/utils/request'

export function getUserProfile() {
  return request({
    url: '/user/profile',
    method: 'get'
  })
}

export function updateUserProfile(data) {
  return request({
    url: '/user/profile',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

export function bindPhone(phone) {
  return request({
    url: '/user/bind-phone',
    method: 'put',
    params: { phone }
  })
}

export function bindEmail(email) {
  return request({
    url: '/user/bind-email',
    method: 'put',
    params: { email }
  })
}

export function getUserStats() {
  return request({
    url: '/user/stats',
    method: 'get'
  })
}
