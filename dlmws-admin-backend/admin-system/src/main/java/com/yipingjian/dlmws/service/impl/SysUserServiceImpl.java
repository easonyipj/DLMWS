package com.yipingjian.dlmws.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yipingjian.dlmws.entity.SysUser;
import com.yipingjian.dlmws.mapper.SysUserMapper;
import com.yipingjian.dlmws.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public SysUser getUserByName(String username) {
        return this.getOne(new QueryWrapper<SysUser>().eq("username", username));
    }

    @Override
    public void insertUser(SysUser user) {
        this.save(user);
    }
}
