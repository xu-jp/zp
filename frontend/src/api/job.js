import request from '@/utils/request'

export function getJobList(params) {
  return request({
    url: '/job/list',
    method: 'get',
    params
  })
}

export function getJobDetail(id) {
  return request({
    url: `/job/detail/${id}`,
    method: 'get'
  })
}
