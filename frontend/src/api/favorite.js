import request from '@/utils/request'

export function addFavorite(data) {
  return request({
    url: '/favorite/add',
    method: 'post',
    data
  })
}

export function removeFavorite(jobId) {
  return request({
    url: `/favorite/remove/${jobId}`,
    method: 'delete'
  })
}

export function updateFavoriteRemark(id, remark) {
  return request({
    url: `/favorite/remark/${id}`,
    method: 'put',
    params: { remark }
  })
}

export function getFavoriteList(params) {
  return request({
    url: '/favorite/list',
    method: 'get',
    params
  })
}

export function checkFavorited(jobId) {
  return request({
    url: `/favorite/check/${jobId}`,
    method: 'get'
  })
}
