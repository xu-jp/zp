import request from '@/utils/request'

export function screenResume(data) {
  return request({
    url: '/company/ai/screen',
    method: 'post',
    data
  })
}

export function batchScreenResumes(jobId, data) {
  return request({
    url: `/company/ai/batch-screen/${jobId}`,
    method: 'post',
    data
  })
}
