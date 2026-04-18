import request from '@/utils/request'

export function getRecommendJobs(limit = 5) {
  return request({
    url: '/recommend/jobs',
    method: 'get',
    params: { limit }
  })
}
