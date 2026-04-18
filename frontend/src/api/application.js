import request from '@/utils/request'

export function applyJob(data) {
  return request({
    url: '/application/apply',
    method: 'post',
    data
  })
}

export function getApplicationList(params) {
  return request({
    url: '/application/list',
    method: 'get',
    params
  })
}

export function getApplicationDetail(id) {
  return request({
    url: `/application/detail/${id}`,
    method: 'get'
  })
}

export function cancelApplication(id) {
  return request({
    url: `/application/cancel/${id}`,
    method: 'delete'
  })
}

export function checkApplied(jobId) {
  return request({
    url: `/application/check/${jobId}`,
    method: 'get'
  })
}
