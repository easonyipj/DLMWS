import request from '@/utils/request'

export function search(params) {
  return request({
    url: '/tomcat/search',
    method: 'get',
    params
  })
}

export function logCount(params) {
  return request({
    url: '/tomcat/logCount/project/min',
    method: 'get',
    params
  })
}

export function levelCount(params) {
  return request({
    url: '/tomcat/logCount/level/project',
    method: 'get',
    params
  })
}

export function topCount(params) {
  return request({
    url: '/tomcat/logCount/top',
    method: 'get',
    params
  })
}
