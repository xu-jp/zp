import request from '@/utils/request'

export function getCompanyInterviewList(params) {
  return request({
    url: '/company/interviews',
    method: 'get',
    params
  })
}

export function getCompanyInterviewDetail(id) {
  return request({
    url: `/company/interviews/${id}`,
    method: 'get'
  })
}

export function updateCompanyInterview(data) {
  return request({
    url: '/company/interviews',
    method: 'put',
    data
  })
}

export function setInterviewResult(data) {
  return request({
    url: '/company/interviews/result',
    method: 'put',
    data
  })
}

export function completeInterview(id) {
  return request({
    url: `/company/interviews/complete/${id}`,
    method: 'put'
  })
}

export function cancelInterview(id) {
  return request({
    url: `/company/interviews/cancel/${id}`,
    method: 'put'
  })
}
