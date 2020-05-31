package com.yipingjian.dlmws.service;

import com.yipingjian.dlmws.entity.SysUser;

public interface SysUserService {
    SysUser getUserByName(String username);
    void insertUser(SysUser user);
}
