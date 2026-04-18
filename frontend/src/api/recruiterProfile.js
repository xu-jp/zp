import request from '@/utils/request'

export function getRecruiterProfile() {
  return request({
    url: '/recruiter/profile',
    method: 'get'
  })
}

export function updateRecruiterProfile(data) {
  return request({
    url: '/recruiter/profile',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/recruiter/password',
    method: 'put',
    data
  })
}

export function bindPhone(phone) {
  return request({
    url: '/recruiter/bind-phone',
    method: 'put',
    params: { phone }
  })
}

export function bindEmail(email) {
  return request({
    url: '/recruiter/bind-email',
    method: 'put',
    params: { email }
  })
}
