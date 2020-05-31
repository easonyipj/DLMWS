package com.yipingjian.dlmws.shrio;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.yipingjian.dlmws.entity.SysUser;
import com.yipingjian.dlmws.service.SysUserService;
import com.yipingjian.dlmws.util.JWTUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

public class ShrioRealm extends AuthorizingRealm {

    @Resource
    private SysUserService sysUserService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return true;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String)authenticationToken.getPrincipal();
        String username = JWTUtil.getUsername(token);
        SysUser sysUser = sysUserService.getUserByName(username);
        Map result = JWTUtil.verify(token, username, sysUser.getPassword());
        Exception exception = (Exception) result.get("exception");
        if (exception instanceof TokenExpiredException) {
            throw new AuthenticationException("Token已过期(Token expired.)！");
        } else {
            // TODO BugFix
            return new SimpleAuthenticationInfo(sysUser, sysUser.getPassword(), getName());
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }
}
