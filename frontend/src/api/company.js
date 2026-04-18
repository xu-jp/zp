import request from '@/utils/request'

export function getCompanyInfo() {
  return request({
    url: '/company/info',
    method: 'get'
  })
}

export function saveCompanyInfo(data) {
  return request({
    url: '/company/save',
    method: 'post',
    data
  })
}

export function submitAudit() {
  return request({
    url: '/company/submit-audit',
    method: 'post'
  })
}
