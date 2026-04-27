import request from '@/utils/request'

export function getRecommendJobs(limit = 6) {
  return request({
    url: '/recommend/jobs',
    method: 'get',
    params: { limit },
    timeout: 60000
  })
}
