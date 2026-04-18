import request from '@/utils/request'

export function getApplicationList(params) {
  return request({
    url: '/company/application/list',
    method: 'get',
    params
  })
}

export function getApplicationDetail(id) {
  return request({
    url: `/company/application/detail/${id}`,
    method: 'get'
  })
}

export function handleApplication(data) {
  return request({
    url: '/company/application/handle',
    method: 'put',
    data
  })
}

export function createInterview(data) {
  return request({
    url: '/company/application/interview',
    method: 'post',
    data
  })
}

export function getStatistics() {
  return request({
    url: '/company/application/statistics',
    method: 'get'
  })
}
