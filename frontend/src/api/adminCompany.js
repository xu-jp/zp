import request from '@/utils/request'

export function getCompanyList(params) {
  return request({
    url: '/admin/companies',
    method: 'get',
    params
  })
}

export function getCompanyDetail(id) {
  return request({
    url: `/admin/companies/${id}`,
    method: 'get'
  })
}

export function auditCompany(data) {
  return request({
    url: '/admin/companies/audit',
    method: 'put',
    data
  })
}

export function toggleRecruitPermission(companyId, enabled) {
  return request({
    url: `/admin/companies/permission/${companyId}`,
    method: 'put',
    params: { enabled }
  })
}
