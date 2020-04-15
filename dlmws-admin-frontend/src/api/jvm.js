import request from '@/utils/request'

export function list(params) {
  return request({
    url: '/jvm/list',
    method: 'get',
    params
  })
}
