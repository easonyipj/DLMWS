import request from '@/utils/request'

export function list(params) {
  return request({
    url: '/warn/list',
    method: 'get',
    params
  })
}
