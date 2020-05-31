package com.yipingjian.dlmws.controller;

import com.yipingjian.dlmws.entity.LoginVo;
import com.yipingjian.dlmws.entity.RegisterVo;
import com.yipingjian.dlmws.entity.SysUser;
import com.yipingjian.dlmws.service.SysUserService;
import com.yipingjian.dlmws.util.JWTUtil;
import com.yipingjian.dlmws.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/sys")
public class SysController {

    @Resource
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Response login(@RequestBody LoginVo loginVo) {
        SysUser user = sysUserService.getUserByName(loginVo.getUsername());
        SimpleHash simpleHash = new SimpleHash("MD5", loginVo.getPassword(),  user.getSalt(), 1024);
        if(!simpleHash.toHex().equals(user.getPassword())){
            return Response.error("error password");
        }
        String token = JWTUtil.sign(loginVo.getUsername(), loginVo.getPassword());
        return Response.ok().put("token", token);
    }

    @PostMapping("/register")
    public Response register(@RequestBody SysUser sysUser) {
        // TODO 检查用户是否重复注册
        String salt = UUID.randomUUID().toString().replaceAll("-","");
        SimpleHash simpleHash = new SimpleHash("MD5", sysUser.getPassword(), salt, 1024);
        sysUser.setPassword(simpleHash.toHex());
        sysUser.setCreateTime(new Date(System.currentTimeMillis()));
        sysUser.setRole("admin");
        sysUser.setSalt(salt);
        sysUserService.insertUser(sysUser);
        return Response.ok();
    }

}
