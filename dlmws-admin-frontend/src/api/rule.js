import request from '@/utils/request'

export function list(params) {
  return request({
    url: '/rule/getByOwner',
    method: 'get',
    params
  })
}

