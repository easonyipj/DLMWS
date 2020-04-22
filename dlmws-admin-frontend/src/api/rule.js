import request from '@/utils/request'
import axios from 'axios'

export function list(params) {
  return request({
    url: '/rule/getByOwner',
    method: 'get',
    params
  })
}

