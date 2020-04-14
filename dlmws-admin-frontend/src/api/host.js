import request from '@/utils/request'

export function list(params) {
  return request({
    url: '/host/list',
    method: 'get',
    params
  })
}

