import request from '@/utils/request'

export function getLogList(params) {
  return request({
    url: '/admin/logs',
    method: 'get',
    params
  })
}
