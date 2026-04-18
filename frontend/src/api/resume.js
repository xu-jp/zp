import request from '@/utils/request'

export function getResumeList() {
  return request({
    url: '/resume/list',
    method: 'get'
  })
}

export function getResumeDetail(id) {
  return request({
    url: `/resume/detail/${id}`,
    method: 'get'
  })
}

export function createResume(data) {
  return request({
    url: '/resume/create',
    method: 'post',
    data
  })
}

export function updateResume(data) {
  return request({
    url: '/resume/update',
    method: 'put',
    data
  })
}

export function deleteResume(id) {
  return request({
    url: `/resume/delete/${id}`,
    method: 'delete'
  })
}

export function setDefaultResume(id) {
  return request({
    url: `/resume/set-default/${id}`,
    method: 'put'
  })
}

export function optimizeResume(id) {
  return request({
    url: `/resume/optimize/${id}`,
    method: 'post'
  })
}

export function optimizeResumeStream(id, onMessage, onComplete, onError) {
  const token = localStorage.getItem('token')
  const baseURL = import.meta.env.VITE_API_BASE_URL || ''
  const url = `${baseURL}/api/resume/optimize-stream/${id}`
  let completed = false
  let buffer = ''
  let messageCount = 0
  
  console.log('[SSE] 开始连接:', url)
  
  fetch(url, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Accept': 'text/event-stream'
    }
  })
    .then(response => {
      console.log('[SSE] 连接成功, status:', response.status)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const reader = response.body.getReader()
      const decoder = new TextDecoder()
      let fullContent = ''
      
      function read() {
        reader.read().then(({ done, value }) => {
          if (done) {
            console.log('[SSE] 流结束, fullContent长度:', fullContent.length, 'buffer长度:', buffer.length)
            if (!completed) {
              completed = true
              if (fullContent.trim()) {
                console.log('[SSE] 调用onComplete, 内容长度:', fullContent.length)
                onComplete(fullContent)
              } else if (buffer.trim()) {
                console.log('[SSE] 调用onComplete (使用buffer), 内容长度:', buffer.length)
                onComplete(buffer)
              } else {
                console.warn('[SSE] 完成时没有内容')
                if (onError) onError('没有收到有效内容')
              }
            }
            return
          }
          
          const chunk = decoder.decode(value, { stream: true })
          buffer += chunk
          
          const lines = buffer.split('\n')
          buffer = lines.pop()
          
          for (const line of lines) {
            const trimmedLine = line.trim()
            if (!trimmedLine) continue
            
            if (trimmedLine.startsWith('data:')) {
              const data = trimmedLine.substring(5).trim()
              messageCount++
              console.log('[SSE] 收到第', messageCount, '条消息:', data.substring(0, 50) + '...')
              
              if (data === '[DONE]') {
                console.log('[SSE] 收到[DONE]信号, fullContent长度:', fullContent.length)
                if (!completed) {
                  completed = true
                  onComplete(fullContent)
                }
                return
              }
              if (data) {
                fullContent += data
                if (!completed && onMessage) {
                  onMessage(data, fullContent)
                }
              }
            } else if (trimmedLine.startsWith('event:')) {
              const eventType = trimmedLine.substring(6).trim()
              console.log('[SSE] 收到事件:', eventType)
              if (eventType === 'complete') {
                console.log('[SSE] 收到complete事件, fullContent长度:', fullContent.length)
                if (!completed) {
                  completed = true
                  onComplete(fullContent)
                }
                return
              } else if (eventType === 'error') {
                console.error('[SSE] 收到error事件')
                if (!completed) {
                  completed = true
                  onError('流式输出失败')
                }
                return
              }
            }
          }
          
          read()
        }).catch(error => {
          console.error('[SSE] 读取错误:', error)
          if (!completed) {
            completed = true
            onError(error)
          }
        })
      }
      
      read()
    })
    .catch(error => {
      console.error('[SSE] 请求错误:', error)
      if (!completed) {
        completed = true
        onError(error)
      }
    })
}
