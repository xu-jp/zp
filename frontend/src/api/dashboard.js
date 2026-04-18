import request from '@/utils/request'

export function getDashboardData() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

export function getDashboardDataByDateRange(startDate, endDate) {
  return request({
    url: '/admin/dashboard/range',
    method: 'get',
    params: { startDate, endDate }
  })
}
