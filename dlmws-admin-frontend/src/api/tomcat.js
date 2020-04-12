import request from '@/utils/request'

export function search(params) {
  return request({
    url: '/tomcat/search',
    method: 'get',
    params
  })
}
