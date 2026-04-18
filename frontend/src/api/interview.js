import request from '@/utils/request'

export function getInterviewList(params) {
  return request({
    url: '/interview/list',
    method: 'get',
    params
  })
}

export function getInterviewDetail(id) {
  return request({
    url: `/interview/detail/${id}`,
    method: 'get'
  })
}

export function updateInterviewStatus(data) {
  return request({
    url: '/interview/status',
    method: 'put',
    data
  })
}
