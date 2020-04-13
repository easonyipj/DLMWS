import request from '@/utils/request'

export function search(params) {
  return request({
    url: '/tomcat/search',
    method: 'get',
    params
  })
}

export function logCount(data) {
  return request({
    url: '/tomcat/logCount/project/min',
    method: 'post',
    data
  })
}
