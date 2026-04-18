import request from '@/utils/request'

export function publishJob(data) {
  return request({
    url: '/company/job/publish',
    method: 'post',
    data
  })
}

export function updateJob(data) {
  return request({
    url: '/company/job/update',
    method: 'put',
    data
  })
}

export function deleteJob(id) {
  return request({
    url: `/company/job/${id}`,
    method: 'delete'
  })
}

export function toggleJobStatus(id) {
  return request({
    url: `/company/job/toggle/${id}`,
    method: 'put'
  })
}

export function getCompanyJobs(params) {
  return request({
    url: '/company/job/list',
    method: 'get',
    params
  })
}

export function getJobDetail(id) {
  return request({
    url: `/company/job/detail/${id}`,
    method: 'get'
  })
}
