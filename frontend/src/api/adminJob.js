import request from '@/utils/request'

export function getJobList(params) {
  return request({
    url: '/admin/jobs',
    method: 'get',
    params
  })
}

export function getJobDetail(id) {
  return request({
    url: `/admin/jobs/${id}`,
    method: 'get'
  })
}

export function auditJobs(data) {
  return request({
    url: '/admin/jobs/audit',
    method: 'put',
    data
  })
}

export function offlineJob(id) {
  return request({
    url: `/admin/jobs/offline/${id}`,
    method: 'put'
  })
}
