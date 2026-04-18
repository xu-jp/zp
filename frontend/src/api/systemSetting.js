import request from '@/utils/request'

export function getAllSettings() {
  return request({
    url: '/admin/settings',
    method: 'get'
  })
}

export function getSettingByKey(key) {
  return request({
    url: `/admin/settings/${key}`,
    method: 'get'
  })
}

export function updateSetting(data) {
  return request({
    url: '/admin/settings',
    method: 'put',
    data
  })
}

export function updateSettings(data) {
  return request({
    url: '/admin/settings/batch',
    method: 'put',
    data
  })
}
